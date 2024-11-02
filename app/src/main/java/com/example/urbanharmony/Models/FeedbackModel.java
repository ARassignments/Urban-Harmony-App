package com.example.urbanharmony.Models;

public class FeedbackModel {
    String id, rating, review, userId, designerId, reviewDate, replyRating, replyReview, replyReviewDate;

    public FeedbackModel() {
    }

    public FeedbackModel(String id, String rating, String review, String userId, String designerId, String reviewDate, String replyRating, String replyReview, String replyReviewDate) {
        this.id = id;
        this.rating = rating;
        this.review = review;
        this.userId = userId;
        this.designerId = designerId;
        this.reviewDate = reviewDate;
        this.replyRating = replyRating;
        this.replyReview = replyReview;
        this.replyReviewDate = replyReviewDate;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReplyRating() {
        return replyRating;
    }

    public void setReplyRating(String replyRating) {
        this.replyRating = replyRating;
    }

    public String getReplyReview() {
        return replyReview;
    }

    public void setReplyReview(String replyReview) {
        this.replyReview = replyReview;
    }

    public String getRepltReviewDate() {
        return replyReviewDate;
    }

    public void setRepltReviewDate(String replyReviewDate) {
        this.replyReviewDate = replyReviewDate;
    }
}
