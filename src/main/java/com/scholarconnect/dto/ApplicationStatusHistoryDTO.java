package com.scholarconnect.dto;

import java.time.LocalDateTime;

public class ApplicationStatusHistoryDTO {
    private Long id;
    private Long applicationId;
    private String previousStatus;
    private String newStatus;
    private String changedBy;
    private String comments;
    private LocalDateTime changedAt;

    public ApplicationStatusHistoryDTO() {
    }

    public ApplicationStatusHistoryDTO(Long id, Long applicationId, String previousStatus,
                                       String newStatus, String changedBy, String comments,
                                       LocalDateTime changedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.comments = comments;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}

