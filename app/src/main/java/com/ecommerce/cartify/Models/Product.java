package com.ecommerce.cartify.Models;

public class Product {

    private int prod_id;
    private String prod_name;
    private int price;
    private int quantity;
    private String seller;
    private String category;
    private String image_url;

    public Product(int prod_id, String prod_name, int price, int quantity, String seller, String category, String image_url) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
        this.category = category;
        this.image_url = image_url;
    }

    public Product() {
    }

    public int getProd_id() {
        return prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
