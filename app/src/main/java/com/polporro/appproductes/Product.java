package com.polporro.appproductes;

public class Product {

    private String barcode;
    private String name;
    private String allergens;
    private String ingredients;
    private String description;
    private String stores;
    private String countries;
    private String imageUrl;

    public Product(String barcode, String name, String allergens, String ingredients, String description, String stores, String countries, String imageUrl) {
        this.barcode = barcode;
        this.name = name;
        this.allergens = allergens;
        this.ingredients = ingredients;
        this.description = description;
        this.stores = stores;
        this.countries = countries;
        this.imageUrl = imageUrl;
    }

    // Getters y setters
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAllergens() {
        return allergens;
    }
    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }
    public String getIngredients() {
        return ingredients;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStores() {
        return stores;
    }
    public void setStores(String stores) {
        this.stores = stores;
    }
    public String getCountries() {
        return countries;
    }
    public void setCountries(String countries) {
        this.countries = countries;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}