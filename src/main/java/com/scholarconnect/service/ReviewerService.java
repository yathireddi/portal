package com.scholarconnect.service;

import com.scholarconnect.dto.*;
import com.scholarconnect.entity.*;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.exception.UnauthorizedException;
import com.scholarconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewerService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final NotificationService notificationService;
    private final ApplicationService applicationService;

    public ReviewerService(UserRepository userRepository,
                           ApplicationRepository applicationRepository,
                           ReviewRepository reviewRepository,
                           DocumentRepository documentRepository,
                           ApplicationStatusHistoryRepository historyRepository,
                           NotificationService notificationService,
                           ApplicationService applicationService) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.reviewRepository = reviewRepository;
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.notificationService = notificationService;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public ReviewerDashboardDTO getDashboard(Long reviewerId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        ReviewerDashboardDTO dto = new ReviewerDashboardDTO();
        dto.setReviewerName(reviewer.getFullName());

        List<Application> assigned = applicationRepository.findByReviewerIdOrderByApplicationDateDesc(reviewerId);
        dto.setTotalAssigned(assigned.size());

        long pending = assigned.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.UNDER_REVIEW || a.getStatus() == ApplicationStatus.DOCUMENT_VERIFICATION)
                .count();
        dto.setPendingReviews(pending);

        long completed = assigned.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED || a.getStatus() == ApplicationStatus.REJECTED)
                .count();
        dto.setCompletedReviews(completed);

        dto.setAssignedApplications(assigned.stream().map(applicationService::mapToDTO).collect(Collectors.toList()));

        List<Review> recentReviews = reviewRepository.findByReviewerIdOrderByReviewedAtDesc(reviewerId);
        dto.setRecentReviews(recentReviews.stream().limit(5).map(r -> {
            ReviewDTO rd = new ReviewDTO();
            rd.setId(r.getId());
            rd.setApplicationId(r.getApplication().getId());
            rd.setReviewerId(r.getReviewer().getId());
            rd.setReviewerName(r.getReviewer().getFullName());
            rd.setReviewerEmail(r.getReviewer().getEmail());
            rd.setComments(r.getComments());
            rd.setRecommendation(r.getRecommendation());
            rd.setReviewedAt(r.getReviewedAt());
            return rd;
        }).collect(Collectors.toList()));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAssignedApplications(Long reviewerId) {
        return applicationRepository.findByReviewerIdOrderByApplicationDateDesc(reviewerId).stream()
                .map(applicationService::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO submitReview(Long applicationId, Long reviewerId, ReviewRequestDTO request) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        if (app.getReviewer() == null || !app.getReviewer().getId().equals(reviewerId)) {
            throw new UnauthorizedException("This application is not assigned to you for review");
        }

        Review review = new Review();
        review.setApplication(app);
        review.setReviewer(reviewer);
        review.setComments(request.getComments().trim());
        review.setRecommendation(request.getRecommendation().trim());
        Review savedReview = reviewRepository.save(review);

        // Update application status based on recommendation
        String prevStatus = app.getStatus().name();
        ApplicationStatus newStatus = app.getStatus();

        if ("RECOMMEND_APPROVAL".equalsIgnoreCase(request.getRecommendation())) {
            newStatus = ApplicationStatus.DOCUMENT_VERIFICATION;
        } else if ("REQUEST_MORE_INFO".equalsIgnoreCase(request.getRecommendation())) {
            newStatus = ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED;
        }

        app.setStatus(newStatus);
        app.setRemarks("Reviewer Remark: " + request.getComments().trim());
        applicationRepository.save(app);

        // Record history
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                app,
                prevStatus,
                newStatus.name(),
                reviewer.getEmail(),
                "Reviewer recommendation: " + request.getRecommendation() + ". Comments: " + request.getComments()
        );
        historyRepository.save(history);

        // Notify student
        notificationService.createNotification(
                app.getStudent(),
                "Review Completed: " + app.getApplicationReference(),
                "Reviewer " + reviewer.getFullName() + " has completed the initial review. " +
                        ("REQUEST_MORE_INFO".equalsIgnoreCase(request.getRecommendation()) ?
                                "Please take corrective action: " + request.getComments() :
                                "Application moved to verification stage.")
        );

        ReviewDTO rd = new ReviewDTO();
        rd.setId(savedReview.getId());
        rd.setApplicationId(app.getId());
        rd.setReviewerId(reviewer.getId());
        rd.setReviewerName(reviewer.getFullName());
        rd.setReviewerEmail(reviewer.getEmail());
        rd.setComments(savedReview.getComments());
        rd.setRecommendation(savedReview.getRecommendation());
        rd.setReviewedAt(savedReview.getReviewedAt());
        return rd;
    }

    @Transactional
    public DocumentDTO verifyDocument(Long documentId, String status, Long reviewerId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        Application app = document.getApplication();
        if (app.getReviewer() == null || !app.getReviewer().getId().equals(reviewerId)) {
            User user = userRepository.findById(reviewerId).orElseThrow();
            if (user.getRole() != Role.ROLE_ADMIN) {
                throw new UnauthorizedException("You are not assigned to review this document");
            }
        }

        String targetStatus = status.trim().toUpperCase();
        if (!"VERIFIED".equals(targetStatus) && !"REJECTED".equals(targetStatus) && !"PENDING".equals(targetStatus)) {
            throw new BadRequestException("Invalid document verification status: " + status);
        }

        document.setVerificationStatus(targetStatus);
        Document saved = documentRepository.save(document);

        DocumentDTO dto = new DocumentDTO();
        dto.setId(saved.getId());
        dto.setApplicationId(saved.getApplication().getId());
        dto.setDocumentType(saved.getDocumentType());
        dto.setOriginalFilename(saved.getOriginalFilename());
        dto.setStoredFilename(saved.getStoredFilename());
        dto.setFileUrl("/api/documents/" + saved.getId());
        dto.setContentType(saved.getContentType());
        dto.setFileSize(saved.getFileSize());
        dto.setUploadedAt(saved.getUploadedAt());
        dto.setVerificationStatus(saved.getVerificationStatus());
        return dto;
    }
}

