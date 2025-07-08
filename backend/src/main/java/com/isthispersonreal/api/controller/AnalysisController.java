package com.isthispersonreal.api.controller;

import com.isthispersonreal.api.model.AnalysisResult;
import com.isthispersonreal.api.security.JwtTokenProvider;
import com.isthispersonreal.api.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    @Autowired
    private AnalysisService analysisService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Validate file
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (!isValidImageFile(file)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please upload a valid image file (JPEG, PNG, GIF)");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Process analysis
            AnalysisResult result = analysisService.analyzeImage(file, username);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("id", result.getId());
            response.put("label", result.getDetectionLabel().toString());
            response.put("confidence", result.getConfidenceScore());
            response.put("scores", result.getScores());
            response.put("processingTime", result.getProcessingTimeMs());
            response.put("modelVersion", result.getMlModelVersion());
            response.put("openaiAnalysis", result.getOpenaiAnalysis());
            response.put("createdAt", result.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/result/{imageHash}")
    public ResponseEntity<?> getAnalysisResult(@PathVariable String imageHash) {
        try {
            var result = analysisService.getAnalysisResult(imageHash);
            
            if (result.isPresent()) {
                AnalysisResult analysisResult = result.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", analysisResult.getId());
                response.put("label", analysisResult.getDetectionLabel().toString());
                response.put("confidence", analysisResult.getConfidenceScore());
                response.put("scores", analysisResult.getScores());
                response.put("processingTime", analysisResult.getProcessingTimeMs());
                response.put("modelVersion", analysisResult.getMlModelVersion());
                response.put("openaiAnalysis", analysisResult.getOpenaiAnalysis());
                response.put("createdAt", analysisResult.getCreatedAt());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Analysis result not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve analysis result: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getAnalysisStats() {
        try {
            Map<String, Object> stats = analysisService.getAnalysisStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/social-media")
    public ResponseEntity<?> analyzeSocialMediaProfile(@RequestBody SocialMediaRequest request) {
        try {
            // This would integrate with social media scraping service
            // For now, return a placeholder response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Social media analysis feature coming soon");
            response.put("platforms", new String[]{"LinkedIn", "X", "Instagram", "Facebook"});
            response.put("status", "not_implemented");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Social media analysis failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif");
    }
    
    public static class SocialMediaRequest {
        private String fullName;
        private String[] platforms;
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String[] getPlatforms() { return platforms; }
        public void setPlatforms(String[] platforms) { this.platforms = platforms; }
    }
} 