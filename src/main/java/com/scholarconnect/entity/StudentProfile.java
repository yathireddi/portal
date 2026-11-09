package com.scholarconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 50, nullable = false)
    private String category = "General";

    @Column(length = 100, nullable = false)
    private String state = "";

    @Column(length = 100, nullable = false)
    private String district = "";

    @Column(name = "college_name", length = 200, nullable = false)
    private String collegeName = "";

    @Column(name = "university_name", length = 200, nullable = false)
    private String universityName = "";

    @Column(length = 150, nullable = false)
    private String course = "";

    @Column(name = "current_year", nullable = false)
    private Integer currentYear = 1;

    @Column(name = "academic_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal academicPercentage = BigDecimal.ZERO;

    @Column(name = "annual_family_income", precision = 12, scale = 2, nullable = false)
    private BigDecimal annualFamilyIncome = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String address;

    public StudentProfile() {
    }

    public StudentProfile(User user, String category, String state, String district,
                          String collegeName, String universityName, String course,
                          Integer currentYear, BigDecimal academicPercentage,
                          BigDecimal annualFamilyIncome, String address) {
        this.user = user;
        this.category = category;
        this.state = state;
        this.district = district;
        this.collegeName = collegeName;
        this.universityName = universityName;
        this.course = course;
        this.currentYear = currentYear;
        this.academicPercentage = academicPercentage;
        this.annualFamilyIncome = annualFamilyIncome;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public BigDecimal getAcademicPercentage() {
        return academicPercentage;
    }

    public void setAcademicPercentage(BigDecimal academicPercentage) {
        this.academicPercentage = academicPercentage;
    }

    public BigDecimal getAnnualFamilyIncome() {
        return annualFamilyIncome;
    }

    public void setAnnualFamilyIncome(BigDecimal annualFamilyIncome) {
        this.annualFamilyIncome = annualFamilyIncome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

