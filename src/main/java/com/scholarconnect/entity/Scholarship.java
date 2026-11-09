package com.scholarconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scholarships")
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scholarship_name", nullable = false, length = 255)
    private String scholarshipName;

    @Column(nullable = false, length = 200)
    private String provider;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "minimum_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal minimumPercentage = BigDecimal.ZERO;

    @Column(name = "maximum_family_income", precision = 12, scale = 2, nullable = false)
    private BigDecimal maximumFamilyIncome = new BigDecimal("9999999.00");

    @Column(name = "eligible_categories", length = 255, nullable = false)
    private String eligibleCategories = "ALL";

    @Column(name = "eligible_states", length = 255, nullable = false)
    private String eligibleStates = "ALL";

    @Column(name = "eligible_courses", length = 255, nullable = false)
    private String eligibleCourses = "ALL";

    @Column(name = "education_level", length = 100, nullable = false)
    private String educationLevel = "ALL";

    @Column(name = "application_start_date", nullable = false)
    private LocalDate applicationStartDate;

    @Column(name = "application_deadline", nullable = false)
    private LocalDate applicationDeadline;

    @Column(length = 30, nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Scholarship() {
    }

    public Scholarship(Long id, String scholarshipName, String provider, String description,
                       BigDecimal amount, BigDecimal minimumPercentage, BigDecimal maximumFamilyIncome,
                       String eligibleCategories, String eligibleStates, String eligibleCourses,
                       String educationLevel, LocalDate applicationStartDate, LocalDate applicationDeadline,
                       String status) {
        this.id = id;
        this.scholarshipName = scholarshipName;
        this.provider = provider;
        this.description = description;
        this.amount = amount;
        this.minimumPercentage = minimumPercentage;
        this.maximumFamilyIncome = maximumFamilyIncome;
        this.eligibleCategories = eligibleCategories;
        this.eligibleStates = eligibleStates;
        this.eligibleCourses = eligibleCourses;
        this.educationLevel = educationLevel;
        this.applicationStartDate = applicationStartDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMinimumPercentage() {
        return minimumPercentage;
    }

    public void setMinimumPercentage(BigDecimal minimumPercentage) {
        this.minimumPercentage = minimumPercentage;
    }

    public BigDecimal getMaximumFamilyIncome() {
        return maximumFamilyIncome;
    }

    public void setMaximumFamilyIncome(BigDecimal maximumFamilyIncome) {
        this.maximumFamilyIncome = maximumFamilyIncome;
    }

    public String getEligibleCategories() {
        return eligibleCategories;
    }

    public void setEligibleCategories(String eligibleCategories) {
        this.eligibleCategories = eligibleCategories;
    }

    public String getEligibleStates() {
        return eligibleStates;
    }

    public void setEligibleStates(String eligibleStates) {
        this.eligibleStates = eligibleStates;
    }

    public String getEligibleCourses() {
        return eligibleCourses;
    }

    public void setEligibleCourses(String eligibleCourses) {
        this.eligibleCourses = eligibleCourses;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
