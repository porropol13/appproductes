package com.polporro.appproductes;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    // 1. Declaración de vistas del producto
    private TextView nameView;
    private TextView brandView;
    private TextView quantityView;
    private TextView ingredientsView;
    private TextView allergensView;
    private TextView descriptionView;
    private TextView storesView;
    private TextView countriesView;
    private ImageView productImage;

    // 2. Vistas para comentarios
    private ListView commentsListView;
    private ArrayAdapter<String> commentsAdapter;
    private List<String> commentsList;

    // 3. Referencia al producto y a Firebase comments
    private Product product;
    private DatabaseReference commentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Inicializar Firebase (si aún no se hizo)
        FirebaseApp.initializeApp(this);

        // 1. Referenciar vistas por ID (deben coincidir con el XML)
        nameView        = findViewById(R.id.nameTextView);
        brandView       = findViewById(R.id.brandTextView);
        quantityView    = findViewById(R.id.quantityTextView);
        ingredientsView = findViewById(R.id.ingredientsTextView);
        allergensView   = findViewById(R.id.allergensTextView);
        descriptionView = findViewById(R.id.descriptionTextView);
        storesView      = findViewById(R.id.storesTextView);
        countriesView   = findViewById(R.id.countriesTextView);
        productImage    = findViewById(R.id.productImageView);

        // 2. Referenciar ListView de comentarios y configurar adapter
        commentsListView = findViewById(R.id.commentsListView);
        commentsList = new ArrayList<>();
        commentsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                commentsList
        );
        commentsListView.setAdapter(commentsAdapter);

        // 3. Obtener objeto Product enviado desde MainActivity
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            // Si no llegó ningún producto, cerramos la actividad
            finish();
            return;
        }

        // 4. Rellenar cada campo del producto (o “Info no disponible”)

        // Nombre
        String nombre = product.getName();
        if (nombre != null && !nombre.trim().isEmpty()
                && !nombre.equalsIgnoreCase(getString(R.string.no_disponible))) {
            nameView.setText(nombre);
        } else {
            nameView.setText(R.string.informaci_n_no_disponible);
        }

        // Marca
        String marca = product.getBrand();
        if (marca != null && !marca.trim().isEmpty()
                && !marca.equalsIgnoreCase(getString(R.string.no_disponible))) {
            brandView.setText(marca);
        } else {
            brandView.setText(R.string.informaci_n_no_disponible);
        }

        // Cantidad
        String cantidad = product.getQuantity();
        if (cantidad != null && !cantidad.trim().isEmpty()
                && !cantidad.equalsIgnoreCase(getString(R.string.no_disponible))) {
            quantityView.setText(cantidad);
        } else {
            quantityView.setText(R.string.informaci_n_no_disponible);
        }

        // Ingredientes
        String ingredientes = product.getIngredients();
        if (ingredientes != null && !ingredientes.trim().isEmpty()
                && !ingredientes.equalsIgnoreCase(getString(R.string.no_disponible))) {
            ingredientsView.setText(ingredientes);
        } else {
            ingredientsView.setText(R.string.informaci_n_no_disponible);
        }

        // Alérgenos
        String alergenos = product.getAllergens();
        if (alergenos != null && !alergenos.trim().isEmpty()
                && !alergenos.equalsIgnoreCase(getString(R.string.no_disponible))) {
            allergensView.setText(alergenos);
        } else {
            allergensView.setText(R.string.informaci_n_no_disponible);
        }

        // Descripción
        String descripcion = product.getDescription();
        if (descripcion != null && !descripcion.trim().isEmpty()
                && !descripcion.equalsIgnoreCase(getString(R.string.no_disponible))) {
            descriptionView.setText(descripcion);
        } else {
            descriptionView.setText(R.string.informaci_n_no_disponible);
        }

        // Tiendas
        String tiendas = product.getStores();
        if (tiendas != null && !tiendas.trim().isEmpty()
                && !tiendas.equalsIgnoreCase(getString(R.string.no_disponible))) {
            storesView.setText(tiendas);
        } else {
            storesView.setText(R.string.informaci_n_no_disponible);
        }

        // Países
        String paises = product.getCountries();
        if (paises != null && !paises.trim().isEmpty()
                && !paises.equalsIgnoreCase(getString(R.string.no_disponible))) {
            countriesView.setText(paises);
        } else {
            countriesView.setText(R.string.informaci_n_no_disponible);
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

        // 5. Inicializar referencia a los comentarios para este barcode
        String barcode = product.getBarcode();
        commentsRef = FirebaseDatabase.getInstance()
                .getReference("comments")
                .child(barcode);

        // 6. Cargar comentarios desde Firebase
        loadCommentsFromFirebase();
    }

    /**
     * Lee todos los comentarios almacenados en "comments/{barcode}" y los muestra en el ListView.
     */
    private void loadCommentsFromFirebase() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String comment = child.getValue(String.class);
                    if (comment != null) {
                        commentsList.add(comment);
                    }
                }
                // Si no hay comentarios, mostramos un texto informativo
                if (commentsList.isEmpty()) {
                    commentsList.add(getString(R.string.no_hay_comentarios_para_este_producto));
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        ProductDetailActivity.this,
                        getString(R.string.error_al_cargar_comentarios) + error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
