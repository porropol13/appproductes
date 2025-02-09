package com.polporro.appproductes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    // Definir los nombres de las tablas y columnas
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALLERGENS = "allergens";
    public static final String COLUMN_INGREDIENTS = "ingredients";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de productos
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_BARCODE + " TEXT PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ALLERGENS + " TEXT,"
                + COLUMN_INGREDIENTS + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la base de datos se actualiza, eliminamos la tabla existente y creamos una nueva
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Método para agregar un producto a la base de datos
    public void addProduct(String barcode, String name, String allergens, String ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BARCODE, barcode);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ALLERGENS, allergens);
        values.put(COLUMN_INGREDIENTS, ingredients);

        // Insertar el producto
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Método para obtener todos los productos de la base de datos
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Seleccionar todos los productos
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        return db.rawQuery(query, null);
    }
}
