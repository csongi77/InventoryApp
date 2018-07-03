package net.sytes.csongi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

/**
 * DB helper for App. The class is package private since only the ProductDAO class can
 * initiate CRUD operations. All clients in other packages can communicate with database
 * through ProductDAO using ProductEntities.
 */
class InventoryDbHelper extends SQLiteOpenHelper {

    // declaring constants
    private static final int DATABASE_VERSION=2;
    private static final String DATABASE_NAME="InventoryApp.db";
    private static final String LOG_TAG=InventoryDbHelper.class.getSimpleName()+" --->";

    // creating Product SQL TABLE constant
    private static final String CREATE_INVENTORY_TABLE="CREATE TABLE "+ ProductEntry.TABLE_NAME+
            "("+ProductEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            ProductEntry.COLUMN_NAME_PRODUCT_NAME +" TEXT NOT NULL, "+
            ProductEntry.COLUMN_NAME_PRICE +" INTEGER NOT NULL DEFAULT 1, "+
            ProductEntry.COLUMN_NAME_QUANTITY +" INTEGER NOT NULL DEFAULT 0, "+
            ProductEntry.COLUMN_NAME_SUPPLIER_ID+" INTEGER NOT NULL DEFAULT 0)";

    // creating Supplier SQL TABLE
    private static final String CREATE_SUPPLIER_TABLE="CREATE TABLE "+ SupplierEntry.TABLE_NAME+
            "("+SupplierEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            SupplierEntry.COLUMN_NAME_SUPPLIER_NAME +" TEXT NOT NULL, "+
            SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE +" TEXT NOT NULL)";

    // dropping old Product TABLE
    private static final String DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS "+ProductEntry.TABLE_NAME;

    // dropping old Supplier Table
    private static final String DROP_SUPPLIER_TABLE = "DROP TABLE IF EXISTS "+SupplierEntry.TABLE_NAME;

    InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // overriding default onCreate method
    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_INVENTORY_TABLE);
       Log.d(LOG_TAG,"Database "+ProductEntry.TABLE_NAME+", V:"+String.valueOf(db.getVersion())+" has been created");
       db.execSQL(CREATE_SUPPLIER_TABLE);
       Log.d(LOG_TAG,"Database "+SupplierEntry.TABLE_NAME+", V:"+String.valueOf(db.getVersion())+" has been created");
    }

    // overriding default onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_PRODUCT_TABLE);
        Log.d(LOG_TAG,"Database "+ProductEntry.TABLE_NAME+", V:"+String.valueOf(db.getVersion())+"has been dropped");
        db.execSQL(DROP_SUPPLIER_TABLE);
        Log.d(LOG_TAG,"Database "+SupplierEntry.TABLE_NAME+", V:"+String.valueOf(db.getVersion())+"has been dropped");
        onCreate(db);
    }

}
