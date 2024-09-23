package com.example.urbanharmony.Models;

public class ProjectModel {
    String id, pName, pImage, pDesc, userId;

    public ProjectModel() {
    }

    public ProjectModel(String id, String pName, String pImage, String pDesc, String userId) {
        this.id = id;
        this.pName = pName;
        this.pImage = pImage;
        this.pDesc = pDesc;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
