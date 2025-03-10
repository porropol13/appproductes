package com.polporro.appproductes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    // Incrementamos la versión para actualizar el esquema.
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "products.db";

    // Columnas de la tabla "products"
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALLERGENS = "allergens";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STORES = "stores";
    public static final String COLUMN_COUNTRIES = "countries";
    public static final String COLUMN_IMAGE_URL = "imageUrl";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Crear la tabla con el esquema actualizado
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE products ("
                + COLUMN_BARCODE + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_ALLERGENS + " TEXT, "
                + COLUMN_INGREDIENTS + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_STORES + " TEXT, "
                + COLUMN_COUNTRIES + " TEXT, "
                + COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    // Forzar actualización del esquema borrando la tabla y recreándola.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String barcode = cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String allergens = cursor.getString(cursor.getColumnIndex(COLUMN_ALLERGENS));
                String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String stores = cursor.getString(cursor.getColumnIndex(COLUMN_STORES));
                String countries = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRIES));
                String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));

                products.add(new Product(barcode, name, allergens, ingredients, description, stores, countries, imageUrl));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO products (" + COLUMN_BARCODE + ", " + COLUMN_NAME + ", " + COLUMN_ALLERGENS + ", "
                + COLUMN_INGREDIENTS + ", " + COLUMN_DESCRIPTION + ", " + COLUMN_STORES + ", " + COLUMN_COUNTRIES + ", "
                + COLUMN_IMAGE_URL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.execSQL(insertQuery, new Object[]{
                product.getBarcode(),
                product.getName(),
                product.getAllergens(),
                product.getIngredients(),
                product.getDescription(),
                product.getStores(),
                product.getCountries(),
                product.getImageUrl()
        });
        db.close();
    }

    public void updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE products SET " + COLUMN_NAME + " = ?, " + COLUMN_ALLERGENS + " = ?, "
                + COLUMN_INGREDIENTS + " = ?, " + COLUMN_DESCRIPTION + " = ?, " + COLUMN_STORES + " = ?, "
                + COLUMN_COUNTRIES + " = ?, " + COLUMN_IMAGE_URL + " = ? WHERE " + COLUMN_BARCODE + " = ?";
        db.execSQL(updateQuery, new Object[]{
                product.getName(),
                product.getAllergens(),
                product.getIngredients(),
                product.getDescription(),
                product.getStores(),
                product.getCountries(),
                product.getImageUrl(),
                product.getBarcode()
        });
        db.close();
    }

    public void deleteProduct(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM products WHERE " + COLUMN_BARCODE + " = ?";
        db.execSQL(deleteQuery, new Object[]{barcode});
        db.close();
    }

    public Product getProduct(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, COLUMN_BARCODE + " = ?", new String[]{barcode}, null, null, null);
        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String allergens = cursor.getString(cursor.getColumnIndex(COLUMN_ALLERGENS));
            String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            String stores = cursor.getString(cursor.getColumnIndex(COLUMN_STORES));
            String countries = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRIES));
            String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));

            product = new Product(barcode, name, allergens, ingredients, description, stores, countries, imageUrl);
            cursor.close();
        }
        return product;
    }
}
