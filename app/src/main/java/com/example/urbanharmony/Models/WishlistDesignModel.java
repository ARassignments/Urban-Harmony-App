package com.example.urbanharmony.Models;

public class WishlistDesignModel {
    String id, UID, DID;

    public WishlistDesignModel() {
    }
    public WishlistDesignModel(String id, String UID, String DID) {
        this.id = id;
        this.UID = UID;
        this.DID = DID;
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

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }
}
