package com.scholarconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class StatusUpdateRequestDTO {

    @NotBlank(message = "New status is required")
    private String status;

    private String comments;

    public StatusUpdateRequestDTO() {
    }

    public StatusUpdateRequestDTO(String status, String comments) {
        this.status = status;
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

