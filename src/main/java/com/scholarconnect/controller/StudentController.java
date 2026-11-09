package com.scholarconnect.controller;

import com.scholarconnect.dto.ApiResponse;
import com.scholarconnect.dto.ApplicationDTO;
import com.scholarconnect.dto.StudentDashboardDTO;
import com.scholarconnect.dto.StudentProfileDTO;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.ApplicationService;
import com.scholarconnect.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class StudentController {

    private final StudentService studentService;
    private final ApplicationService applicationService;

    public StudentController(StudentService studentService, ApplicationService applicationService) {
        this.studentService = studentService;
        this.applicationService = applicationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileDTO>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileDTO profile = studentService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileDTO>> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                                        @RequestBody StudentProfileDTO dto) {
        StudentProfileDTO updated = studentService.updateProfile(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<StudentDashboardDTO>> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        StudentDashboardDTO dashboard = studentService.getDashboard(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard data loaded", dashboard));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplications(@AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationDTO> apps = applicationService.getMyApplications(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", apps));
    }
}

