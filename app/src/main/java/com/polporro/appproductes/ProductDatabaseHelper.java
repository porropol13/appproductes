package com.polporro.appproductes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "products.db";
    private static final int    DATABASE_VERSION = 5;

    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID         = "_id";
    public static final String COL_BARCODE    = "barcode";
    public static final String COL_NAME       = "name";
    public static final String COL_BRAND      = "brand";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BARCODE + " TEXT UNIQUE, " +
                COL_NAME    + " TEXT, " +
                COL_BRAND   + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Guardar o reemplazar un producto
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BARCODE, product.getBarcode());
        cv.put(COL_NAME,    product.getName());
        cv.put(COL_BRAND,   product.getBrand());
        db.insertWithOnConflict(TABLE_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Actualizar un producto (solo name y brand)
    public void updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,  product.getName());
        cv.put(COL_BRAND, product.getBrand());
        db.update(TABLE_PRODUCTS, cv, COL_BARCODE + "=?", new String[]{ product.getBarcode() });
        db.close();
    }

    // Obtener un producto concreto por barcode
    public Product getProduct(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                new String[]{ COL_BARCODE, COL_NAME, COL_BRAND },
                COL_BARCODE + "=?",
                new String[]{ barcode },
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String name  = cursor.getString(cursor.getColumnIndex(COL_NAME));
            @SuppressLint("Range") String brand = cursor.getString(cursor.getColumnIndex(COL_BRAND));
            cursor.close();
            db.close();
            return new Product(
                    barcode,
                    name,
                    brand,
                    "",   // quantity
                    "",   // allergens
                    "",   // ingredients
                    "",   // description
                    "",   // stores
                    "",   // countries
                    ""    // imageUrl
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Obtener la lista completa de productos (historial)
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                new String[]{ COL_BARCODE, COL_NAME, COL_BRAND },
                null, null, null, null,
                COL_ID + " DESC"    // ordenamos por ID descendente (último escaneado primero)
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String barcode = cursor.getString(cursor.getColumnIndex(COL_BARCODE));
                String name    = cursor.getString(cursor.getColumnIndex(COL_NAME));
                @SuppressLint("Range") String brand   = cursor.getString(cursor.getColumnIndex(COL_BRAND));
                products.add(new Product(
                        barcode,
                        name,
                        brand,
                        "",   // quantity
                        "",   // allergens
                        "",   // ingredients
                        "",   // description
                        "",   // stores
                        "",   // countries
                        ""    // imageUrl
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return products;
    }
}
