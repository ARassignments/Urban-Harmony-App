package com.example.urbanharmony.Models;

public class SubCategoryModel {
    String id, name;

    public SubCategoryModel() {
    }

    public SubCategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
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
}
