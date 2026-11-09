package com.scholarconnect.dto;

import jakarta.validation.constraints.NotNull;

public class AssignReviewerRequestDTO {

    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    public AssignReviewerRequestDTO() {
    }

    public AssignReviewerRequestDTO(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }
}
