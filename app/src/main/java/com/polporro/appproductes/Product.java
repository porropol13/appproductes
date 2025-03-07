package com.polporro.appproductes;

public class Product {

    /*
     String info = "Nom: " + productName + "\n" +
                            "Codi: " + code + "\n" +
                            "Al·lèrgens: " + allergens + "\n" +
                            "Quantitat: " + quantity + "\n" +
                            "Ingredients: " + ingredients + "\n" +
                            "Descripció: " + description + "\n" +
                            "Llocs: " + stores + "\n" +
                            "Països: " + countries;

                    productInfo.setText(info);
     */

    private String barcode;
    private String name;
    private int codi;
    private String allergens;
    private String ingredients;
    private String description;
    private String stores;
    private String countries;
    private String imageUrl;

    // Constructor, getters y setters

    public Product(String barcode, String name, int codi, String allergens, String ingredients, String description, String stores, String countries, String imageUrl) {
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

    public int getCodi(){
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

