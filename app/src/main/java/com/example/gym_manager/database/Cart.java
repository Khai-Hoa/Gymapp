package com.example.gym_manager.database;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "cart_tb")
public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String product_name;
    private Double price_product;
    private String url_product;
    private int quantity;
    private String name_customer;
    private String phone_customer;
    private String address_customer;
    private Double totalPrice;
    private Date purchaseTime;

    public Cart() {
    }

    public Cart(String product_name, Double price_product, String url_product, int quantity, String name_customer, String phone_customer, String address_customer, Double totalPrice,Date purchaseTime) {
        this.product_name = product_name;
        this.price_product = price_product;
        this.url_product = url_product;
        this.quantity = quantity;
        this.name_customer = name_customer;
        this.phone_customer = phone_customer;
        this.address_customer = address_customer;
        this.totalPrice = totalPrice;
        this.purchaseTime = purchaseTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Double getPrice_product() {
        return price_product;
    }

    public void setPrice_product(Double price_product) {
        this.price_product = price_product;
    }

    public String getUrl_product() {
        return url_product;
    }

    public void setUrl_product(String url_product) {
        this.url_product = url_product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName_customer() {
        return name_customer;
    }

    public void setName_customer(String name_customer) {
        this.name_customer = name_customer;
    }

    public String getPhone_customer() {
        return phone_customer;
    }

    public void setPhone_customer(String phone_customer) {
        this.phone_customer = phone_customer;
    }

    public String getAddress_customer() {
        return address_customer;
    }

    public void setAddress_customer(String address_customer) {
        this.address_customer = address_customer;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }
}
