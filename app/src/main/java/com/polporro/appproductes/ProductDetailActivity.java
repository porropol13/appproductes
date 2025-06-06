package com.polporro.appproductes;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    // Declaración de todas las vistas
    private TextView nameView;
    private TextView brandView;
    private TextView quantityView;     // NUEVO campo cantidad
    private TextView ingredientsView;
    private TextView allergensView;
    private TextView descriptionView;
    private TextView storesView;
    private TextView countriesView;
    private ImageView productImage;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Referenciar vistas por su ID (deben coincidir con el XML)
        nameView        = findViewById(R.id.nameTextView);
        brandView       = findViewById(R.id.brandTextView);
        quantityView    = findViewById(R.id.quantityTextView);
        ingredientsView = findViewById(R.id.ingredientsTextView);
        allergensView   = findViewById(R.id.allergensTextView);
        descriptionView = findViewById(R.id.descriptionTextView);
        storesView      = findViewById(R.id.storesTextView);
        countriesView   = findViewById(R.id.countriesTextView);
        productImage    = findViewById(R.id.productImageView);

        // Obtener objeto Product enviado desde MainActivity
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        // Rellenar cada campo o mostrar "Información no disponible"

        // Nombre
        String nombre = product.getName();
        if (nombre != null && !nombre.trim().isEmpty()) {
            nameView.setText(nombre);
        } else {
            nameView.setText("Información no disponible");
        }

        // Marca
        String marca = product.getBrand();
        if (marca != null && !marca.trim().isEmpty()) {
            brandView.setText("Marca: " + marca);
        } else {
            brandView.setText("Marca: Información no disponible");
        }

        // Cantidad
        String cantidad = product.getQuantity();
        if (cantidad != null && !cantidad.trim().isEmpty()) {
            quantityView.setText("Cantidad: " + cantidad);
        } else {
            quantityView.setText("Cantidad: Información no disponible");
        }

        // Ingredientes
        String ingredientes = product.getIngredients();
        if (ingredientes != null && !ingredientes.trim().isEmpty()) {
            ingredientsView.setText("Ingredientes: " + ingredientes);
        } else {
            ingredientsView.setText("Ingredientes: Información no disponible");
        }

        // Alérgenos
        String alergenos = product.getAllergens();
        if (alergenos != null && !alergenos.trim().isEmpty()) {
            allergensView.setText("Al·lèrgens: " + alergenos);
        } else {
            allergensView.setText("Al·lèrgens: Información no disponible");
        }

        // Descripción
        String descripcion = product.getDescription();
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            descriptionView.setText("Descripció: " + descripcion);
        } else {
            descriptionView.setText("Descripció: Información no disponible");
        }

        // Tiendas
        String tiendas = product.getStores();
        if (tiendas != null && !tiendas.trim().isEmpty()) {
            storesView.setText("Botigues: " + tiendas);
        } else {
            storesView.setText("Botigues: Información no disponible");
        }

        // Países
        String paises = product.getCountries();
        if (paises != null && !paises.trim().isEmpty()) {
            countriesView.setText("Països: " + paises);
        } else {
            countriesView.setText("Països: Información no disponible");
        }

        // Imagen (si existe URL)
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            productImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .into(productImage);
        } else {
            productImage.setVisibility(View.GONE);
        }
    }
}
