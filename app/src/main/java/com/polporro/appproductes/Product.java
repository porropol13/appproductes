package com.polporro.appproductes;

public class Product {

    private String barcode;
    private String name;
    private String allergens;
    private String ingredients;
    private String description;
    private String imageUrl;

    // Constructor, getters y setters

    public Product(String barcode, String name, String allergens, String ingredients, String description, String imageUrl) {
        this.barcode = barcode;
        this.name = name;
        this.allergens = allergens;
        this.ingredients = ingredients;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

