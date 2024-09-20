package com.example.urbanharmony.Models;

public class WishlistModel {
    String id, UID, PID;

    public WishlistModel() {
    }
    public WishlistModel(String id, String UID, String PID) {
        this.id = id;
        this.UID = UID;
        this.PID = PID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }
}
