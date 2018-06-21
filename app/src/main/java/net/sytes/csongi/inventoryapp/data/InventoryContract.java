package net.sytes.csongi.inventoryapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for products
 */
final class InventoryContract {

    // defining constants: content authority, base content uri and path for Products and Suppliers
    private static final String CONTENT_AUTHORITY="net.sytes.csongi.inventoryapp";
    private static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    private static final String PATH_PRODUCTS="products";
    private static final String PATH_SUPPLIERS="suppliers";

    // empty constructor for avoiding instantiating this class
    private InventoryContract() {
    }

    // the product table contract
    static class ProductEntry implements BaseColumns {

        // defining table name
        public static final String TABLE_NAME="products";



        // defining columns
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_NAME_PRODUCT_NAME ="product_name";
        public static final String COLUMN_NAME_PRICE ="price";
        public static final String COLUMN_NAME_QUANTITY ="quantity";
        public static final String COLUMN_NAME_SUPPLIER_ID="supplier_id";

    }

    // the supplier table contract
    static class SupplierEntry implements BaseColumns {

        // defining columns
        public static final String TABLE_NAME="suppliers";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_NAME_SUPPLIER_NAME ="supplier_name";
        public static final String COLUMN_NAME_SUPPLIER_PHONE ="supplier_phone";

    }
}
