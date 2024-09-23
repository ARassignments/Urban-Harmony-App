package com.example.urbanharmony.Models;

public class ScheduleModel {
    String id, name, userId, Slots;

    public ScheduleModel() {
    }

    public ScheduleModel(String id, String name, String userId, String Slots) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.Slots = Slots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSlots() {
        return Slots;
    }

    public void setSlots(String slots) {
        this.Slots = slots;
    }
}
