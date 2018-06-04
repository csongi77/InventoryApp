package net.sytes.csongi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static net.sytes.csongi.inventoryapp.data.ProductContract.ProductEntry;

/**
 * DB helper for App. The class is package private since only the ProductDAO class can
 * initiate CRUD operations. All clients in other packages can communicate with database
 * through ProductDAO using ProductEntities.
 */
class ProductDbHelper extends SQLiteOpenHelper {

    // declaring constants
    static final int DATABASE_VERSION=1;
    static final String DATABASE_NAME="InventoryApp.db";
    private static final String LOG_TAG=ProductDbHelper.class.getSimpleName()+" --->";

    // creating SQL TABLE constant
    private static final String CREATE_INVENTORY_TABLE="CREATE TABLE "+ ProductEntry.TABLE_NAME+
            "("+ProductEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            ProductEntry.COLUMN_NAME_PRODUCT_NAME +" TEXT NOT NULL, "+
            ProductEntry.COLUMN_NAME_PRICE +" INTEGER NOT NULL DEFAULT 0, "+
            ProductEntry.COLUMN_NAME_QUANTITY +" INTEGER NOT NULL DEFAULT 0, "+
            ProductEntry.COLUMN_NAME_SUPPLIER_NAME +" TEXT NOT NULL, "+
            ProductEntry.COLUMN_NAME_SUPPLIER_PHONE +" TEXT NOT NULL)";

    ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_INVENTORY_TABLE);
        Log.d(LOG_TAG,"Database "+DATABASE_NAME+", V:"+String.valueOf(db.getVersion())+" has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing since we don't want to upgrade the database
    }

}
