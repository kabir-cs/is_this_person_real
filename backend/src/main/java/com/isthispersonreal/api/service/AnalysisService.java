package com.isthispersonreal.api.service;

import com.isthispersonreal.api.model.AnalysisResult;
import com.isthispersonreal.api.model.AnalysisQueue;
import com.isthispersonreal.api.model.User;
import com.isthispersonreal.api.repository.AnalysisResultRepository;
import com.isthispersonreal.api.repository.AnalysisQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AnalysisService {
    
    @Autowired
    private AnalysisResultRepository analysisResultRepository;
    
    @Autowired
    private AnalysisQueueRepository analysisQueueRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Value("${ml-service.url}")
    private String mlServiceUrl;
    
    @Value("${ml-service.timeout}")
    private int mlServiceTimeout;
    
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
            .build();
    
    public AnalysisResult analyzeImage(MultipartFile file, String username) throws IOException {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String imageHash = calculateImageHash(file);
        
        // Check if we already have results for this image
        Optional<AnalysisResult> existingResult = analysisResultRepository.findByImageHash(imageHash);
        if (existingResult.isPresent()) {
            return existingResult.get();
        }
        
        // Check if this image is already in the queue
        Optional<AnalysisQueue> existingQueue = analysisQueueRepository.findByImageHash(imageHash);
        if (existingQueue.isPresent()) {
            throw new RuntimeException("Analysis already in progress for this image");
        }
        
        // Create queue entry
        AnalysisQueue queueEntry = new AnalysisQueue(user, imageHash, file.getOriginalFilename(), 
                                                   file.getSize(), file.getContentType());
        analysisQueueRepository.save(queueEntry);
        
        // Process immediately (in production, this would be async)
        return processAnalysis(file, user, imageHash);
    }
    
    @Cacheable(value = "analysisResults", key = "#imageHash")
    public Optional<AnalysisResult> getAnalysisResult(String imageHash) {
        return analysisResultRepository.findByImageHash(imageHash);
    }
    
    public AnalysisResult processAnalysis(MultipartFile file, User user, String imageHash) throws IOException {
        long startTime = System.currentTimeMillis();
        
        try {
            // Call Python ML service
            Map<String, Object> mlResult = callMLService(file);
            
            // Extract results
            String label = (String) mlResult.get("label");
            Double confidence = (Double) mlResult.get("confidence");
            @SuppressWarnings("unchecked")
            Map<String, Double> scores = (Map<String, Double>) mlResult.get("scores");
            
            // Convert label to enum
            AnalysisResult.DetectionLabel detectionLabel = convertLabel(label);
            
            // Create analysis result
            AnalysisResult result = new AnalysisResult(user, imageHash, file.getOriginalFilename(), 
                                                     detectionLabel, confidence);
            result.setFileSize(file.getSize());
            result.setMimeType(file.getContentType());
            result.setScores(scores);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            result.setMlModelVersion((String) mlResult.get("model_version"));
            
            // Get OpenAI analysis
            String openaiAnalysis = openAIService.analyzeImageResult(result);
            result.setOpenaiAnalysis(openaiAnalysis);
            
            // Save result
            result = analysisResultRepository.save(result);
            
            // Update queue status
            updateQueueStatus(imageHash, AnalysisQueue.QueueStatus.COMPLETED);
            
            return result;
            
        } catch (Exception e) {
            updateQueueStatus(imageHash, AnalysisQueue.QueueStatus.FAILED, e.getMessage());
            throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> callMLService(MultipartFile file) throws IOException {
        return webClient.post()
                .uri(mlServiceUrl + "/analyze")
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(java.time.Duration.ofMillis(mlServiceTimeout))
                .block();
    }
    
    private String calculateImageHash(MultipartFile file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    private AnalysisResult.DetectionLabel convertLabel(String label) {
        if ("AI".equalsIgnoreCase(label) || "ai_generated".equalsIgnoreCase(label)) {
            return AnalysisResult.DetectionLabel.AI_GENERATED;
        } else if ("REAL".equalsIgnoreCase(label) || "real".equalsIgnoreCase(label)) {
            return AnalysisResult.DetectionLabel.REAL;
        } else {
            return AnalysisResult.DetectionLabel.UNCERTAIN;
        }
    }
    
    private void updateQueueStatus(String imageHash, AnalysisQueue.QueueStatus status) {
        updateQueueStatus(imageHash, status, null);
    }
    
    private void updateQueueStatus(String imageHash, AnalysisQueue.QueueStatus status, String errorMessage) {
        Optional<AnalysisQueue> queueOpt = analysisQueueRepository.findByImageHash(imageHash);
        if (queueOpt.isPresent()) {
            AnalysisQueue queue = queueOpt.get();
            queue.setStatus(status);
            if (status == AnalysisQueue.QueueStatus.PROCESSING) {
                queue.setStartedAt(LocalDateTime.now());
            } else if (status == AnalysisQueue.QueueStatus.COMPLETED) {
                queue.setCompletedAt(LocalDateTime.now());
            } else if (status == AnalysisQueue.QueueStatus.FAILED) {
                queue.setErrorMessage(errorMessage);
            }
            analysisQueueRepository.save(queue);
        }
    }
    
    public Map<String, Object> getAnalysisStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_analyses", analysisResultRepository.count());
        stats.put("real_count", analysisResultRepository.countByDetectionLabel(AnalysisResult.DetectionLabel.REAL));
        stats.put("ai_generated_count", analysisResultRepository.countByDetectionLabel(AnalysisResult.DetectionLabel.AI_GENERATED));
        stats.put("uncertain_count", analysisResultRepository.countByDetectionLabel(AnalysisResult.DetectionLabel.UNCERTAIN));
        
        // Queue stats
        stats.put("pending_jobs", analysisQueueRepository.countByStatus(AnalysisQueue.QueueStatus.PENDING));
        stats.put("processing_jobs", analysisQueueRepository.countByStatus(AnalysisQueue.QueueStatus.PROCESSING));
        stats.put("completed_jobs", analysisQueueRepository.countByStatus(AnalysisQueue.QueueStatus.COMPLETED));
        stats.put("failed_jobs", analysisQueueRepository.countByStatus(AnalysisQueue.QueueStatus.FAILED));
        
        return stats;
    }
} 