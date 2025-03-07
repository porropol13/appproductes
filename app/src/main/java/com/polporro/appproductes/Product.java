package com.polporro.appproductes;

public class Product {

    private String barcode;
    private String name;
    private String codi;
    private String allergens;
    private String ingredients;
    private String description;
    private String stores;
    private String countries;
    private String imageUrl;

    // Constructor, getters y setters

    public Product(String barcode, String name, String allergens, String ingredients, String description, String stores, String countries) {
        this.barcode = barcode;
        this.name = name;
        this.codi = codi;
        this.allergens = allergens;
        this.ingredients = ingredients;
        this.description = description;
        this.stores = stores;
        this.countries = countries;
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getCodi(){
        return codi;
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

    public String getStores(){
        return stores;
    }

    public String getCountries(){
        return countries;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

