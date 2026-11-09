package com.scholarconnect.repository;

import com.scholarconnect.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByApplicationIdOrderByReviewedAtDesc(Long applicationId);
    List<Review> findByReviewerIdOrderByReviewedAtDesc(Long reviewerId);
}
