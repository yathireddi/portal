package com.scholarconnect.controller;

import com.scholarconnect.dto.ApiResponse;
import com.scholarconnect.dto.ApplicationDTO;
import com.scholarconnect.dto.ApplicationRequestDTO;
import com.scholarconnect.dto.ApplicationTrackingDTO;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationDTO>> submitApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApplicationRequestDTO request) {
        ApplicationDTO dto = applicationService.submitApplication(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Scholarship application submitted successfully! Reference: " + dto.getApplicationReference(), dto), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getMyApplications(@AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationDTO> list = applicationService.getMyApplications(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("My applications retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDTO>> getApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        String role = principal.getAuthorities().iterator().next().getAuthority();
        ApplicationDTO dto = applicationService.getApplicationById(id, principal.getId(), role);
        return ResponseEntity.ok(ApiResponse.success("Application details retrieved", dto));
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<ApplicationTrackingDTO>> getApplicationTracking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        String role = principal.getAuthorities().iterator().next().getAuthority();
        ApplicationTrackingDTO tracking = applicationService.getTracking(id, principal.getId(), role);
        return ResponseEntity.ok(ApiResponse.success("Application tracking status loaded", tracking));
    }
}

