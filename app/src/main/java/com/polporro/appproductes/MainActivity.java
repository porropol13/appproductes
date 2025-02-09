package com.polporro.appproductes;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

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

        // Configuración de la lista de productos
        ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(MainActivity.this);
        Cursor cursor = dbHelper.getAllProducts();
        String[] fromColumns = {ProductDatabaseHelper.COLUMN_NAME, ProductDatabaseHelper.COLUMN_ALLERGENS};
        int[] toViews = {R.id.productName, R.id.productAllergens};
        adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.product_item, cursor, fromColumns, toViews, 0);
        productList.setAdapter(adapter);

        // Acción al hacer clic en un producto de la lista
        productList.setOnItemClickListener((parent, view, position, id) -> {
            Cursor clickedItemCursor = (Cursor) adapter.getItem(position);
            int barcodeColumnIndex = clickedItemCursor.getColumnIndex(ProductDatabaseHelper.COLUMN_BARCODE);
            int nameColumnIndex = clickedItemCursor.getColumnIndex(ProductDatabaseHelper.COLUMN_NAME);
            if (barcodeColumnIndex >= 0 && nameColumnIndex >= 0) {
                String productBarcode = clickedItemCursor.getString(barcodeColumnIndex);
                String productName = clickedItemCursor.getString(nameColumnIndex);

                Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                intent.putExtra("productBarcode", productBarcode);
                intent.putExtra("productName", productName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private class FetchProductInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String barcode = params[0];
            String result = "";
            try {
                URL url = new URL("https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("status") == 1) {
                    JSONObject product = jsonObject.getJSONObject("product");
                    String productName = product.optString("product_name", "Desconegut");
                    String allergens = product.optString("allergens", "No especificat");
                    String ingredients = product.optString("ingredients_text", "No disponible");
                    String description = product.optString("description", "No hi ha descripcio");

                    String info = "Nom: " + productName + "\nAl·lèrgens: " + allergens + "\nIngredients: " + ingredients;
                    productInfo.setText(info);

                    ProductDatabaseHelper dbHelper = new ProductDatabaseHelper(MainActivity.this);
                    dbHelper.addProduct(barcodeInput.getText().toString(), productName, allergens, ingredients, description);
                    // Guardar producto en Firebase
                    saveProductToFirebase(barcodeInput.getText().toString(), productName, allergens, ingredients);

                    Cursor cursor = dbHelper.getAllProducts();
                    adapter.changeCursor(cursor);
                    adapter.notifyDataSetChanged();
                } else {
                    productInfo.setText("Producte no trobat.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                productInfo.setText("Error obteniendo la información del producto.");
            }
        }

        // Método para guardar producto en Firebase
        private void saveProductToFirebase(String barcode, String productName, String allergens, String ingredients) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("products").child(barcode);

            ref.child("name").setValue(productName);
            ref.child("allergens").setValue(allergens);
            ref.child("ingredients").setValue(ingredients);
        }
    }

    // Método para guardar comentarios en Firebase
    private void saveComment(String barcode, String comment) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("comments").child(barcode);
        ref.push().setValue(comment);
    }

}
