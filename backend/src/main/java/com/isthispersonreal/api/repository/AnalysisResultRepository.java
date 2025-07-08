package com.isthispersonreal.api.repository;

import com.isthispersonreal.api.model.AnalysisResult;
import com.isthispersonreal.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    
    Page<AnalysisResult> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Optional<AnalysisResult> findByImageHash(String imageHash);
    
    boolean existsByImageHash(String imageHash);
    
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.user.id = :userId AND ar.createdAt >= :since")
    List<AnalysisResult> findByUserIdAndCreatedAfter(@Param("userId") Long userId, 
                                                    @Param("since") LocalDateTime since);
    
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.detectionLabel = :label")
    Page<AnalysisResult> findByDetectionLabel(@Param("label") AnalysisResult.DetectionLabel label, 
                                             Pageable pageable);
    
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar WHERE ar.detectionLabel = :label")
    long countByDetectionLabel(@Param("label") AnalysisResult.DetectionLabel label);
    
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar WHERE ar.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(ar.confidenceScore) FROM AnalysisResult ar WHERE ar.user.id = :userId")
    Double getAverageConfidenceByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.confidenceScore >= :minConfidence ORDER BY ar.confidenceScore DESC")
    List<AnalysisResult> findByHighConfidence(@Param("minConfidence") Double minConfidence);
    
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.createdAt BETWEEN :startDate AND :endDate")
    List<AnalysisResult> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ar.detectionLabel, COUNT(ar) FROM AnalysisResult ar GROUP BY ar.detectionLabel")
    List<Object[]> getDetectionLabelStats();
    
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.processingTimeMs > :threshold ORDER BY ar.processingTimeMs DESC")
    List<AnalysisResult> findSlowProcessingResults(@Param("threshold") Long threshold);
} 