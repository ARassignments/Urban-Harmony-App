package com.example.urbanharmony.Models;

public class AddressModel {
    String id, UID, name, address, defaultStatus;
    public AddressModel(){

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(String defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public AddressModel(String id, String UID, String name, String address, String defaultStatus) {
        this.id = id;
        this.UID = UID;
        this.name = name;
        this.address = address;
        this.defaultStatus = defaultStatus;
    }
}
