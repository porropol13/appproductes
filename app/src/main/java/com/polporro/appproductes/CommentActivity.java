package com.polporro.appproductes;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CommentActivity extends AppCompatActivity {

    private TextView productName, productCodi, productStores, productCountries, productAllergens, productIngredients, productDescription, commentText;
    private EditText newCommentInput;
    private ImageView productImage;
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
        productCodi = findViewById(R.id.productCodi);
        productStores = findViewById(R.id.productStores);
        productCountries = findViewById(R.id.productCountries);
        commentText = findViewById(R.id.commentText);
        newCommentInput = findViewById(R.id.newCommentInput);
        productImage = findViewById(R.id.productImage);

        // Obtener el código de barras y el nombre del producto desde el Intent
        barcode = getIntent().getStringExtra("productBarcode");
        String name = getIntent().getStringExtra("productName");

        if (name != null) {
            productName.setText("Product Name: " + name);
        } else {
            productName.setText("Product Name: Not available");
        }

        // Cargar detalles y comentarios
        loadProductDetails(barcode);
        loadComments(barcode);
        setupSaveCommentButton();
    }

    private void loadProductDetails(String barcode) {
        // Simulación de datos (reemplaza esto con tu lógica de base de datos)
        Product product = new Product(
                barcode,
                "Sample Product",
                "Sample Stores",
                "Sample Countries",
                "Sample Allergens",
                "Sample Ingredients",
                "Sample Description",
                "https://via.placeholder.com/200" // URL de imagen de ejemplo
        );

        if (product != null) {
            productAllergens.setText("Allergens: " + (product.getAllergens() != null ? product.getAllergens() : "Not available"));
            productIngredients.setText("Ingredients: " + (product.getIngredients() != null ? product.getIngredients() : "Not available"));
            productDescription.setText("Description: " + (product.getDescription() != null ? product.getDescription() : "Not available"));
            productCodi.setText("Barcode: " + product.getBarcode());
            productStores.setText("Stores: " + (product.getStores() != null ? product.getStores() : "Not available"));
            productCountries.setText("Countries: " + (product.getCountries() != null ? product.getCountries() : "Not available"));

            String imageUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("No disponible")) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(productImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher)
                        .into(productImage);
            }
        } else {
            productAllergens.setText("Allergens: Not available");
            productIngredients.setText("Ingredients: Not available");
            productDescription.setText("Description: Not available");
            productCodi.setText("Barcode: Not available");
            productStores.setText("Stores: Not available");
            productCountries.setText("Countries: Not available");
            Toast.makeText(this, "Product details not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadComments(String barcode) {
        commentText.setText("Loading comments..."); // Mensaje mientras se cargan los comentarios
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(barcode);

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder allComments = new StringBuilder();
                allComments.append("Comments:\n");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String comment = snapshot.getValue(String.class);
                    if (comment != null) {
                        allComments.append("- ").append(comment).append("\n");
                    }
                }

                if (allComments.length() > 10) {
                    commentText.setText(allComments.toString());
                } else {
                    commentText.setText("Comments: No comments yet.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                commentText.setText("Comments: Error loading comments.");
                Toast.makeText(CommentActivity.this, "Error loading comments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSaveCommentButton() {
        Button saveCommentButton = findViewById(R.id.saveCommentButton);
        saveCommentButton.setOnClickListener(v -> {
            String comment = newCommentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                saveComment(barcode, comment);
                newCommentInput.setText("");
            } else {
                Toast.makeText(CommentActivity.this, "Please write a comment before saving", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveComment(String barcode, String comment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(barcode);
        String commentId = commentsRef.push().getKey();
        if (commentId != null) {
            commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Comment added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, "Error saving comment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}