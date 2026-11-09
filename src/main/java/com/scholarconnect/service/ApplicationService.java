package com.scholarconnect.service;

import com.scholarconnect.dto.*;
import com.scholarconnect.entity.*;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.exception.UnauthorizedException;
import com.scholarconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final ReviewRepository reviewRepository;
    private final EligibilityService eligibilityService;
    private final NotificationService notificationService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ScholarshipRepository scholarshipRepository,
                              UserRepository userRepository,
                              StudentProfileRepository profileRepository,
                              DocumentRepository documentRepository,
                              ApplicationStatusHistoryRepository historyRepository,
                              ReviewRepository reviewRepository,
                              EligibilityService eligibilityService,
                              NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.reviewRepository = reviewRepository;
        this.eligibilityService = eligibilityService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApplicationDTO submitApplication(Long studentUserId, ApplicationRequestDTO request) {
        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found"));

        StudentProfile profile = profileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new BadRequestException("Please complete your student profile before applying for scholarships."));

        // If request includes academic/financial updates from step forms, update profile
        if (request.getCollegeName() != null && !request.getCollegeName().isBlank()) profile.setCollegeName(request.getCollegeName().trim());
        if (request.getUniversityName() != null && !request.getUniversityName().isBlank()) profile.setUniversityName(request.getUniversityName().trim());
        if (request.getCourse() != null && !request.getCourse().isBlank()) profile.setCourse(request.getCourse().trim());
        if (request.getCurrentYear() != null) profile.setCurrentYear(request.getCurrentYear());
        if (request.getAcademicPercentage() != null) profile.setAcademicPercentage(request.getAcademicPercentage());
        if (request.getAnnualFamilyIncome() != null) profile.setAnnualFamilyIncome(request.getAnnualFamilyIncome());
        if (request.getAddress() != null && !request.getAddress().isBlank()) profile.setAddress(request.getAddress().trim());
        profileRepository.save(profile);

        Scholarship scholarship = scholarshipRepository.findById(request.getScholarshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with ID: " + request.getScholarshipId()));

        // Verify status and deadline
        if (!"ACTIVE".equalsIgnoreCase(scholarship.getStatus())) {
            throw new BadRequestException("This scholarship is currently inactive and not accepting applications.");
        }
        if (scholarship.getApplicationDeadline() != null && scholarship.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Application deadline for this scholarship has expired (" + scholarship.getApplicationDeadline() + ").");
        }

        // Prevent duplicate active applications
        boolean hasActiveApplication = applicationRepository.existsByStudentIdAndScholarshipIdAndStatusNot(
                studentUserId,
                scholarship.getId(),
                ApplicationStatus.REJECTED
        );
        if (hasActiveApplication) {
            throw new BadRequestException("You already have an active application submitted for " + scholarship.getScholarshipName() + ".");
        }

        // Server-side Eligibility Verification
        eligibilityService.validateForSubmission(scholarship, student, profile);

        // Generate unique reference number (e.g. SC-2026-84920)
        String referenceNumber = generateUniqueReference();

        Application application = new Application();
        application.setApplicationReference(referenceNumber);
        application.setStudent(student);
        application.setScholarship(scholarship);
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setRemarks(request.getRemarks() != null ? request.getRemarks() : "Application submitted by student.");

        Application savedApp = applicationRepository.save(application);

        // Associate documents
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            for (Long docId : request.getDocumentIds()) {
                documentRepository.findById(docId).ifPresent(doc -> {
                    doc.setApplication(savedApp);
                    documentRepository.save(doc);
                });
            }
        }

        // Status history audit record
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                savedApp,
                null,
                ApplicationStatus.SUBMITTED.name(),
                student.getEmail(),
                "Application initiated and submitted by student."
        );
        historyRepository.save(history);

        // Notify student
        notificationService.createNotification(student,
                "Application Submitted Successfully",
                "Your application for '" + scholarship.getScholarshipName() + "' has been submitted with reference " + referenceNumber + ".");

        return mapToDTO(savedApp);
    }

    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long applicationId, Long currentUserId, String role) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        // Check RBAC permissions
        boolean isStudentOwner = app.getStudent().getId().equals(currentUserId);
        boolean isAdmin = Role.ROLE_ADMIN.name().equalsIgnoreCase(role);
        boolean isAssignedReviewer = Role.ROLE_REVIEWER.name().equalsIgnoreCase(role) &&
                app.getReviewer() != null &&
                app.getReviewer().getId().equals(currentUserId);

        if (!isStudentOwner && !isAdmin && !isAssignedReviewer) {
            throw new UnauthorizedException("You are not authorized to view this application");
        }

        return mapToDTO(app);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getMyApplications(Long studentUserId) {
        return applicationRepository.findByStudentIdOrderByApplicationDateDesc(studentUserId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationTrackingDTO getTracking(Long applicationId, Long currentUserId, String role) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        boolean isStudentOwner = app.getStudent().getId().equals(currentUserId);
        boolean isAdmin = Role.ROLE_ADMIN.name().equalsIgnoreCase(role);
        boolean isAssignedReviewer = Role.ROLE_REVIEWER.name().equalsIgnoreCase(role) &&
                app.getReviewer() != null &&
                app.getReviewer().getId().equals(currentUserId);

        if (!isStudentOwner && !isAdmin && !isAssignedReviewer) {
            throw new UnauthorizedException("You are not authorized to view this tracking information");
        }

        ApplicationTrackingDTO tracking = new ApplicationTrackingDTO();
        tracking.setApplicationReference(app.getApplicationReference());
        tracking.setScholarshipName(app.getScholarship().getScholarshipName());
        tracking.setProvider(app.getScholarship().getProvider());
        tracking.setStudentName(app.getStudent().getFullName());
        tracking.setCurrentStatus(app.getStatus().name());
        tracking.setSubmissionDate(app.getApplicationDate());
        tracking.setLastUpdated(app.getUpdatedAt());
        tracking.setRemarks(app.getRemarks());

        if (app.getStatus() == ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED) {
            tracking.setCorrectiveActionRequired(true);
            tracking.setCorrectiveActionDetails(app.getRemarks() != null ? app.getRemarks() : "Additional documentation required by reviewer/admin.");
        }

        // Build milestones
        List<ApplicationStatusHistory> histories = historyRepository.findByApplicationIdOrderByChangedAtAsc(app.getId());
        List<ApplicationStatusHistoryDTO> historyDTOs = histories.stream()
                .map(h -> new ApplicationStatusHistoryDTO(h.getId(), app.getId(), h.getPreviousStatus(), h.getNewStatus(), h.getChangedBy(), h.getComments(), h.getChangedAt()))
                .collect(Collectors.toList());
        tracking.setHistory(historyDTOs);

        int milestoneIndex = determineMilestoneIndex(app.getStatus());
        tracking.setCurrentMilestoneIndex(milestoneIndex);

        List<ApplicationTrackingDTO.TrackingMilestone> milestones = new ArrayList<>();
        milestones.add(new ApplicationTrackingDTO.TrackingMilestone(
                "Application Submitted",
                "Student completed registration and submitted initial details.",
                milestoneIndex >= 0 ? "COMPLETED" : "PENDING",
                app.getApplicationDate()
        ));
        milestones.add(new ApplicationTrackingDTO.TrackingMilestone(
                "Application Received",
                "Central scholarship desk acknowledged receipt and queued for review.",
                milestoneIndex >= 1 ? "COMPLETED" : (milestoneIndex == 0 ? "CURRENT" : "PENDING"),
                app.getApplicationDate()
        ));
        milestones.add(new ApplicationTrackingDTO.TrackingMilestone(
                "Under Review",
                app.getReviewer() != null ? "Assigned to " + app.getReviewer().getFullName() + " for scrutiny." : "Awaiting reviewer assignment.",
                milestoneIndex >= 2 ? (milestoneIndex == 2 && app.getStatus() == ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED ? "FAILED" : "COMPLETED") : (milestoneIndex == 1 ? "CURRENT" : "PENDING"),
                app.getUpdatedAt()
        ));
        milestones.add(new ApplicationTrackingDTO.TrackingMilestone(
                "Document Verification",
                "Income, academic records, and category certificates verification.",
                milestoneIndex >= 3 ? "COMPLETED" : (milestoneIndex == 2 ? "CURRENT" : "PENDING"),
                app.getUpdatedAt()
        ));
        milestones.add(new ApplicationTrackingDTO.TrackingMilestone(
                "Final Decision",
                app.getStatus() == ApplicationStatus.APPROVED ? "Sanctioned and approved for disbursal." :
                        (app.getStatus() == ApplicationStatus.REJECTED ? "Application rejected." : "Final authority sanction."),
                app.getStatus() == ApplicationStatus.APPROVED ? "COMPLETED" : (app.getStatus() == ApplicationStatus.REJECTED ? "FAILED" : "PENDING"),
                app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.REJECTED ? app.getUpdatedAt() : null
        ));

        tracking.setMilestones(milestones);
        return tracking;
    }

    private int determineMilestoneIndex(ApplicationStatus status) {
        return switch (status) {
            case DRAFT -> 0;
            case SUBMITTED -> 1;
            case UNDER_REVIEW -> 2;
            case ADDITIONAL_INFORMATION_REQUIRED -> 2;
            case DOCUMENT_VERIFICATION -> 3;
            case APPROVED, REJECTED -> 4;
        };
    }

    private String generateUniqueReference() {
        int year = LocalDate.now().getYear();
        String ref;
        do {
            int randomNum = ThreadLocalRandom.current().nextInt(10000, 99999);
            ref = "SC-" + year + "-" + randomNum;
        } while (applicationRepository.findByApplicationReference(ref).isPresent());
        return ref;
    }

    public ApplicationDTO mapToDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setApplicationReference(app.getApplicationReference());

        if (app.getStudent() != null) {
            dto.setStudentId(app.getStudent().getId());
            dto.setStudentName(app.getStudent().getFullName());
            dto.setStudentEmail(app.getStudent().getEmail());
            dto.setStudentMobile(app.getStudent().getMobileNumber());

            StudentProfile p = profileRepository.findByUserId(app.getStudent().getId()).orElse(null);
            if (p != null) {
                dto.setStudentCategory(p.getCategory());
                dto.setStudentState(p.getState());
                dto.setStudentDistrict(p.getDistrict());
                dto.setCollegeName(p.getCollegeName());
                dto.setUniversityName(p.getUniversityName());
                dto.setCourse(p.getCourse());
                dto.setCurrentYear(p.getCurrentYear());
                dto.setAcademicPercentage(p.getAcademicPercentage());
                dto.setAnnualFamilyIncome(p.getAnnualFamilyIncome());
            }
        }

        if (app.getScholarship() != null) {
            dto.setScholarshipId(app.getScholarship().getId());
            dto.setScholarshipName(app.getScholarship().getScholarshipName());
            dto.setScholarshipProvider(app.getScholarship().getProvider());
            dto.setScholarshipAmount(app.getScholarship().getAmount());
            dto.setScholarshipDeadline(app.getScholarship().getApplicationDeadline());
        }

        dto.setApplicationDate(app.getApplicationDate());
        dto.setStatus(app.getStatus().name());

        if (app.getReviewer() != null) {
            dto.setReviewerId(app.getReviewer().getId());
            dto.setReviewerName(app.getReviewer().getFullName());
            dto.setReviewerEmail(app.getReviewer().getEmail());
        }

        dto.setRemarks(app.getRemarks());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        // Documents
        List<Document> docs = documentRepository.findByApplicationIdOrderByUploadedAtAsc(app.getId());
        List<DocumentDTO> docDTOs = docs.stream().map(d -> {
            DocumentDTO dd = new DocumentDTO();
            dd.setId(d.getId());
            dd.setApplicationId(app.getId());
            dd.setDocumentType(d.getDocumentType());
            dd.setOriginalFilename(d.getOriginalFilename());
            dd.setStoredFilename(d.getStoredFilename());
            dd.setFileUrl("/api/documents/" + d.getId());
            dd.setContentType(d.getContentType());
            dd.setFileSize(d.getFileSize());
            dd.setUploadedAt(d.getUploadedAt());
            dd.setVerificationStatus(d.getVerificationStatus());
            return dd;
        }).collect(Collectors.toList());
        dto.setDocuments(docDTOs);

        // Status histories
        List<ApplicationStatusHistory> histories = historyRepository.findByApplicationIdOrderByChangedAtAsc(app.getId());
        List<ApplicationStatusHistoryDTO> histDTOs = histories.stream().map(h ->
                new ApplicationStatusHistoryDTO(h.getId(), app.getId(), h.getPreviousStatus(), h.getNewStatus(), h.getChangedBy(), h.getComments(), h.getChangedAt())
        ).collect(Collectors.toList());
        dto.setStatusHistories(histDTOs);

        // Reviews
        List<Review> reviews = reviewRepository.findByApplicationIdOrderByReviewedAtDesc(app.getId());
        List<ReviewDTO> revDTOs = reviews.stream().map(r -> {
            ReviewDTO rd = new ReviewDTO();
            rd.setId(r.getId());
            rd.setApplicationId(app.getId());
            rd.setReviewerId(r.getReviewer().getId());
            rd.setReviewerName(r.getReviewer().getFullName());
            rd.setReviewerEmail(r.getReviewer().getEmail());
            rd.setComments(r.getComments());
            rd.setRecommendation(r.getRecommendation());
            rd.setReviewedAt(r.getReviewedAt());
            return rd;
        }).collect(Collectors.toList());
        dto.setReviews(revDTOs);

        return dto;
    }
}
