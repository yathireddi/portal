package com.scholarconnect.repository;

import com.scholarconnect.entity.Application;
import com.scholarconnect.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentIdOrderByApplicationDateDesc(Long studentId);

    List<Application> findByReviewerIdOrderByApplicationDateDesc(Long reviewerId);

    Optional<Application> findByApplicationReference(String applicationReference);

    boolean existsByStudentIdAndScholarshipIdAndStatusNot(Long studentId, Long scholarshipId, ApplicationStatus status);

    long countByStatus(ApplicationStatus status);

    long countByScholarshipId(Long scholarshipId);

    long countByReviewerId(Long reviewerId);

    long countByReviewerIdAndStatus(Long reviewerId, ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE " +
           "(:scholarshipId IS NULL OR a.scholarship.id = :scholarshipId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(a.applicationReference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.student.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.student.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.scholarship.scholarshipName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.applicationDate DESC")
    List<Application> searchApplications(@Param("scholarshipId") Long scholarshipId,
                                        @Param("status") ApplicationStatus status,
                                        @Param("search") String search);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.student.id = :studentId AND a.status = :status")
    long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") ApplicationStatus status);
}

