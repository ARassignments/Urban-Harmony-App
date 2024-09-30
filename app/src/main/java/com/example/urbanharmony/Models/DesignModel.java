package com.example.urbanharmony.Models;

public class DesignModel {
    String id, dName, dImage, userId;

    public DesignModel() {
    }

    public DesignModel(String id, String dName, String dImage, String userId) {
        this.id = id;
        this.dName = dName;
        this.dImage = dImage;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdImage() {
        return dImage;
    }

    public void setdImage(String dImage) {
        this.dImage = dImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
