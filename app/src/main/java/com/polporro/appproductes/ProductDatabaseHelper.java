package com.polporro.appproductes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "productDB";
    private static final int DATABASE_VERSION = 1;

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla de productos si no existe
        db.execSQL("CREATE TABLE products (barcode TEXT PRIMARY KEY, name TEXT, allergens TEXT, ingredients TEXT, description TEXT, imageUrl TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    // Método para obtener un producto por su código de barras
    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, "barcode=?", new String[]{barcode}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Product product = new Product(
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("allergens")),
                    cursor.getString(cursor.getColumnIndex("ingredients")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("imageUrl"))
            );
            cursor.close();
            return product;
        } else {
            return null;
        }
    }
}
