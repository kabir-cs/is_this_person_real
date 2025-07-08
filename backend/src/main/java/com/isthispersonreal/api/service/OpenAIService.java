package com.isthispersonreal.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.isthispersonreal.api.model.AnalysisResult;

import java.util.Map;

@Service
public class OpenAIService {
    
    @Value("${openai.api-key}")
    private String apiKey;
    
    @Value("${openai.model}")
    private String model;
    
    @Value("${openai.max-tokens}")
    private int maxTokens;
    
    @Value("${openai.temperature}")
    private double temperature;
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
    
    public String analyzeImageResult(AnalysisResult result) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "OpenAI analysis not available - API key not configured";
        }
        
        try {
            String prompt = buildAnalysisPrompt(result);
            
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", Map.of("role", "user", "content", prompt),
                "max_tokens", maxTokens,
                "temperature", temperature
            );
            
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> choices = (java.util.List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return (String) message.get("content");
                }
            }
            
            return "Unable to generate OpenAI analysis";
            
        } catch (Exception e) {
            return "OpenAI analysis failed: " + e.getMessage();
        }
    }
    
    private String buildAnalysisPrompt(AnalysisResult result) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following AI face detection result and provide insights:\n\n");
        prompt.append("Detection Label: ").append(result.getDetectionLabel()).append("\n");
        prompt.append("Confidence Score: ").append(String.format("%.2f%%", result.getConfidenceScore() * 100)).append("\n");
        prompt.append("Processing Time: ").append(result.getProcessingTimeMs()).append("ms\n");
        
        if (result.getScores() != null && !result.getScores().isEmpty()) {
            prompt.append("Detailed Scores:\n");
            result.getScores().forEach((key, value) -> 
                prompt.append("- ").append(key).append(": ").append(String.format("%.2f%%", value * 100)).append("\n"));
        }
        
        prompt.append("\nPlease provide:\n");
        prompt.append("1. A brief explanation of what this result means\n");
        prompt.append("2. Factors that might have influenced the detection\n");
        prompt.append("3. Recommendations for users interpreting this result\n");
        prompt.append("4. Any limitations or considerations\n");
        
        return prompt.toString();
    }
    
    public String analyzeSocialMediaProfile(String profileData) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "OpenAI analysis not available - API key not configured";
        }
        
        try {
            String prompt = "Analyze the following social media profile data for authenticity indicators:\n\n" + profileData + 
                          "\n\nProvide a brief analysis focusing on:\n" +
                          "1. Consistency of information across platforms\n" +
                          "2. Activity patterns and engagement\n" +
                          "3. Profile completeness and quality\n" +
                          "4. Potential red flags for fake profiles\n" +
                          "5. Overall credibility assessment";
            
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", Map.of("role", "user", "content", prompt),
                "max_tokens", maxTokens,
                "temperature", temperature
            );
            
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> choices = (java.util.List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return (String) message.get("content");
                }
            }
            
            return "Unable to generate social media analysis";
            
        } catch (Exception e) {
            return "Social media analysis failed: " + e.getMessage();
        }
    }
} 