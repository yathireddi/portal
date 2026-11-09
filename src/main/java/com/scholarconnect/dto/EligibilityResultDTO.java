package com.scholarconnect.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EligibilityResultDTO {
    private boolean eligible;
    private Long scholarshipId;
    private String scholarshipName;

    // Parameters compared
    private BigDecimal studentPercentage;
    private BigDecimal requiredPercentage;

    private BigDecimal studentIncome;
    private BigDecimal maximumIncome;

    private String studentCategory;
    private String eligibleCategories;

    private String studentState;
    private String eligibleStates;

    private String studentCourse;
    private String eligibleCourses;

    private String studentEducationLevel;
    private String eligibleEducationLevel;

    private List<String> passedConditions = new ArrayList<>();
    private List<String> failedConditions = new ArrayList<>();

    public EligibilityResultDTO() {
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(Long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public String getScholarshipName() {
        return scholarshipName;
    }

    public void setScholarshipName(String scholarshipName) {
        this.scholarshipName = scholarshipName;
    }

    public BigDecimal getStudentPercentage() {
        return studentPercentage;
    }

    public void setStudentPercentage(BigDecimal studentPercentage) {
        this.studentPercentage = studentPercentage;
    }

    public BigDecimal getRequiredPercentage() {
        return requiredPercentage;
    }

    public void setRequiredPercentage(BigDecimal requiredPercentage) {
        this.requiredPercentage = requiredPercentage;
    }

    public BigDecimal getStudentIncome() {
        return studentIncome;
    }

    public void setStudentIncome(BigDecimal studentIncome) {
        this.studentIncome = studentIncome;
    }

    public BigDecimal getMaximumIncome() {
        return maximumIncome;
    }

    public void setMaximumIncome(BigDecimal maximumIncome) {
        this.maximumIncome = maximumIncome;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
    }

    public String getEligibleCategories() {
        return eligibleCategories;
    }

    public void setEligibleCategories(String eligibleCategories) {
        this.eligibleCategories = eligibleCategories;
    }

    public String getStudentState() {
        return studentState;
    }

    public void setStudentState(String studentState) {
        this.studentState = studentState;
    }

    public String getEligibleStates() {
        return eligibleStates;
    }

    public void setEligibleStates(String eligibleStates) {
        this.eligibleStates = eligibleStates;
    }

    public String getStudentCourse() {
        return studentCourse;
    }

    public void setStudentCourse(String studentCourse) {
        this.studentCourse = studentCourse;
    }

    public String getEligibleCourses() {
        return eligibleCourses;
    }

    public void setEligibleCourses(String eligibleCourses) {
        this.eligibleCourses = eligibleCourses;
    }

    public String getStudentEducationLevel() {
        return studentEducationLevel;
    }

    public void setStudentEducationLevel(String studentEducationLevel) {
        this.studentEducationLevel = studentEducationLevel;
    }

    public String getEligibleEducationLevel() {
        return eligibleEducationLevel;
    }

    public void setEligibleEducationLevel(String eligibleEducationLevel) {
        this.eligibleEducationLevel = eligibleEducationLevel;
    }

    public List<String> getPassedConditions() {
        return passedConditions;
    }

    public void setPassedConditions(List<String> passedConditions) {
        this.passedConditions = passedConditions;
    }

    public List<String> getFailedConditions() {
        return failedConditions;
    }

    public void setFailedConditions(List<String> failedConditions) {
        this.failedConditions = failedConditions;
    }
}

