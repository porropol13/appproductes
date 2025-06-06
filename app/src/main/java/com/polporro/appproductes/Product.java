package com.polporro.appproductes;

import java.io.Serializable;

public class Product implements Serializable {

    private String barcode;
    private String name;
    private String brand;
    private String quantity;     // NUEVO campo: cantidad
    private String allergens;
    private String ingredients;
    private String description;
    private String stores;
    private String countries;
    private String imageUrl;

    // Constructor vacío (para Serializable/Firebase)
    public Product() { }

    // Constructor completo con todos los campos (10 parámetros)
    public Product(String barcode,
                   String name,
                   String brand,
                   String quantity,
                   String allergens,
                   String ingredients,
                   String description,
                   String stores,
                   String countries,
                   String imageUrl) {
        this.barcode     = barcode;
        this.name        = name;
        this.brand       = brand;
        this.quantity    = quantity;
        this.allergens   = allergens;
        this.ingredients = ingredients;
        this.description = description;
        this.stores      = stores;
        this.countries   = countries;
        this.imageUrl    = imageUrl;
    }

    // --- Getters y setters ---

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

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
