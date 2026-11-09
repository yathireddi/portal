package com.scholarconnect.repository;

import com.scholarconnect.entity.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {

    List<Scholarship> findByStatus(String status);

    List<Scholarship> findByStatusOrderByApplicationDeadlineAsc(String status);

    long countByStatus(String status);

    @Query("SELECT s FROM Scholarship s WHERE s.status = 'ACTIVE' AND (" +
           "LOWER(s.scholarshipName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.provider) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.eligibleCategories) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.educationLevel) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Scholarship> searchActiveScholarships(@Param("query") String query);

    @Query("SELECT s FROM Scholarship s WHERE s.status = 'ACTIVE' " +
           "AND (:category IS NULL OR :category = '' OR :category = 'ALL' OR LOWER(s.eligibleCategories) LIKE LOWER(CONCAT('%', :category, '%')) OR s.eligibleCategories = 'ALL') " +
           "AND (:state IS NULL OR :state = '' OR :state = 'ALL' OR LOWER(s.eligibleStates) LIKE LOWER(CONCAT('%', :state, '%')) OR s.eligibleStates = 'ALL') " +
           "AND (:level IS NULL OR :level = '' OR :level = 'ALL' OR LOWER(s.educationLevel) = LOWER(:level) OR s.educationLevel = 'ALL') " +
           "ORDER BY s.applicationDeadline ASC")
    List<Scholarship> filterScholarships(@Param("category") String category,
                                         @Param("state") String state,
                                         @Param("level") String level);
}
