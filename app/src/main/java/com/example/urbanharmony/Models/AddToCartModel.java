package com.example.urbanharmony.Models;

public class AddToCartModel {
    String id, PID, UID, qty;

    public AddToCartModel(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public AddToCartModel(String id, String PID, String UID, String qty) {
        this.id = id;
        this.PID = PID;
        this.UID = UID;
        this.qty = qty;
    }
}
