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
    private ImageView productImage;  // Vista para la imagen del producto
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

        // Verificar si el nombre es nulo
        if (name != null) {
            productName.setText("Product Name: " + name);
        } else {
            productName.setText("Product Name: Not available");
        }

        // Cargar los detalles del producto
        loadProductDetails(barcode);

        // Cargar los comentarios desde Firebase
        loadComments(barcode);
    }

    private void loadProductDetails(String barcode) {
        ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(this);
        Product product = dbHelper.getProductByBarcode(barcode);

        if (product != null) {
            // Mostrar los detalles del producto
            productAllergens.setText("Allergens: " + (product.getAllergens() != null ? product.getAllergens() : "Not available"));
            productIngredients.setText("Ingredients: " + (product.getIngredients() != null ? product.getIngredients() : "Not available"));
            productDescription.setText("Description: " + (product.getDescription() != null ? product.getDescription() : "Not available"));

            // Cargar la imagen del producto usando Glide
            String imageUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher)  // Imagen predeterminada mientras se carga
                        .error(R.drawable.ic_launcher)  // Imagen en caso de error
                        .into(productImage);
            } else {
                // Si la imagen no está disponible, usa un marcador de posición
                Glide.with(this)
                        .load(R.drawable.ic_launcher)
                        .into(productImage);
            }
        } else {
            // Si el producto no se encuentra en la base de datos, muestra un mensaje predeterminado
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
            } else {
                // Si ocurre un error al obtener los comentarios, muestra un mensaje
                commentText.setText("Comments: Error loading comments.");
            }
        });
    }
}
