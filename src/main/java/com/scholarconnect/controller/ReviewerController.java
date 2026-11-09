package com.scholarconnect.controller;

import com.scholarconnect.dto.*;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.ReviewerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewer")
@PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
public class ReviewerController {

    private final ReviewerService reviewerService;

    public ReviewerController(ReviewerService reviewerService) {
        this.reviewerService = reviewerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ReviewerDashboardDTO>> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        ReviewerDashboardDTO dashboard = reviewerService.getDashboard(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Reviewer dashboard loaded", dashboard));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getAssignedApplications(@AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationDTO> list = reviewerService.getAssignedApplications(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Assigned applications retrieved", list));
    }

    @PostMapping("/applications/{id}/review")
    public ResponseEntity<ApiResponse<ReviewDTO>> submitReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ReviewDTO review = reviewerService.submitReview(id, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Application review submitted successfully", review));
    }

    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<ApiResponse<DocumentDTO>> verifyDocument(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal principal) {
        DocumentDTO doc = reviewerService.verifyDocument(id, status, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Document verification updated to " + status, doc));
    }
}

