package com.polporro.appproductes;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide; // Importa Glide
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AppCompatActivity;

public class CommentActivity extends AppCompatActivity {

    private TextView productName, productAllergens, productIngredients, productDescription, commentText;
    private ImageView productImage;  // Agregamos la vista para la imagen del producto
    private String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Vincular las vistas
        productName = findViewById(R.id.productName);
        productAllergens = findViewById(R.id.productAllergens);
        productIngredients = findViewById(R.id.productIngredients);
        productDescription = findViewById(R.id.productDescription);
        commentText = findViewById(R.id.commentText);
        productImage = findViewById(R.id.productImage);  // Inicializar ImageView

        // Obtener el código de barras y el nombre del producto desde el Intent
        barcode = getIntent().getStringExtra("productBarcode");
        String name = getIntent().getStringExtra("productName");

        // Mostrar el nombre del producto
        productName.setText("Product Name: " + name);

        // Cargar más detalles desde la base de datos o API
        loadProductDetails(barcode);

        // Cargar los comentarios desde Firebase
        loadComments(barcode);
    }

    private void loadProductDetails(String barcode) {
        // Aquí puedes obtener los detalles del producto desde la base de datos
        // o llamar a la API si no se han almacenado previamente.

        ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(this);
        Product product = dbHelper.getProductByBarcode(barcode);

        if (product != null) {
            productAllergens.setText("Allergens: " + product.getAllergens());
            productIngredients.setText("Ingredients: " + product.getIngredients());
            productDescription.setText("Description: " + product.getDescription());

            // Cargar la imagen del producto usando Glide
            String imageUrl = product.getImageUrl(); // Suponiendo que tienes la URL de la imagen
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher)  // Imagen predeterminada mientras se carga
                    .into(productImage);
        } else {
            productAllergens.setText("Allergens: Not available");
            productIngredients.setText("Ingredients: Not available");
            productDescription.setText("Description: Not available");
        }
    }

    private void loadComments(String barcode) {
        // Cargar los comentarios del producto desde Firebase
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(barcode);
        commentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Obtener el primer comentario (si existe)
                String comment = task.getResult().getValue(String.class);
                if (comment != null) {
                    commentText.setText("Comments: " + comment);
                } else {
                    commentText.setText("Comments: No comments yet.");
                }
            }
        });
    }
}
