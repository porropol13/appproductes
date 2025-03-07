package com.polporro.appproductes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 3;  // O una versión mayor si es necesario

    // Nombres de las tablas y columnas
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALLERGENS = "allergens";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_STORES = "stores";
    public static final String COLUMN_COUNTRIES = "countries";
    public static final String COLUMN_ID = "_id";  // Columna _id

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla con la columna _id y la columna description
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // _id como clave primaria autoincremental
                + COLUMN_BARCODE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ALLERGENS + " TEXT,"
                + COLUMN_INGREDIENTS + " TEXT,"
                + COLUMN_STORES + "TEXT,"
                + COLUMN_COUNTRIES + "TEXT,"
                + "description TEXT" + ")";  // Añadido la columna description
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar la base de datos si la versión se va a hacer un downgrade
        if (oldVersion > newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            onCreate(db);
        } else {
            // Actualización normal de la base de datos
            if (oldVersion < 3) {
                // Aquí van las migraciones necesarias entre versiones
            }

        }
    }

    // Método para agregar un producto
    public void addProduct(String barcode, String name, String allergens, String ingredients, String description, String code, String stores, String countries) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BARCODE, barcode);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ALLERGENS, allergens);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put("description", description);  // Guardar la descripción
        values.put(COLUMN_STORES, stores);
        values.put(COLUMN_COUNTRIES, countries);

        db.insert(TABLE_PRODUCTS, null, values);  // Insertar el producto
        db.close();
    }

    // Método para obtener todos los productos
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Seleccionar todas las columnas, incluida _id
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_BARCODE + ", " + COLUMN_NAME + ", "
                + COLUMN_ALLERGENS + ", " + COLUMN_INGREDIENTS + COLUMN_STORES + COLUMN_COUNTRIES + " FROM " + TABLE_PRODUCTS;
        return db.rawQuery(query, null);
    }

    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultar el producto por su código de barras
        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                new String[]{COLUMN_ID, COLUMN_BARCODE, COLUMN_NAME, COLUMN_ALLERGENS, COLUMN_INGREDIENTS, COLUMN_STORES, COLUMN_COUNTRIES, "description"},  // Añadir "description"
                COLUMN_BARCODE + "=?",
                new String[]{barcode},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            @SuppressLint("Range") String allergens = cursor.getString(cursor.getColumnIndex(COLUMN_ALLERGENS));
            @SuppressLint("Range") String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
            @SuppressLint("Range") String stores = cursor.getString(cursor.getColumnIndex(COLUMN_STORES));
            @SuppressLint("Range") String countries = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRIES));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));  // Obtener la descripción
            String imageUrl = "default_image_url";

            Product product = new Product(barcode, name, allergens, ingredients, description, stores, countries);
            cursor.close();
            return product;
        } else {
            cursor.close();
            return null;
        }
    }
}
