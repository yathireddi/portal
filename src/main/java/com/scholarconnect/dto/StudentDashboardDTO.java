package com.scholarconnect.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboardDTO {
    private String studentName;
    private int profileCompletionPercentage;
    private long totalAvailableScholarships;
    private long applicationsSubmitted;
    private long applicationsUnderReview;
    private long applicationsApproved;
    private long applicationsRejected;

    private List<ScholarshipDTO> recommendedScholarships = new ArrayList<>();
    private List<ApplicationDTO> recentApplications = new ArrayList<>();
    private List<ScholarshipDTO> upcomingDeadlines = new ArrayList<>();
    private List<NotificationDTO> notifications = new ArrayList<>();
    private Map<String, Long> statusCounts = new HashMap<>();

    public static class NotificationDTO {
        private Long id;
        private String title;
        private String message;
        private boolean read;
        private String timeAgo;

        public NotificationDTO() {
        }

        public NotificationDTO(Long id, String title, String message, boolean read, String timeAgo) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.read = read;
            this.timeAgo = timeAgo;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }
    }

    public StudentDashboardDTO() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(int profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    public long getTotalAvailableScholarships() {
        return totalAvailableScholarships;
    }

    public void setTotalAvailableScholarships(long totalAvailableScholarships) {
        this.totalAvailableScholarships = totalAvailableScholarships;
    }

    public long getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(long applicationsSubmitted) {
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public long getApplicationsUnderReview() {
        return applicationsUnderReview;
    }

    public void setApplicationsUnderReview(long applicationsUnderReview) {
        this.applicationsUnderReview = applicationsUnderReview;
    }

    public long getApplicationsApproved() {
        return applicationsApproved;
    }

    public void setApplicationsApproved(long applicationsApproved) {
        this.applicationsApproved = applicationsApproved;
    }

    public long getApplicationsRejected() {
        return applicationsRejected;
    }

    public void setApplicationsRejected(long applicationsRejected) {
        this.applicationsRejected = applicationsRejected;
    }

    public List<ScholarshipDTO> getRecommendedScholarships() {
        return recommendedScholarships;
    }

    public void setRecommendedScholarships(List<ScholarshipDTO> recommendedScholarships) {
        this.recommendedScholarships = recommendedScholarships;
    }

    public List<ApplicationDTO> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<ApplicationDTO> recentApplications) {
        this.recentApplications = recentApplications;
    }

    public List<ScholarshipDTO> getUpcomingDeadlines() {
        return upcomingDeadlines;
    }

    public void setUpcomingDeadlines(List<ScholarshipDTO> upcomingDeadlines) {
        this.upcomingDeadlines = upcomingDeadlines;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }
}
