package com.isthispersonreal.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "image_hash")
    private String imageHash;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "detection_label")
    private DetectionLabel detectionLabel;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @ElementCollection
    @CollectionTable(name = "analysis_scores", 
        joinColumns = @JoinColumn(name = "analysis_result_id"))
    @MapKeyColumn(name = "score_type")
    @Column(name = "score_value")
    private Map<String, Double> scores;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "ml_model_version")
    private String mlModelVersion;
    
    @Column(name = "openai_analysis")
    @Lob
    private String openaiAnalysis;
    
    @Column(name = "social_media_analysis")
    @Lob
    private String socialMediaAnalysis;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public AnalysisResult() {}
    
    public AnalysisResult(User user, String imageHash, String fileName, 
                         DetectionLabel detectionLabel, Double confidenceScore) {
        this.user = user;
        this.imageHash = imageHash;
        this.fileName = fileName;
        this.detectionLabel = detectionLabel;
        this.confidenceScore = confidenceScore;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getImageHash() {
        return imageHash;
    }
    
    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public DetectionLabel getDetectionLabel() {
        return detectionLabel;
    }
    
    public void setDetectionLabel(DetectionLabel detectionLabel) {
        this.detectionLabel = detectionLabel;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public Map<String, Double> getScores() {
        return scores;
    }
    
    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }
    
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public String getMlModelVersion() {
        return mlModelVersion;
    }
    
    public void setMlModelVersion(String mlModelVersion) {
        this.mlModelVersion = mlModelVersion;
    }
    
    public String getOpenaiAnalysis() {
        return openaiAnalysis;
    }
    
    public void setOpenaiAnalysis(String openaiAnalysis) {
        this.openaiAnalysis = openaiAnalysis;
    }
    
    public String getSocialMediaAnalysis() {
        return socialMediaAnalysis;
    }
    
    public void setSocialMediaAnalysis(String socialMediaAnalysis) {
        this.socialMediaAnalysis = socialMediaAnalysis;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public enum DetectionLabel {
        REAL, AI_GENERATED, UNCERTAIN
    }
} 