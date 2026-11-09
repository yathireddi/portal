package com.scholarconnect.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardDTO {
    private long totalStudents;
    private long totalScholarships;
    private long totalApplications;
    private long pendingApplications;
    private long applicationsUnderReview;
    private long approvedApplications;
    private long rejectedApplications;

    // Charts data
    private Map<String, Long> applicationsByStatus = new HashMap<>();
    private Map<String, Long> monthlyApplications = new HashMap<>();
    private Map<String, Long> scholarshipWiseApplications = new HashMap<>();
    private Map<String, Long> categoryWiseDistribution = new HashMap<>();

    // Recent activity feeds
    private List<ApplicationDTO> recentApplications = new ArrayList<>();
    private List<UserSummaryDTO> recentRegistrations = new ArrayList<>();

    public AdminDashboardDTO() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalScholarships() {
        return totalScholarships;
    }

    public void setTotalScholarships(long totalScholarships) {
        this.totalScholarships = totalScholarships;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(long pendingApplications) {
        this.pendingApplications = pendingApplications;
    }

    public long getApplicationsUnderReview() {
        return applicationsUnderReview;
    }

    public void setApplicationsUnderReview(long applicationsUnderReview) {
        this.applicationsUnderReview = applicationsUnderReview;
    }

    public long getApprovedApplications() {
        return approvedApplications;
    }

    public void setApprovedApplications(long approvedApplications) {
        this.approvedApplications = approvedApplications;
    }

    public long getRejectedApplications() {
        return rejectedApplications;
    }

    public void setRejectedApplications(long rejectedApplications) {
        this.rejectedApplications = rejectedApplications;
    }

    public Map<String, Long> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public void setApplicationsByStatus(Map<String, Long> applicationsByStatus) {
        this.applicationsByStatus = applicationsByStatus;
    }

    public Map<String, Long> getMonthlyApplications() {
        return monthlyApplications;
    }

    public void setMonthlyApplications(Map<String, Long> monthlyApplications) {
        this.monthlyApplications = monthlyApplications;
    }

    public Map<String, Long> getScholarshipWiseApplications() {
        return scholarshipWiseApplications;
    }

    public void setScholarshipWiseApplications(Map<String, Long> scholarshipWiseApplications) {
        this.scholarshipWiseApplications = scholarshipWiseApplications;
    }

    public Map<String, Long> getCategoryWiseDistribution() {
        return categoryWiseDistribution;
    }

    public void setCategoryWiseDistribution(Map<String, Long> categoryWiseDistribution) {
        this.categoryWiseDistribution = categoryWiseDistribution;
    }

    public List<ApplicationDTO> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<ApplicationDTO> recentApplications) {
        this.recentApplications = recentApplications;
    }

    public List<UserSummaryDTO> getRecentRegistrations() {
        return recentRegistrations;
    }

    public void setRecentRegistrations(List<UserSummaryDTO> recentRegistrations) {
        this.recentRegistrations = recentRegistrations;
    }
}

