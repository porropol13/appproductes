package com.polporro.appproductes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
    private TextView productInfo;
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
        barcodeInput = findViewById(R.id.barcodeInput);
        scanButton = findViewById(R.id.scanButton);
        scanCameraButton = findViewById(R.id.scanCameraButton);
        productInfo = findViewById(R.id.productInfo);
        commentInput = findViewById(R.id.commentInput);
        commentButton = findViewById(R.id.commentButton);
        productList = findViewById(R.id.productList);

        // Acción para buscar producto por código de barras
        scanButton.setOnClickListener(v -> {
            String barcode = barcodeInput.getText().toString();
            if (!barcode.isEmpty()) {
                new FetchProductInfo().execute(barcode);
            }
        });

        // Acción para escanear código de barras con cámara
        scanCameraButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setPrompt("Escanea un código de barras");
            integrator.setBeepEnabled(true);
            integrator.setCaptureActivity(CaptureActivity.class);
            integrator.initiateScan();
        });

        // Acción para guardar comentario
        commentButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString();
            if (!comment.isEmpty()) {
                saveComment(barcodeInput.getText().toString(), comment);
                commentInput.setText("");
            }
        });
    }

    private void saveComment(String barcode, String comment) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("comments").child(barcode);
        // Utilizamos push() para agregar un comentario sin sobrescribir los existentes
        database.push().setValue(comment);
    }

    private class FetchProductInfo extends AsyncTask<String, Void, Product> {
        @Override
        protected Product doInBackground(String... params) {
            String barcode = params[0];
            try {
                URL url = new URL("https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                Log.d("FetchProductInfo", "Response Code: " + responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();

                Log.d("FetchProductInfo", "API Response: " + stringBuilder.toString());

                // Parseo de la respuesta JSON
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONObject productObject = jsonObject.getJSONObject("product");

                // Extraer datos necesarios
                String name = productObject.optString("product_name", "No disponible");
                String allergens = productObject.optString("allergens", "No disponible");
                String ingredients = productObject.optString("ingredients_text", "No disponible");
                String description = productObject.optString("description", "No disponible");
                String stores = productObject.optString("stores", "No disponible");
                String countries = productObject.optString("countries", "No disponible");
                String imageUrl = productObject.optString("image_url", "No disponible");

                return new Product(barcode, name, allergens, ingredients, description, stores, countries, imageUrl);
            } catch (Exception e) {
                Log.e("FetchProductInfo", "Error al obtener la información del producto", e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product product) {
            if (product != null) {
                String productDetails = "Nombre: " + product.getName() + "\n"
                        + "Alérgenos: " + product.getAllergens() + "\n"
                        + "Ingredientes: " + product.getIngredients() + "\n"
                        + "Descripción: " + product.getDescription() + "\n"
                        + "Tiendas: " + product.getStores() + "\n"
                        + "Países: " + product.getCountries() + "\n"
                        + "Imagen: " + product.getImageUrl();

                Log.d("FetchProductInfo", "Detalles del producto: " + productDetails);

                productInfo.setText(productDetails);

                // Guardar o actualizar producto en la base de datos local
                ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(MainActivity.this);
                Product existingProduct = dbHelper.getProduct(product.getBarcode());
                if (existingProduct == null) {
                    dbHelper.addProduct(product);
                } else {
                    dbHelper.updateProduct(product);
                }
            } else {
                productInfo.setText("No se pudo obtener la información.");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                productInfo.setText("Escaneo cancelado");
            } else {
                barcodeInput.setText(result.getContents());
                new FetchProductInfo().execute(result.getContents());
            }
        }
    }
}