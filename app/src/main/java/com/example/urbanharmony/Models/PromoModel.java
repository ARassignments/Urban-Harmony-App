package com.example.urbanharmony.Models;

public class PromoModel {
    String id, name, detail, price;

    public PromoModel() {
    }

    public PromoModel(String id, String name, String detail, String price) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.price = price;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
