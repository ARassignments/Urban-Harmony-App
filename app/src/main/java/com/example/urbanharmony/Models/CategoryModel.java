package com.example.urbanharmony.Models;

public class CategoryModel {
    String id, name, image, SubCategory;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name, String image, String subCategory) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.SubCategory = subCategory;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubCategory() {
        return SubCategory;
    }

    public void setSubCategory(String subCategory) {
        this.SubCategory = subCategory;
    }
}
