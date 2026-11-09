package com.scholarconnect.dto;

import java.util.ArrayList;
import java.util.List;

public class ReviewerDashboardDTO {
    private String reviewerName;
    private long totalAssigned;
    private long pendingReviews;
    private long completedReviews;

    private List<ApplicationDTO> assignedApplications = new ArrayList<>();
    private List<ReviewDTO> recentReviews = new ArrayList<>();

    public ReviewerDashboardDTO() {
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(long totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public long getPendingReviews() {
        return pendingReviews;
    }

    public void setPendingReviews(long pendingReviews) {
        this.pendingReviews = pendingReviews;
    }

    public long getCompletedReviews() {
        return completedReviews;
    }

    public void setCompletedReviews(long completedReviews) {
        this.completedReviews = completedReviews;
    }

    public List<ApplicationDTO> getAssignedApplications() {
        return assignedApplications;
    }

    public void setAssignedApplications(List<ApplicationDTO> assignedApplications) {
        this.assignedApplications = assignedApplications;
    }

    public List<ReviewDTO> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<ReviewDTO> recentReviews) {
        this.recentReviews = recentReviews;
    }
}
