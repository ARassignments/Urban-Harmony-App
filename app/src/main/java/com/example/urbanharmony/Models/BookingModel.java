package com.example.urbanharmony.Models;

public class BookingModel {
    String id, designerId, userId, day, slot, onBooking, status;

    public BookingModel() {
    }

    public BookingModel(String id, String designerId, String userId, String day, String slot, String onBooking, String status) {
        this.id = id;
        this.designerId = designerId;
        this.userId = userId;
        this.day = day;
        this.slot = slot;
        this.onBooking = onBooking;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getOnBooking() {
        return onBooking;
    }

    public void setOnBooking(String onBooking) {
        this.onBooking = onBooking;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
