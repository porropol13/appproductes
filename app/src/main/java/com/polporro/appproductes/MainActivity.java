package com.polporro.appproductes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText barcodeInput;
    private Button scanButton, scanCameraButton, commentButton;
    private EditText commentInput;
    private ListView productList;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de Firebase
        FirebaseApp.initializeApp(this);

        // Vinculación de vistas
        barcodeInput     = findViewById(R.id.barcodeInput);
        scanButton       = findViewById(R.id.scanButton);
        scanCameraButton = findViewById(R.id.scanCameraButton);
        commentInput     = findViewById(R.id.commentInput);
        commentButton    = findViewById(R.id.commentButton);
        productList      = findViewById(R.id.productList);

        // Acción para buscar producto manualmente
        scanButton.setOnClickListener(v -> {
            String barcode = barcodeInput.getText().toString().trim();
            if (!barcode.isEmpty()) {
                new FetchProductInfo().execute(barcode);
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

        // Acción para guardar comentario
        commentButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            String barcode  = barcodeInput.getText().toString().trim();
            if (!barcode.isEmpty() && !comment.isEmpty()) {
                saveComment(barcode, comment);
                commentInput.setText("");
            }
        });
    }

    private void saveComment(String barcode, String comment) {
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("comments")
                .child(barcode);
        database.push().setValue(comment);
    }

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

                // --- Extraemos todos los campos, incluido "quantity" ---
                String name        = productObject.optString("product_name", "No disponible");
                String brand       = productObject.optString("brands", "No disponible");
                String quantity    = productObject.optString("quantity", "No disponible"); // <-- AQUÍ
                String allergens   = productObject.optString("allergens", "No disponible");
                String ingredients = productObject.optString("ingredients_text", "No disponible");
                String description = productObject.optString("generic_name", "No disponible");
                String stores      = productObject.optString("stores", "No disponible");
                String countries   = productObject.optString("countries", "No disponible");
                String imageUrl    = productObject.optString("image_url", "");

                // --- Llamamos al constructor con 10 parámetros ---
                return new Product(
                        barcode,
                        name,
                        brand,
                        quantity,       // <-- PASAMOS quantity como cuarto parámetro
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
                // Guardar o actualizar en la base de datos local
                ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(MainActivity.this);
                Product existingProduct = dbHelper.getProduct(product.getBarcode());
                if (existingProduct == null) {
                    dbHelper.addProduct(product);
                } else {
                    dbHelper.updateProduct(product);
                }

                // Lanzar detalle
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            } else {
                Log.e("FetchProductInfo", "No se pudo obtener la información del producto");
            }
        }
    }

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
}
