package com.example.gym_manager.model;

public class Product {
    private String product_id;
    private String product_name;
    private String url;
    private Double product_price;

    public Product() {
    }

    public Product(String product_id,String product_name, String url, Double product_price) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.url = url;
        this.product_price = product_price;

    }



    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Double product_price) {
        this.product_price = product_price;
    }
}
