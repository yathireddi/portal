package com.scholarconnect.service;

import com.scholarconnect.dto.*;
import com.scholarconnect.entity.*;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.repository.ApplicationRepository;
import com.scholarconnect.repository.NotificationRepository;
import com.scholarconnect.repository.ScholarshipRepository;
import com.scholarconnect.repository.StudentProfileRepository;
import com.scholarconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final NotificationRepository notificationRepository;
    private final ScholarshipService scholarshipService;

    public StudentService(UserRepository userRepository,
                          StudentProfileRepository profileRepository,
                          ApplicationRepository applicationRepository,
                          ScholarshipRepository scholarshipRepository,
                          NotificationRepository notificationRepository,
                          ScholarshipService scholarshipService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.applicationRepository = applicationRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.notificationRepository = notificationRepository;
        this.scholarshipService = scholarshipService;
    }

    @Transactional(readOnly = true)
    public StudentProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    StudentProfile p = new StudentProfile();
                    p.setUser(user);
                    return profileRepository.save(p);
                });

        return mapToProfileDTO(user, profile);
    }

    @Transactional
    public StudentProfileDTO updateProfile(Long userId, StudentProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getMobileNumber() != null && !dto.getMobileNumber().isBlank()) {
            user.setMobileNumber(dto.getMobileNumber().trim());
        }
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        userRepository.save(user);

        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    StudentProfile p = new StudentProfile();
                    p.setUser(user);
                    return p;
                });

        if (dto.getCategory() != null) profile.setCategory(dto.getCategory());
        if (dto.getState() != null) profile.setState(dto.getState());
        if (dto.getDistrict() != null) profile.setDistrict(dto.getDistrict());
        if (dto.getCollegeName() != null) profile.setCollegeName(dto.getCollegeName());
        if (dto.getUniversityName() != null) profile.setUniversityName(dto.getUniversityName());
        if (dto.getCourse() != null) profile.setCourse(dto.getCourse());
        if (dto.getCurrentYear() != null) profile.setCurrentYear(dto.getCurrentYear());
        if (dto.getAcademicPercentage() != null) profile.setAcademicPercentage(dto.getAcademicPercentage());
        if (dto.getAnnualFamilyIncome() != null) profile.setAnnualFamilyIncome(dto.getAnnualFamilyIncome());
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress());

        StudentProfile updatedProfile = profileRepository.save(profile);
        return mapToProfileDTO(user, updatedProfile);
    }

    @Transactional(readOnly = true)
    public StudentDashboardDTO getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = profileRepository.findByUserId(userId).orElse(null);

        StudentDashboardDTO dto = new StudentDashboardDTO();
        dto.setStudentName(user.getFullName());
        dto.setProfileCompletionPercentage(calculateProfileCompletion(user, profile));

        dto.setTotalAvailableScholarships(scholarshipRepository.countByStatus("ACTIVE"));
        dto.setApplicationsSubmitted(applicationRepository.countByStudentId(userId));
        dto.setApplicationsUnderReview(
                applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.UNDER_REVIEW) +
                applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.DOCUMENT_VERIFICATION)
        );
        dto.setApplicationsApproved(applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.APPROVED));
        dto.setApplicationsRejected(applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.REJECTED));

        // Status counts for Chart.js
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("Submitted", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.SUBMITTED));
        statusCounts.put("Under Review", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.UNDER_REVIEW));
        statusCounts.put("Doc Verification", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.DOCUMENT_VERIFICATION));
        statusCounts.put("Approved", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.APPROVED));
        statusCounts.put("Rejected", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.REJECTED));
        statusCounts.put("Action Required", applicationRepository.countByStudentIdAndStatus(userId, ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED));
        dto.setStatusCounts(statusCounts);

        // Recent applications
        List<Application> studentApps = applicationRepository.findByStudentIdOrderByApplicationDateDesc(userId);
        List<ApplicationDTO> recentApps = studentApps.stream()
                .limit(5)
                .map(this::mapToApplicationDTO)
                .collect(Collectors.toList());
        dto.setRecentApplications(recentApps);

        // Recommended scholarships
        List<ScholarshipDTO> allScholarships = scholarshipService.getAllActive("", "", "");
        List<ScholarshipDTO> recommended = allScholarships.stream()
                .limit(4)
                .collect(Collectors.toList());
        dto.setRecommendedScholarships(recommended);

        // Upcoming deadlines
        List<ScholarshipDTO> deadlines = allScholarships.stream()
                .filter(s -> !s.isExpired())
                .sorted(Comparator.comparing(ScholarshipDTO::getApplicationDeadline))
                .limit(4)
                .collect(Collectors.toList());
        dto.setUpcomingDeadlines(deadlines);

        // Notifications
        List<Notification> notifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        List<StudentDashboardDTO.NotificationDTO> notifDTOs = notifs.stream()
                .limit(6)
                .map(n -> new StudentDashboardDTO.NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt().format(formatter)
                ))
                .collect(Collectors.toList());
        dto.setNotifications(notifDTOs);

        return dto;
    }

    private StudentProfileDTO mapToProfileDTO(User user, StudentProfile profile) {
        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(profile != null ? profile.getId() : null);
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());

        if (profile != null) {
            dto.setCategory(profile.getCategory());
            dto.setState(profile.getState());
            dto.setDistrict(profile.getDistrict());
            dto.setCollegeName(profile.getCollegeName());
            dto.setUniversityName(profile.getUniversityName());
            dto.setCourse(profile.getCourse());
            dto.setCurrentYear(profile.getCurrentYear());
            dto.setAcademicPercentage(profile.getAcademicPercentage());
            dto.setAnnualFamilyIncome(profile.getAnnualFamilyIncome());
            dto.setAddress(profile.getAddress());
        }

        dto.setProfileCompletionPercentage(calculateProfileCompletion(user, profile));
        return dto;
    }

    public int calculateProfileCompletion(User user, StudentProfile profile) {
        int score = 0;
        if (user.getFullName() != null && !user.getFullName().isBlank()) score += 10;
        if (user.getEmail() != null && !user.getEmail().isBlank()) score += 10;
        if (user.getMobileNumber() != null && !user.getMobileNumber().isBlank()) score += 10;
        if (user.getDateOfBirth() != null) score += 10;
        if (user.getGender() != null && !user.getGender().isBlank()) score += 10;

        if (profile != null) {
            if (profile.getState() != null && !profile.getState().isBlank()) score += 10;
            if (profile.getCollegeName() != null && !profile.getCollegeName().isBlank()) score += 10;
            if (profile.getCourse() != null && !profile.getCourse().isBlank()) score += 10;
            if (profile.getAcademicPercentage() != null && profile.getAcademicPercentage().compareTo(BigDecimal.ZERO) > 0) score += 10;
            if (profile.getAnnualFamilyIncome() != null && profile.getAnnualFamilyIncome().compareTo(BigDecimal.ZERO) > 0) score += 10;
        }

        return Math.min(score, 100);
    }

    private ApplicationDTO mapToApplicationDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setApplicationReference(app.getApplicationReference());
        dto.setStudentId(app.getStudent().getId());
        dto.setStudentName(app.getStudent().getFullName());
        dto.setStudentEmail(app.getStudent().getEmail());
        dto.setScholarshipId(app.getScholarship().getId());
        dto.setScholarshipName(app.getScholarship().getScholarshipName());
        dto.setScholarshipProvider(app.getScholarship().getProvider());
        dto.setScholarshipAmount(app.getScholarship().getAmount());
        dto.setScholarshipDeadline(app.getScholarship().getApplicationDeadline());
        dto.setApplicationDate(app.getApplicationDate());
        dto.setStatus(app.getStatus().name());
        dto.setRemarks(app.getRemarks());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
        return dto;
    }
}
