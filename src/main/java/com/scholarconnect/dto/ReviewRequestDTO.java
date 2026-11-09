package com.scholarconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequestDTO {

    @NotBlank(message = "Review comments are required")
    private String comments;

    @NotBlank(message = "Recommendation is required")
    private String recommendation; // RECOMMEND_APPROVAL, RECOMMEND_REJECTION, REQUEST_MORE_INFO

    public ReviewRequestDTO() {
    }

    public ReviewRequestDTO(String comments, String recommendation) {
        this.comments = comments;
        this.recommendation = recommendation;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

