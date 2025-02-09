package com.polporro.appproductes;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private TextView productNameTextView;
    private ListView commentsListView;
    private ArrayList<String> commentsList;
    private CommentsAdapter adapter;
    private String productBarcode;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Inicialitza Firebase
        FirebaseApp.initializeApp(this);

        productNameTextView = findViewById(R.id.productNameTextView);
        commentsListView = findViewById(R.id.commentsListView);

        // Obtenir el nom del producte i el codi de barres passat des de MainActivity
        productBarcode = getIntent().getStringExtra("productBarcode");
        productName = getIntent().getStringExtra("productName");

        // Mostrar el nom del producte
        productNameTextView.setText(productName);

        // Inicialitzar la llista de comentaris
        commentsList = new ArrayList<>();
        adapter = new CommentsAdapter(this, commentsList);
        commentsListView.setAdapter(adapter);

        // Obtenir comentaris de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("comments").child(productBarcode);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String comment = snapshot.getValue(String.class);
                    commentsList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar l'error
            }
        });
    }
}

