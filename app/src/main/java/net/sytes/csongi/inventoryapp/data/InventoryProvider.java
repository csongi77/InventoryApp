package net.sytes.csongi.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import net.sytes.csongi.inventoryapp.R;

import java.io.FileNotFoundException;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class InventoryProvider extends ContentProvider {

    private static final String TAG = InventoryProvider.class.getSimpleName();

    private static final int PRODUCT_ID = 100;
    private static final int PRODUCTS = 101;
    private static final int SUPPLIER_ID = 200;
    private static final int SUPPLIERS = 201;
    private static UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS + "/#", PRODUCT_ID);
        sMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS, PRODUCTS);
        sMatcher.addURI(CONTENT_AUTHORITY, PATH_SUPPLIERS + "/#", SUPPLIER_ID);
        sMatcher.addURI(CONTENT_AUTHORITY, PATH_SUPPLIERS, SUPPLIERS);
    }

    private InventoryDbHelper mInventoryDbHelper;

    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // checking query
        int queryType = sMatcher.match(uri);

        // declare Cursor
        Cursor cursor = null;

        // obtaining readable db
        SQLiteDatabase db = mInventoryDbHelper.getReadableDatabase();

        switch (queryType) {
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIERS:
                cursor = db.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query this URI: " + uri);
        }

        // set listener for this query/cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sMatcher.match(uri);
        Long id = null;
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            case SUPPLIERS:
                return insertSupplier(uri, values);
            default:
                throw new IllegalArgumentException("Adding new entity failure from uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sMatcher.match(uri);
        int affectedRows = 0;
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        switch (match) {
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                affectedRows = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (affectedRows > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return affectedRows;
            case PRODUCTS:
                affectedRows = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (affectedRows > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return affectedRows;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                affectedRows = deleteSupplier(selection, selectionArgs, db);
                if (affectedRows > 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.inventory_provider_supplier_deleted, ContentUris.parseId(uri)), Toast.LENGTH_LONG).show();
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return affectedRows;
            case SUPPLIERS:
                affectedRows = deleteSupplier(selection, selectionArgs, db);
                if (affectedRows > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return affectedRows;
            default:
                throw new IllegalArgumentException("Deleting entity failure from uri: " + uri);
        }
    }

    /**
     * helper method for deleting Supplier from database. This method is also responsible for
     * setting the related Products' supplierId to -2L (which is the default value for 'Supplier removed' state)
     * For further info see InventoryContract.
     *
     * @param selection     - either id of Supplier or other column
     * @param selectionArgs - arguments for column
     * @param db            - reference of database
     * @return - the number of affected rows
     */
    private int deleteSupplier(String selection, String[] selectionArgs, SQLiteDatabase db) {
        int affectedRows = 0;
        // before deleting we have to check how many Suppliers will be affected
        String[] columns = new String[]{SupplierEntry._ID};
        Cursor suppliersCursor = db.query(SupplierEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        /*
            Now we iterate through the result and do the following:
            0) move supplierCursor to first result
            1) get the SupplierEntry._ID from supplierCursor
            2) find products with this ProductEntry.supplier_id
            3) delete product
            4) repeat steps 1-3 until supplierCursor has next entry.
            5) close cursor
         */

        // we have at least 1 result! :)
        while (suppliersCursor.moveToNext()) {

            long supplierId = suppliersCursor.getLong(suppliersCursor.getColumnIndexOrThrow(SupplierEntry._ID));

            String productSelection = ProductEntry.COLUMN_NAME_SUPPLIER_ID + "=?";
            String[] productSelectionArgs = new String[]{String.valueOf(supplierId)};;
            int affectedProducts=db.delete(ProductEntry.TABLE_NAME,productSelection,productSelectionArgs);
            Log.i(TAG, "supplierCursor moved to ID:: deleted " + affectedProducts+" pcs of products");
        }
        affectedRows = db.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
        return affectedRows;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sMatcher.match(uri);
        switch (match) {
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case SUPPLIER_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSupplier(uri, values, selection, selectionArgs);
            case SUPPLIERS:
                return updateSupplier(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Failed updating uri: " + uri);
        }
    }

    /**
     * Overriding MIME type
     *
     * @param uri
     * @return the MIME type, one of the following:
     * 1) "vnd.android.cursor.item/net.sytes.csongi.inventoryapp/products"
     * 2) "vnd.android.cursor.dir/net.sytes.csongi.inventoryapp/products"
     * 3) "vnd.android.cursor.item/net.sytes.csongi.inventoryapp/suppliers"
     * 4) "vnd.android.cursor.dir/net.sytes.csongi.inventoryapp/suppliers"
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sMatcher.match(uri);
        switch (match) {
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            case SUPPLIERS:
                return SupplierEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    /**
     * helper method for modifying Suppliers
     *
     * @param uri           - uri of product
     * @param values        - Content Values to modify
     * @param selection     - columns we want to modify
     * @param selectionArgs - which rows we want to modify
     * @return number of affected rows
     */
    private int updateSupplier(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /* Sanity check:
         * 1: is Supplier name valid?
         * 2: is Supplier phone valid?
         * 3: are there any columns to change? */
        if (values.containsKey(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME)) {
            String supplierNewName = values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME).trim();
            if (TextUtils.isEmpty(supplierNewName))
                throw new IllegalArgumentException("Supplier Name cannot be empty");
        }

        if (values.containsKey(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)) {
            String supplierNewPhoneNumber = values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE).trim();
            if (TextUtils.isEmpty(supplierNewPhoneNumber))
                throw new IllegalArgumentException("Supplier Phone number cannot be empty");
        }

        if (values.size() == 0) return 0;

        /* Sanity check OK, no exception has thrown and values has at least one parameter
         *  So now we get writable database and persist the changes. Then - if at least a single
         *  row has changed - notify observers. */
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        int affectedRows = db.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    /**
     * helper method for modifying Products
     *
     * @param uri           - uri of product
     * @param values        - Content Values to modify
     * @param selection     - columns we want to modify
     * @param selectionArgs - which rows we want to modify
     * @return number of affected rows
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsAffected = 0;

        // sanity check
        if (values.containsKey(ProductEntry.COLUMN_NAME_PRODUCT_NAME)) {
            String productName = values.getAsString(ProductEntry.COLUMN_NAME_PRODUCT_NAME);
            if (TextUtils.isEmpty(productName))
                throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (values.containsKey(ProductEntry.COLUMN_NAME_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(ProductEntry.COLUMN_NAME_QUANTITY);
            if (productQuantity == null) {
                throw new IllegalArgumentException("Product quantity must be an integer");
            }
            if (productQuantity < 0)
                throw new IllegalArgumentException("Product quantity must be positive number or 0");
        }

        if (values.containsKey(ProductEntry.COLUMN_NAME_PRICE)) {
            Integer productPrice = values.getAsInteger(ProductEntry.COLUMN_NAME_PRICE);
            if (productPrice == null) {
                throw new IllegalArgumentException("Product price must be an integer");
            }
            if (productPrice < 1)
                throw new IllegalArgumentException("Product price must be positive number");
        }
        // check supplier id

        if (values.containsKey(ProductEntry.COLUMN_NAME_SUPPLIER_ID)) {
            Long supplierId = values.getAsLong(ProductEntry.COLUMN_NAME_SUPPLIER_ID);
            if (supplierId == null) {
                throw new IllegalArgumentException("Supplier must be set");
            }
            if (supplierId < 1) {
                throw new IllegalArgumentException("Select valid supplier!");
            }
            if (!isSupplierIdValid(supplierId))
                throw new IllegalArgumentException("Supplier id is invalid!");
        }

        if (values.size() == 0) return 0;

        // update product
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        rowsAffected = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsAffected>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsAffected;
    }

    /**
     * Insert new Supplier
     *
     * @param uri    - the target uri
     * @param values - the Content Values to insert
     * @return the Uri of new Supplier
     */
    private Uri insertSupplier(Uri uri, ContentValues values) {
        /*
         * Sanity check
         */
        String supplierName = values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME);
        if (supplierName == null || supplierName.isEmpty())
            throw new IllegalArgumentException("Supplier Name must not be empty!");

        String supplierPhone = values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE);
        if (supplierPhone == null || supplierPhone.isEmpty())
            throw new IllegalArgumentException("Supplier phone number must not be empty!");

        // getting writable database reference
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        // add new entry
        long id = db.insert(SupplierEntry.TABLE_NAME, null, values);
        if (id == INSERT_ERROR)
            return null;
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert new Product
     *
     * @param uri    - the target uri
     * @param values - the Content Values
     * @return Uri of new product OR null
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        /*
         * Sanity check. We don't have to check quantity and price since they have default
         * value (=0)
         */
        String productName = values.getAsString(ProductEntry.COLUMN_NAME_PRODUCT_NAME);
        if (productName == null || productName.isEmpty())
            throw new IllegalArgumentException("Product Name must be set");

        Long supplierId = values.getAsLong(ProductEntry.COLUMN_NAME_SUPPLIER_ID);
        if (supplierId == null || supplierId < 0 || !isSupplierIdValid(supplierId))
            throw new IllegalArgumentException("Supplier must be set");

        // getting writable database reference
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        // add new entry
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == INSERT_ERROR) return null;
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Check whether Supplier exists with given Id
     *
     * @param supplierId the Id to check
     * @return true if Supplier exists with given supplerId. False otherwise
     */
    private boolean isSupplierIdValid(Long supplierId) {
        Log.d(TAG, "isSupplierIdValid: has been called");
        SQLiteDatabase db = mInventoryDbHelper.getReadableDatabase();

        if (supplierId == ProductEntry.SUPPLIER_HAS_BEEN_REMOVED) {
            Log.d(TAG, "isSupplierIdValid: supplier has been removed");
            return false;
        }
        // creating query on Supplier Id
        String selection = SupplierEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(supplierId)};

        // running the query
        Cursor queryResult = db.query(SupplierEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        // if Supplier with given Id exists then the queryResult has a result which returns true.
        if (queryResult.moveToFirst()) {
            queryResult.close();
            Log.d(TAG, "isSupplierIdValid: supplier with id: " + supplierId + " exists");
            return true;
        } else {
            queryResult.close();
            Log.d(TAG, "isSupplierIdValid: supplier with id: " + supplierId + " doesn't exists");
            return false;
        }
    }
}
