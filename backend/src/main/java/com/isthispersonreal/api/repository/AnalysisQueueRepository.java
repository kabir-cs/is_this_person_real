package com.isthispersonreal.api.repository;

import com.isthispersonreal.api.model.AnalysisQueue;
import com.isthispersonreal.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisQueueRepository extends JpaRepository<AnalysisQueue, Long> {
    
    List<AnalysisQueue> findByStatusOrderByPriorityDescCreatedAtAsc(AnalysisQueue.QueueStatus status);
    
    Page<AnalysisQueue> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Optional<AnalysisQueue> findByImageHash(String imageHash);
    
    @Query("SELECT aq FROM AnalysisQueue aq WHERE aq.status = 'PENDING' AND aq.retryCount < aq.maxRetries ORDER BY aq.priority DESC, aq.createdAt ASC")
    List<AnalysisQueue> findPendingJobs();
    
    @Query("SELECT aq FROM AnalysisQueue aq WHERE aq.status = 'PROCESSING' AND aq.startedAt < :timeout")
    List<AnalysisQueue> findStuckJobs(@Param("timeout") LocalDateTime timeout);
    
    @Query("SELECT COUNT(aq) FROM AnalysisQueue aq WHERE aq.status = :status")
    long countByStatus(@Param("status") AnalysisQueue.QueueStatus status);
    
    @Query("SELECT COUNT(aq) FROM AnalysisQueue aq WHERE aq.user.id = :userId AND aq.status = 'PENDING'")
    long countPendingByUserId(@Param("userId") Long userId);
    
    @Query("SELECT aq FROM AnalysisQueue aq WHERE aq.user.id = :userId AND aq.status IN ('PENDING', 'PROCESSING')")
    List<AnalysisQueue> findActiveJobsByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE AnalysisQueue aq SET aq.status = 'FAILED', aq.errorMessage = :errorMessage WHERE aq.id = :id")
    void markAsFailed(@Param("id") Long id, @Param("errorMessage") String errorMessage);
    
    @Modifying
    @Query("UPDATE AnalysisQueue aq SET aq.retryCount = aq.retryCount + 1, aq.status = 'PENDING' WHERE aq.id = :id")
    void incrementRetryCount(@Param("id") Long id);
    
    @Query("SELECT aq FROM AnalysisQueue aq WHERE aq.createdAt >= :since ORDER BY aq.createdAt DESC")
    List<AnalysisQueue> findRecentJobs(@Param("since") LocalDateTime since);
    
    @Query("SELECT aq.status, COUNT(aq) FROM AnalysisQueue aq GROUP BY aq.status")
    List<Object[]> getQueueStats();
    
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, aq.createdAt, aq.completedAt)) FROM AnalysisQueue aq WHERE aq.status = 'COMPLETED' AND aq.completedAt IS NOT NULL")
    Double getAverageProcessingTime();
} 