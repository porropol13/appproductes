package com.polporro.appproductes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 3;  // Incrementa la versión de la base de datos a 3

    // Nombres de las tablas y columnas
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALLERGENS = "allergens";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_IMAGE_URL = "image_url";  // Nueva columna para la URL de la imagen
    public static final String COLUMN_ID = "_id";  // Columna _id

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla con la columna _id y la nueva columna image_url
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // _id como clave primaria autoincremental
                + COLUMN_BARCODE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ALLERGENS + " TEXT,"
                + COLUMN_INGREDIENTS + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT" + ")";  // Nueva columna image_url
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Verificar si la columna image_url ya existe, si no, agregarla
            String ADD_IMAGE_URL_COLUMN = "ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COLUMN_IMAGE_URL + " TEXT";
            db.execSQL(ADD_IMAGE_URL_COLUMN);
        }
    }

    // Método para agregar un producto
    public void addProduct(String barcode, String name, String allergens, String ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BARCODE, barcode);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ALLERGENS, allergens);
        values.put(COLUMN_INGREDIENTS, ingredients);

        // No agregues la columna de descripción o imagen si no la necesitas
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Método para obtener todos los productos
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Seleccionar todas las columnas, incluida image_url
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_BARCODE + ", " + COLUMN_NAME + ", "
                + COLUMN_ALLERGENS + ", " + COLUMN_INGREDIENTS + ", " + COLUMN_IMAGE_URL + " FROM " + TABLE_PRODUCTS;
        return db.rawQuery(query, null);
    }

    // Método para obtener un producto por su código de barras
    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultar el producto por su código de barras
        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                new String[]{COLUMN_ID, COLUMN_BARCODE, COLUMN_NAME, COLUMN_ALLERGENS, COLUMN_INGREDIENTS, COLUMN_IMAGE_URL},
                COLUMN_BARCODE + "=?",
                new String[]{barcode},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String allergens = cursor.getString(cursor.getColumnIndex(COLUMN_ALLERGENS));
            String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
            String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));  // Obtener la URL de la imagen
            String description = "No description available";  // Puedes cambiar esto si tienes una columna para descripción

            Product product = new Product(barcode, name, allergens, ingredients, description, imageUrl);
            cursor.close();
            return product;
        } else {
            cursor.close();
            return null;
        }
    }
}
