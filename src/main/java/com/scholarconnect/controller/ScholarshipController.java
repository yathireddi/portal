package com.scholarconnect.controller;

import com.scholarconnect.dto.ApiResponse;
import com.scholarconnect.dto.EligibilityResultDTO;
import com.scholarconnect.dto.ScholarshipDTO;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.EligibilityService;
import com.scholarconnect.service.ScholarshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final EligibilityService eligibilityService;

    public ScholarshipController(ScholarshipService scholarshipService,
                                 EligibilityService eligibilityService) {
        this.scholarshipService = scholarshipService;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScholarshipDTO>>> getScholarships(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String level) {
        List<ScholarshipDTO> list = scholarshipService.getAllActive(category, state, level);
        return ResponseEntity.ok(ApiResponse.success("Scholarships fetched successfully", list));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ScholarshipDTO>>> searchScholarships(@RequestParam(required = false) String q) {
        List<ScholarshipDTO> list = scholarshipService.search(q);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScholarshipDTO>> getScholarshipById(@PathVariable Long id) {
        ScholarshipDTO dto = scholarshipService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Scholarship details retrieved", dto));
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<ApiResponse<EligibilityResultDTO>> checkEligibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please login to check your scholarship eligibility."));
        }
        EligibilityResultDTO result = eligibilityService.checkEligibility(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Eligibility evaluated", result));
    }
}

