package com.polporro.appproductes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText barcodeInput;
    private Button scanButton, scanCameraButton, commentButton;
    private EditText commentInput;
    private ListView productList;

    // --- Firebase ---
    private DatabaseReference historyRef;

    // --- Para el ListView de historial ---
    private List<Product> historyProducts;
    private ArrayAdapter<String> historyAdapter;
    private List<String> historyDisplayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase (si no lo habías hecho ya)
        FirebaseApp.initializeApp(this);
        historyRef = FirebaseDatabase.getInstance().getReference("history");

        // Vincular vistas
        barcodeInput     = findViewById(R.id.barcodeInput);
        scanButton       = findViewById(R.id.scanButton);
        scanCameraButton = findViewById(R.id.scanCameraButton);
        commentInput     = findViewById(R.id.commentInput);
        commentButton    = findViewById(R.id.commentButton);
        productList      = findViewById(R.id.productList);

        // Inicializar lista y adaptador para el historial de Firebase
        historyProducts   = new ArrayList<>();
        historyDisplayList = new ArrayList<>();
        historyAdapter     = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                historyDisplayList
        );
        productList.setAdapter(historyAdapter);

        // Cargar historial desde Firebase en cuanto inicie la activity
        loadHistoryFromFirebase();

        // Acción para buscar producto manualmente
        scanButton.setOnClickListener(v -> {
            String barcode = barcodeInput.getText().toString().trim();
            if (!barcode.isEmpty()) {
                new FetchProductInfo().execute(barcode);
            } else {
                Toast.makeText(MainActivity.this,
                        "Introduce un código de barras válido",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para escanear con cámara (ZXing)
        scanCameraButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setPrompt("Escanea un código de barras");
            integrator.setBeepEnabled(true);
            integrator.setCaptureActivity(CaptureActivity.class);
            integrator.initiateScan();
        });

        // Acción para guardar comentario en Firebase
        commentButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            String barcode  = barcodeInput.getText().toString().trim();
            if (!barcode.isEmpty() && !comment.isEmpty()) {
                saveComment(barcode, comment);
                commentInput.setText("");
                Toast.makeText(MainActivity.this,
                        "Comentario agregado",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Debe introducir un comentario y un código bien escaneado",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Al hacer clic en un elemento del historial, abrimos detalle
        productList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Product clicked = historyProducts.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", clicked);
            startActivity(intent);
        });
    }

    // === Método que lee todos los productos de "history" en Firebase y actualiza la lista ===
    private void loadHistoryFromFirebase() {
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyProducts.clear();
                historyDisplayList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    // Cada child es un Product serializado
                    Product p = child.getValue(Product.class);
                    if (p != null) {
                        historyProducts.add(p);
                        // Para la vista, mostramos "Nombre (Marca)" o el barcode si falta nombre
                        String line = p.getName();
                        if (line == null || line.trim().isEmpty()) {
                            line = p.getBarcode();
                        }
                        String brand = p.getBrand();
                        if (brand != null && !brand.trim().isEmpty()) {
                            line += " (" + brand + ")";
                        }
                        historyDisplayList.add(line);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Error al cargar historial de Firebase: " + error.getMessage());
            }
        });
    }

    // Guarda un comentario bajo un nodo "comments/{barcode}" en Firebase
    private void saveComment(String barcode, String comment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance()
                .getReference("comments")
                .child(barcode);
        commentsRef.push().setValue(comment);
    }

    // Recibir resultado de ZXing y lanzar búsqueda
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            String barcode = result.getContents().trim();
            barcodeInput.setText(barcode);
            new FetchProductInfo().execute(barcode);
        }
    }

    // === AsyncTask para consultar Open Food Facts, guardar en SQLite y en Firebase ===
    private class FetchProductInfo extends AsyncTask<String, Void, Product> {
        @Override
        protected Product doInBackground(String... params) {
            String barcode = params[0];
            try {
                URL url = new URL("https://world.openfoodfacts.org/api/v0/product/"
                        + barcode + ".json");
                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                Log.d("FetchProductInfo", "Response Code: " + responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONObject productObject = jsonObject.getJSONObject("product");

                // Extraer todos los campos, incluido "quantity"
                String name        = productObject.optString("product_name", "No disponible");
                String brand       = productObject.optString("brands", "No disponible");
                String quantity    = productObject.optString("quantity", "No disponible");
                String allergens   = productObject.optString("allergens", "No disponible");
                String ingredients = productObject.optString("ingredients_text", "No disponible");
                String description = productObject.optString("generic_name", "No disponible");
                String stores      = productObject.optString("stores", "No disponible");
                String countries   = productObject.optString("countries", "No disponible");
                String imageUrl    = productObject.optString("image_url", "");

                return new Product(
                        barcode,
                        name,
                        brand,
                        quantity,
                        allergens,
                        ingredients,
                        description,
                        stores,
                        countries,
                        imageUrl
                );
            } catch (Exception e) {
                Log.e("FetchProductInfo", "Error al obtener info del producto", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product product) {
            if (product != null) {
                // 1) Guardar en SQLite local (mantenemos tu lógica existente)
                ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(MainActivity.this);
                Product existingProduct = dbHelper.getProduct(product.getBarcode());
                if (existingProduct == null) {
                    dbHelper.addProduct(product);
                } else {
                    dbHelper.updateProduct(product);
                }

                // 2) Guardar en Firebase en "history/{pushId}"
                historyRef.push().setValue(product);

                // 3) Lanzar ProductDetailActivity para ver el detalle
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this,
                        "No se encontró información del producto",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
