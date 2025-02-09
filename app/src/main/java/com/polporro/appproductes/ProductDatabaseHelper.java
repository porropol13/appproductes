package com.polporro.appproductes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 2;  // Incrementa la versión de la base de datos a 2

    // Nombres de las tablas y columnas
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALLERGENS = "allergens";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "_id";  // Columna _id

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla con la columna _id
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // _id como clave primaria autoincremental
                + COLUMN_BARCODE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ALLERGENS + " TEXT,"
                + COLUMN_INGREDIENTS + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la versión de la base de datos cambia, eliminar la tabla antigua y crear la nueva
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);  // Eliminar la tabla antigua
            onCreate(db);  // Crear la nueva tabla con la columna _id
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

        db.insert(TABLE_PRODUCTS, null, values);  // Insertar el producto
        db.close();
    }

    // Método para obtener todos los productos
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Seleccionar todas las columnas, incluida _id
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_BARCODE + ", " + COLUMN_NAME + ", "
                + COLUMN_ALLERGENS + ", " + COLUMN_INGREDIENTS + " FROM " + TABLE_PRODUCTS;
        return db.rawQuery(query, null);
    }

    // Método para obtener un producto por su código de barras
    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultar el producto por su código de barras
        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                new String[]{COLUMN_ID, COLUMN_BARCODE, COLUMN_NAME, COLUMN_ALLERGENS, COLUMN_INGREDIENTS},
                COLUMN_BARCODE + "=?",
                new String[]{barcode},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String allergens = cursor.getString(cursor.getColumnIndex(COLUMN_ALLERGENS));
            String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
            String description = "No description available";
            String imageUrl = "default_image_url";

            Product product = new Product(barcode, name, allergens, ingredients, description, imageUrl);
            cursor.close();
            return product;
        } else {
            cursor.close();
            return null;
        }
    }
}
