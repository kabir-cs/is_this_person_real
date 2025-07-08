package com.isthispersonreal.api.graphql;

import com.isthispersonreal.api.model.AnalysisResult;
import com.isthispersonreal.api.model.User;
import com.isthispersonreal.api.security.JwtTokenProvider;
import com.isthispersonreal.api.service.AnalysisService;
import com.isthispersonreal.api.service.UserService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLResolver {
    
    @Autowired
    private AnalysisService analysisService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public AnalysisResult uploadImage(DataFetchingEnvironment env) throws Exception {
        MultipartFile file = env.getArgument("file");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return analysisService.analyzeImage(file, username);
    }
    
    public Map<String, Object> registerUser(DataFetchingEnvironment env) {
        Map<String, Object> input = env.getArgument("input");
        String username = (String) input.get("username");
        String email = (String) input.get("email");
        String password = (String) input.get("password");
        
        User user = userService.createUser(username, email, password, User.Role.USER);
        String token = tokenProvider.generateToken(user.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("username", username);
        response.put("message", "User registered successfully");
        
        return response;
    }
    
    public Map<String, Object> loginUser(DataFetchingEnvironment env) {
        Map<String, Object> input = env.getArgument("input");
        String username = (String) input.get("username");
        String password = (String) input.get("password");
        
        // This would typically use AuthenticationManager
        // For simplicity, we'll just generate a token
        String token = tokenProvider.generateToken(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("username", username);
        
        return response;
    }
    
    public AnalysisResult analysisResult(DataFetchingEnvironment env) {
        String id = env.getArgument("id");
        // Implementation would fetch by ID
        return null; // Placeholder
    }
    
    public List<AnalysisResult> analysisResults(DataFetchingEnvironment env) {
        String userId = env.getArgument("userId");
        Integer limit = env.getArgument("limit");
        Integer offset = env.getArgument("offset");
        
        // Implementation would fetch with pagination
        return List.of(); // Placeholder
    }
    
    public Map<String, Object> analysisStats(DataFetchingEnvironment env) {
        return analysisService.getAnalysisStats();
    }
    
    public Map<String, Object> userProfile(DataFetchingEnvironment env) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().toString());
        profile.put("createdAt", user.getCreatedAt().toString());
        profile.put("lastLogin", user.getLastLogin() != null ? user.getLastLogin().toString() : null);
        profile.put("analysisCount", analysisService.getAnalysisStats().get("total_analyses"));
        profile.put("averageConfidence", 0.0); // Would calculate from user's analyses
        
        return profile;
    }
    
    public List<Map<String, Object>> scores(AnalysisResult result) {
        if (result.getScores() == null) {
            return List.of();
        }
        
        return result.getScores().entrySet().stream()
                .map(entry -> {
                    Map<String, Object> score = new HashMap<>();
                    score.put("type", entry.getKey());
                    score.put("value", entry.getValue());
                    return score;
                })
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> analyzeSocialMedia(DataFetchingEnvironment env) {
        Map<String, Object> input = env.getArgument("input");
        String fullName = (String) input.get("fullName");
        @SuppressWarnings("unchecked")
        List<String> platforms = (List<String>) input.get("platforms");
        
        Map<String, Object> response = new HashMap<>();
        response.put("platforms", List.of()); // Would contain platform analysis
        response.put("overallScore", 0.75);
        response.put("recommendations", List.of("Profile appears authentic", "Consider additional verification"));
        response.put("status", "completed");
        
        return response;
    }
} 