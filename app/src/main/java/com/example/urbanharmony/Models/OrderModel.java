package com.example.urbanharmony.Models;

public class OrderModel {
    String id, orderId, UID, totalItemAmount, totalShippingAmount, totalOverallAmount, shippingName, shippingDetail, locationName, locationAddress, paymentMethod, status, createdOn, items;

    public OrderModel() {
    }

    public OrderModel(String id, String orderId, String UID, String totalItemAmount, String totalShippingAmount, String totalOverallAmount, String shippingName, String shippingDetail, String locationName, String locationAddress, String paymentMethod, String status, String createdOn, String items) {
        this.id = id;
        this.orderId = orderId;
        this.UID = UID;
        this.totalItemAmount = totalItemAmount;
        this.totalShippingAmount = totalShippingAmount;
        this.totalOverallAmount = totalOverallAmount;
        this.shippingName = shippingName;
        this.shippingDetail = shippingDetail;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdOn = createdOn;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTotalItemAmount() {
        return totalItemAmount;
    }

    public void setTotalItemAmount(String totalItemAmount) {
        this.totalItemAmount = totalItemAmount;
    }

    public String getTotalShippingAmount() {
        return totalShippingAmount;
    }

    public void setTotalShippingAmount(String totalShippingAmount) {
        this.totalShippingAmount = totalShippingAmount;
    }

    public String getTotalOverallAmount() {
        return totalOverallAmount;
    }

    public void setTotalOverallAmount(String totalOverallAmount) {
        this.totalOverallAmount = totalOverallAmount;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingDetail() {
        return shippingDetail;
    }

    public void setShippingDetail(String shippingDetail) {
        this.shippingDetail = shippingDetail;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
