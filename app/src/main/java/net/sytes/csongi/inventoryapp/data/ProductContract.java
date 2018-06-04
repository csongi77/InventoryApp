package net.sytes.csongi.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Contract for products
 */
public final class ProductContract {

    // empty constructor for avoiding instantiating this class
    private ProductContract() {
    }

    // the product table contract
    public static class ProductEntry implements BaseColumns {

        // defining columns
        private static final String TABLE_NAME="products";
        private static final String COLUMN_ID="_id";
        private static final String COLUMN_PRODUCT_NAME="product_name";
        private static final String COLUMN_PRICE="price";
        private static final String COLUMN_QUANTITY="quantity";
        private static final String COLUMN_SUPPLIER_NAME="supplier_name";
        private static final String COLUMN_SUPPLIER_PHONE="supplier_phone";
    }
}
