package com.example.gym_manager.model;

public class Device {
    private String id;
    private  String url;
    private String name;
    private String price;
    private String quantity;
    private String dateBuy;
    private String dateFix;
    private Boolean isFix;
    private double maintenanceCost;
    private String maintenanceQuantity;
    private String maintenanceStatus;

    public Device() {
    }

    public Device(String id, String url, String name, String price, String quantity, String dateBuy, String dateFix, Boolean isFix, double maintenanceCost, String maintenanceQuantity, String maintenanceStatus) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.dateBuy = dateBuy;
        this.dateFix = dateFix;
        this.isFix = isFix;
        this.maintenanceCost = maintenanceCost;
        this.maintenanceQuantity = maintenanceQuantity;
        this.maintenanceStatus = maintenanceStatus;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDateBuy() {
        return dateBuy;
    }

    public void setDateBuy(String dateBuy) {
        this.dateBuy = dateBuy;
    }

    public String getDateFix() {
        return dateFix;
    }

    public void setDateFix(String dateFix) {
        this.dateFix = dateFix;
    }

    public Boolean getFix() {
        return isFix;
    }

    public void setFix(Boolean fix) {
        isFix = fix;
    }

    public double getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public String getMaintenanceQuantity() {
        return maintenanceQuantity;
    }

    public void setMaintenanceQuantity(String maintenanceQuantity) {
        this.maintenanceQuantity = maintenanceQuantity;
    }

    public String getMaintenanceStatus() {
        return maintenanceStatus;
    }

    public void setMaintenanceStatus(String maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }
}
