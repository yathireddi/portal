package com.scholarconnect.service;

import com.scholarconnect.dto.*;
import com.scholarconnect.entity.*;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository profileRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final NotificationService notificationService;
    private final ApplicationService applicationService;

    public AdminService(UserRepository userRepository,
                        ScholarshipRepository scholarshipRepository,
                        ApplicationRepository applicationRepository,
                        StudentProfileRepository profileRepository,
                        ApplicationStatusHistoryRepository historyRepository,
                        NotificationService notificationService,
                        ApplicationService applicationService) {
        this.userRepository = userRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.applicationRepository = applicationRepository;
        this.profileRepository = profileRepository;
        this.historyRepository = historyRepository;
        this.notificationService = notificationService;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        dto.setTotalStudents(userRepository.countByRole(Role.ROLE_STUDENT));
        dto.setTotalScholarships(scholarshipRepository.count());
        dto.setTotalApplications(applicationRepository.count());
        dto.setPendingApplications(applicationRepository.countByStatus(ApplicationStatus.SUBMITTED));
        dto.setApplicationsUnderReview(
                applicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW) +
                applicationRepository.countByStatus(ApplicationStatus.DOCUMENT_VERIFICATION)
        );
        dto.setApprovedApplications(applicationRepository.countByStatus(ApplicationStatus.APPROVED));
        dto.setRejectedApplications(applicationRepository.countByStatus(ApplicationStatus.REJECTED));

        // 1. Applications by Status Chart Data
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            byStatus.put(status.name(), applicationRepository.countByStatus(status));
        }
        dto.setApplicationsByStatus(byStatus);

        // 2. Monthly Applications Chart Data
        Map<String, Long> monthly = new LinkedHashMap<>();
        List<Application> allApps = applicationRepository.findAll();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (Application a : allApps) {
            String month = a.getApplicationDate().format(monthFormatter);
            monthly.put(month, monthly.getOrDefault(month, 0L) + 1);
        }
        dto.setMonthlyApplications(monthly);

        // 3. Scholarship-wise Applications
        Map<String, Long> schWise = new LinkedHashMap<>();
        List<Scholarship> scholarships = scholarshipRepository.findAll();
        for (Scholarship s : scholarships) {
            long count = applicationRepository.countByScholarshipId(s.getId());
            schWise.put(s.getScholarshipName(), count);
        }
        dto.setScholarshipWiseApplications(schWise);

        // 4. Category-wise student distribution
        Map<String, Long> catDist = new LinkedHashMap<>();
        List<StudentProfile> profiles = profileRepository.findAll();
        for (StudentProfile p : profiles) {
            String cat = p.getCategory() != null && !p.getCategory().isBlank() ? p.getCategory() : "General";
            catDist.put(cat, catDist.getOrDefault(cat, 0L) + 1);
        }
        dto.setCategoryWiseDistribution(catDist);

        // 5. Recent applications
        List<ApplicationDTO> recentApps = allApps.stream()
                .sorted(Comparator.comparing(Application::getApplicationDate).reversed())
                .limit(5)
                .map(applicationService::mapToDTO)
                .collect(Collectors.toList());
        dto.setRecentApplications(recentApps);

        // 6. Recent registrations
        List<User> students = userRepository.findByRole(Role.ROLE_STUDENT);
        List<UserSummaryDTO> recentUsers = students.stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(5)
                .map(this::mapToUserSummary)
                .collect(Collectors.toList());
        dto.setRecentRegistrations(recentUsers);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAllApplications(Long scholarshipId, ApplicationStatus status, String search) {
        return applicationRepository.searchApplications(scholarshipId, status, search).stream()
                .map(applicationService::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDTO updateApplicationStatus(Long applicationId, StatusUpdateRequestDTO request, String adminEmail) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        ApplicationStatus newStatus;
        try {
            newStatus = ApplicationStatus.valueOf(request.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status value: " + request.getStatus());
        }

        // Rule: require comments or reasons when rejecting or requesting additional info
        if ((newStatus == ApplicationStatus.REJECTED || newStatus == ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED)
                && (request.getComments() == null || request.getComments().trim().isBlank())) {
            throw new BadRequestException("Comments or explanatory reason is mandatory when rejecting or requesting additional information.");
        }

        String previousStatus = app.getStatus().name();
        app.setStatus(newStatus);
        if (request.getComments() != null && !request.getComments().isBlank()) {
            app.setRemarks(request.getComments().trim());
        }
        Application updated = applicationRepository.save(app);

        // Status history audit record
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                updated,
                previousStatus,
                newStatus.name(),
                adminEmail,
                request.getComments() != null ? request.getComments().trim() : "Status updated by Administrator."
        );
        historyRepository.save(history);

        // Notify student
        String notificationTitle = "Application Status Updated: " + newStatus.name();
        String notificationMsg = "Your application (" + app.getApplicationReference() + ") for " +
                app.getScholarship().getScholarshipName() + " has been updated to " + newStatus.name() +
                (request.getComments() != null ? ". Remarks: " + request.getComments() : ".");
        notificationService.createNotification(app.getStudent(), notificationTitle, notificationMsg);

        return applicationService.mapToDTO(updated);
    }

    @Transactional
    public ApplicationDTO assignReviewer(Long applicationId, AssignReviewerRequestDTO request, String adminEmail) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with ID: " + request.getReviewerId()));

        if (reviewer.getRole() != Role.ROLE_REVIEWER) {
            throw new BadRequestException("Selected user is not a verified Reviewer.");
        }

        String previousStatus = app.getStatus().name();
        app.setReviewer(reviewer);
        app.setStatus(ApplicationStatus.UNDER_REVIEW);
        Application updated = applicationRepository.save(app);

        // Status history
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                updated,
                previousStatus,
                ApplicationStatus.UNDER_REVIEW.name(),
                adminEmail,
                "Assigned to reviewer: " + reviewer.getFullName() + " (" + reviewer.getEmail() + ")."
        );
        historyRepository.save(history);

        // Notify Student
        notificationService.createNotification(
                app.getStudent(),
                "Application Under Review",
                "Your application (" + app.getApplicationReference() + ") has been assigned to " + reviewer.getFullName() + " for document scrutiny."
        );

        // Notify Reviewer
        notificationService.createNotification(
                reviewer,
                "New Application Assigned",
                "Application " + app.getApplicationReference() + " for '" + app.getScholarship().getScholarshipName() + "' has been assigned to you."
        );

        return applicationService.mapToDTO(updated);
    }

    private UserSummaryDTO mapToUserSummary(User u) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setMobileNumber(u.getMobileNumber());
        dto.setRole(u.getRole().name());
        dto.setEnabled(u.isEnabled());
        dto.setDateOfBirth(u.getDateOfBirth());
        dto.setGender(u.getGender());
        dto.setCreatedAt(u.getCreatedAt());

        StudentProfile p = profileRepository.findByUserId(u.getId()).orElse(null);
        if (p != null) {
            dto.setCategory(p.getCategory());
            dto.setCollegeName(p.getCollegeName());
            dto.setCourse(p.getCourse());
        }
        return dto;
    }
}
