package com.example.urbanharmony.Models;

public class ProductFeedbackModel {
    String id, rating, review, PID, UID, reviewDate;

    public ProductFeedbackModel() {
    }

    public ProductFeedbackModel(String id, String rating, String review, String PID, String UID, String reviewDate) {
        this.id = id;
        this.rating = rating;
        this.review = review;
        this.PID = PID;
        this.UID = UID;
        this.reviewDate = reviewDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
}
