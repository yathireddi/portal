package com.scholarconnect.controller;

import com.scholarconnect.dto.*;
import com.scholarconnect.entity.ApplicationStatus;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.AdminService;
import com.scholarconnect.service.ScholarshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ScholarshipService scholarshipService;

    public AdminController(AdminService adminService, ScholarshipService scholarshipService) {
        this.adminService = adminService;
        this.scholarshipService = scholarshipService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard data loaded", dashboard));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryDTO>>> getUsers() {
        List<UserSummaryDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplications(
            @RequestParam(required = false) Long scholarshipId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String search) {
        List<ApplicationDTO> applications = adminService.getAllApplications(scholarshipId, status, search);
        return ResponseEntity.ok(ApiResponse.success("Applications list retrieved", applications));
    }

    @PostMapping("/scholarships")
    public ResponseEntity<ApiResponse<ScholarshipDTO>> createScholarship(@Valid @RequestBody ScholarshipDTO dto) {
        ScholarshipDTO created = scholarshipService.createScholarship(dto);
        return new ResponseEntity<>(ApiResponse.success("Scholarship created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/scholarships/{id}")
    public ResponseEntity<ApiResponse<ScholarshipDTO>> updateScholarship(
            @PathVariable Long id,
            @Valid @RequestBody ScholarshipDTO dto) {
        ScholarshipDTO updated = scholarshipService.updateScholarship(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Scholarship updated successfully", updated));
    }

    @DeleteMapping("/scholarships/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteScholarship(@PathVariable Long id) {
        scholarshipService.deleteScholarship(id);
        return ResponseEntity.ok(ApiResponse.success("Scholarship removed successfully"));
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ApplicationDTO updated = adminService.updateApplicationStatus(id, request, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Application status updated to " + request.getStatus(), updated));
    }

    @PutMapping("/applications/{id}/assign-reviewer")
    public ResponseEntity<ApiResponse<ApplicationDTO>> assignReviewer(
            @PathVariable Long id,
            @Valid @RequestBody AssignReviewerRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ApplicationDTO updated = adminService.assignReviewer(id, request, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Application assigned to reviewer", updated));
    }
}
