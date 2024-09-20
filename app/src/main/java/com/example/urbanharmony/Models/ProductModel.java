package com.example.urbanharmony.Models;

public class ProductModel {
    String id, pName, pPrice, pStock, pDiscount, pImage, pDesc, pCategory, pSubcategory, pBrand, pStyle, status;

    public ProductModel() {
    }

    public ProductModel(String id, String pName, String pPrice, String pStock, String pDiscount, String pImage, String pDesc, String pCategory, String pSubcategory, String pBrand, String pStyle, String status) {
        this.id = id;
        this.pName = pName;
        this.pPrice = pPrice;
        this.pStock = pStock;
        this.pDiscount = pDiscount;
        this.pImage = pImage;
        this.pDesc = pDesc;
        this.pCategory = pCategory;
        this.pSubcategory = pSubcategory;
        this.pBrand = pBrand;
        this.pStyle = pStyle;
        this.status = status;
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

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpStock() {
        return pStock;
    }

    public void setpStock(String pStock) {
        this.pStock = pStock;
    }

    public String getpDiscount() {
        return pDiscount;
    }

    public void setpDiscount(String pDiscount) {
        this.pDiscount = pDiscount;
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

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }

    public String getpSubcategory() {
        return pSubcategory;
    }

    public void setpSubcategory(String pSubcategory) {
        this.pSubcategory = pSubcategory;
    }

    public String getpBrand() {
        return pBrand;
    }

    public void setpBrand(String pBrand) {
        this.pBrand = pBrand;
    }

    public String getpStyle() {
        return pStyle;
    }

    public void setpStyle(String pStyle) {
        this.pStyle = pStyle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
