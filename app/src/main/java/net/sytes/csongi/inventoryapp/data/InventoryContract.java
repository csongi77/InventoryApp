package net.sytes.csongi.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for products
 */
public final class InventoryContract {

    // defining constants: content authority, base content uri and path for Products and Suppliers
    public static final String CONTENT_AUTHORITY="net.sytes.csongi.inventoryapp";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS="products";
    public static final String PATH_SUPPLIERS="suppliers";

    /**
     * constant value for every insertation error.
     */
    public static final long INSERT_ERROR=-1L;

    // empty constructor for avoiding instantiating this class
    private InventoryContract() {
    }

    // the product table contract
    public static class ProductEntry implements BaseColumns {

        /** defining table name */
        public static final String TABLE_NAME="products";

        /** content uri for products */
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        /** Defining MIME type for directory */
        public static final String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PRODUCTS;

        /** Defining MIME type for single item */
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PRODUCTS;

        /**
         * Id of product
         *  type: long, not null, autoincrement
         */
        public static final String _ID=BaseColumns._ID;

        /**
         * Name of product
         * type: String, not null
         */
        public static final String COLUMN_NAME_PRODUCT_NAME ="product_name";

        /**
         * Price of product
         * type: int, not null, default 0
         */
        public static final String COLUMN_NAME_PRICE ="price";

        /**
         * Quantity of product
         * type: int, not null, default 0
         */
        public static final String COLUMN_NAME_QUANTITY ="quantity";

        /**
         * Supplier Id (key for supplier _ID)
         * type: long, not null, default 0
         * if supplier is removed, this value must set to -1L
         */
        public static final String COLUMN_NAME_SUPPLIER_ID="supplier_id";

        /**
         * Error code in order to let the client know that supplier of this product has been deleted
         * type: long
         */
        public static final long SUPPLIER_HAS_BEEN_REMOVED=-2L;
        private static final long SUPPLIER_NOT_SELECTED = -1L;
    }

    // the supplier table contract
    public static class SupplierEntry implements BaseColumns {

        /** Table name */
        public static final String TABLE_NAME="suppliers";

        /** content uri for products */
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SUPPLIERS);

        /** Defining MIME type for directory */
        public static final String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SUPPLIERS;

        /** Defining MIME type for single item */
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SUPPLIERS;

        /**
         * Id of Supplier
         * type: long, not null, autoincrement
         */
        public static final String _ID=BaseColumns._ID;

        /**
         * Supplier name
         * type: String, not null
         */
        public static final String COLUMN_NAME_SUPPLIER_NAME ="supplier_name";

        /**
         * Supplier phone
         * type: String, not null
         */
        public static final String COLUMN_NAME_SUPPLIER_PHONE ="supplier_phone";

    }
}
