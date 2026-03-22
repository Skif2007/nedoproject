package com.example.test_api;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private String imageURL;

    public Product(int id, String name, double price, String description, String imageURL) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageURL; }
}