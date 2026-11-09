package com.scholarconnect.service;

import com.scholarconnect.dto.EligibilityResultDTO;
import com.scholarconnect.entity.Scholarship;
import com.scholarconnect.entity.StudentProfile;
import com.scholarconnect.entity.User;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.repository.ScholarshipRepository;
import com.scholarconnect.repository.StudentProfileRepository;
import com.scholarconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EligibilityService {

    private final ScholarshipRepository scholarshipRepository;
    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public EligibilityService(ScholarshipRepository scholarshipRepository,
                              StudentProfileRepository profileRepository,
                              UserRepository userRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public EligibilityResultDTO checkEligibility(Long scholarshipId, Long studentUserId) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with ID: " + scholarshipId));

        User user = userRepository.findById(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found"));

        StudentProfile profile = profileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new BadRequestException("Student profile is not completed yet. Please fill your profile first."));

        return evaluate(scholarship, user, profile);
    }

    public EligibilityResultDTO evaluate(Scholarship scholarship, User user, StudentProfile profile) {
        EligibilityResultDTO result = new EligibilityResultDTO();
        result.setScholarshipId(scholarship.getId());
        result.setScholarshipName(scholarship.getScholarshipName());

        result.setStudentPercentage(profile.getAcademicPercentage());
        result.setRequiredPercentage(scholarship.getMinimumPercentage());

        result.setStudentIncome(profile.getAnnualFamilyIncome());
        result.setMaximumIncome(scholarship.getMaximumFamilyIncome());

        result.setStudentCategory(profile.getCategory());
        result.setEligibleCategories(scholarship.getEligibleCategories());

        result.setStudentState(profile.getState());
        result.setEligibleStates(scholarship.getEligibleStates());

        result.setStudentCourse(profile.getCourse());
        result.setEligibleCourses(scholarship.getEligibleCourses());

        result.setStudentEducationLevel(scholarship.getEducationLevel());
        result.setEligibleEducationLevel(scholarship.getEducationLevel());

        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        // 1. Status Check
        if (!"ACTIVE".equalsIgnoreCase(scholarship.getStatus())) {
            failed.add("Scholarship is currently inactive or closed for applications.");
        } else {
            passed.add("Scholarship status is active.");
        }

        // 2. Deadline Check
        LocalDate today = LocalDate.now();
        if (scholarship.getApplicationDeadline() != null && scholarship.getApplicationDeadline().isBefore(today)) {
            failed.add("Application deadline passed on " + scholarship.getApplicationDeadline() + ".");
        } else {
            passed.add("Application window is open (Deadline: " + scholarship.getApplicationDeadline() + ").");
        }

        // 3. Academic Percentage
        BigDecimal studentPct = profile.getAcademicPercentage() != null ? profile.getAcademicPercentage() : BigDecimal.ZERO;
        BigDecimal reqPct = scholarship.getMinimumPercentage() != null ? scholarship.getMinimumPercentage() : BigDecimal.ZERO;
        if (studentPct.compareTo(reqPct) < 0) {
            failed.add("Academic score (" + studentPct + "%) is below the required minimum (" + reqPct + "%).");
        } else {
            passed.add("Academic score (" + studentPct + "%) meets minimum criteria (" + reqPct + "%).");
        }

        // 4. Annual Family Income
        BigDecimal studentInc = profile.getAnnualFamilyIncome() != null ? profile.getAnnualFamilyIncome() : BigDecimal.ZERO;
        BigDecimal maxInc = scholarship.getMaximumFamilyIncome() != null ? scholarship.getMaximumFamilyIncome() : new BigDecimal("9999999");
        if (studentInc.compareTo(maxInc) > 0) {
            failed.add("Family annual income (₹" + studentInc + ") exceeds the maximum allowed limit (₹" + maxInc + ").");
        } else {
            passed.add("Family income (₹" + studentInc + ") is within the permitted ceiling (₹" + maxInc + ").");
        }

        // 5. Category Check
        String allowedCat = scholarship.getEligibleCategories();
        String studentCat = profile.getCategory() != null ? profile.getCategory().trim() : "";
        if (allowedCat != null && !allowedCat.equalsIgnoreCase("ALL") && !allowedCat.isBlank()) {
            List<String> cats = Arrays.stream(allowedCat.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            if (!cats.contains(studentCat.toLowerCase()) && !cats.contains("all")) {
                failed.add("Your social category (" + studentCat + ") is not eligible. Required: " + allowedCat + ".");
            } else {
                passed.add("Social category (" + studentCat + ") is eligible.");
            }
        } else {
            passed.add("All social categories are eligible.");
        }

        // 6. Domicile / State Check
        String allowedStates = scholarship.getEligibleStates();
        String studentState = profile.getState() != null ? profile.getState().trim() : "";
        if (allowedStates != null && !allowedStates.equalsIgnoreCase("ALL") && !allowedStates.isBlank()) {
            List<String> states = Arrays.stream(allowedStates.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            if (!states.contains(studentState.toLowerCase()) && !states.contains("all")) {
                failed.add("Your state domicile (" + studentState + ") is not eligible. Applicable states: " + allowedStates + ".");
            } else {
                passed.add("Domicile state (" + studentState + ") is eligible.");
            }
        } else {
            passed.add("All Indian states and union territories are eligible.");
        }

        // 7. Course Check
        String allowedCourses = scholarship.getEligibleCourses();
        String studentCourse = profile.getCourse() != null ? profile.getCourse().trim() : "";
        if (allowedCourses != null && !allowedCourses.equalsIgnoreCase("ALL") && !allowedCourses.isBlank()) {
            List<String> courses = Arrays.stream(allowedCourses.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            boolean match = courses.stream().anyMatch(c -> studentCourse.toLowerCase().contains(c) || c.contains(studentCourse.toLowerCase()));
            if (!match && !courses.contains("all")) {
                failed.add("Your course (" + studentCourse + ") is not covered. Eligible courses: " + allowedCourses + ".");
            } else {
                passed.add("Degree course (" + studentCourse + ") is eligible.");
            }
        } else {
            passed.add("All accredited university courses are eligible.");
        }

        result.setPassedConditions(passed);
        result.setFailedConditions(failed);
        result.setEligible(failed.isEmpty());
        return result;
    }

    public void validateForSubmission(Scholarship scholarship, User user, StudentProfile profile) {
        EligibilityResultDTO evaluation = evaluate(scholarship, user, profile);
        if (!evaluation.isEligible()) {
            String reasons = String.join("; ", evaluation.getFailedConditions());
            throw new BadRequestException("Eligibility verification failed: " + reasons);
        }
    }
}

