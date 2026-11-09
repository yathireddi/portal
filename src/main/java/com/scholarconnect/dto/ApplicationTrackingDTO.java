package com.scholarconnect.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationTrackingDTO {
    private String applicationReference;
    private String scholarshipName;
    private String provider;
    private String studentName;
    private String currentStatus;
    private LocalDateTime submissionDate;
    private LocalDateTime lastUpdated;
    private int currentMilestoneIndex; // 0=Submitted, 1=Received, 2=Under Review, 3=Document Verification, 4=Final Decision
    private List<TrackingMilestone> milestones = new ArrayList<>();
    private String remarks;
    private boolean correctiveActionRequired;
    private String correctiveActionDetails;
    private List<ApplicationStatusHistoryDTO> history = new ArrayList<>();

    public static class TrackingMilestone {
        private String stepName;
        private String description;
        private String status; // COMPLETED, CURRENT, PENDING, FAILED
        private LocalDateTime date;

        public TrackingMilestone() {
        }

        public TrackingMilestone(String stepName, String description, String status, LocalDateTime date) {
            this.stepName = stepName;
            this.description = description;
            this.status = status;
            this.date = date;
        }

        public String getStepName() {
            return stepName;
        }

        public void setStepName(String stepName) {
            this.stepName = stepName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }

    public ApplicationTrackingDTO() {
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(String applicationReference) {
        this.applicationReference = applicationReference;
    }

    public String getScholarshipName() {
        return scholarshipName;
    }

    public void setScholarshipName(String scholarshipName) {
        this.scholarshipName = scholarshipName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getCurrentMilestoneIndex() {
        return currentMilestoneIndex;
    }

    public void setCurrentMilestoneIndex(int currentMilestoneIndex) {
        this.currentMilestoneIndex = currentMilestoneIndex;
    }

    public List<TrackingMilestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<TrackingMilestone> milestones) {
        this.milestones = milestones;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isCorrectiveActionRequired() {
        return correctiveActionRequired;
    }

    public void setCorrectiveActionRequired(boolean correctiveActionRequired) {
        this.correctiveActionRequired = correctiveActionRequired;
    }

    public String getCorrectiveActionDetails() {
        return correctiveActionDetails;
    }

    public void setCorrectiveActionDetails(String correctiveActionDetails) {
        this.correctiveActionDetails = correctiveActionDetails;
    }

    public List<ApplicationStatusHistoryDTO> getHistory() {
        return history;
    }

    public void setHistory(List<ApplicationStatusHistoryDTO> history) {
        this.history = history;
    }
}

