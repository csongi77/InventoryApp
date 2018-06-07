package net.sytes.csongi.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Contract for products
 */
final class InventoryContract {

    // empty constructor for avoiding instantiating this class
    private InventoryContract() {
    }

    // the product table contract
    static class ProductEntry implements BaseColumns {

        // defining columns
        public static final String TABLE_NAME="products";
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