package net.sytes.csongi.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class InventoryProvider extends ContentProvider {

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
        return cursor;
    }

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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sMatcher.match(uri);
        Long id = null;
        switch (match) {
            case PRODUCTS:
                id = insertProduct(uri, values);
                if (id == -1) return null;
                else {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri, String.valueOf(id));
                }
            case SUPPLIERS:
                id = insertSupplier(uri, values);
                if (id == -1) return null;
                else {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri, String.valueOf(id));
                }
            default:
                throw new IllegalArgumentException("Adding new entity failure from uri: " + uri);
        }
    }

    /**
     * Insert new Supplier
     *
     * @param uri    - the target uri
     * @param values - the Content Values to insert
     * @return the id of Supplier
     */
    private Long insertSupplier(Uri uri, ContentValues values) {
        /*
         * Sanity check
         */
        String supplierName=values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME);
        if(supplierName==null||supplierName.isEmpty()) return null;

        String supplierPhone=values.getAsString(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE);
        if(supplierPhone==null||supplierPhone.isEmpty()) return null;

        // getting writeable database reference
        SQLiteDatabase db=mInventoryDbHelper.getWritableDatabase();

        // add new entry
        long id=db.insert(SupplierEntry.TABLE_NAME,null,values);
        return id;
    }

    /**
     * Insert new Product
     *
     * @param uri    - the target uri
     * @param values - the Content Values
     * @return id of new product OR null
     */
    private long insertProduct(Uri uri, ContentValues values) {
        /*
         * Sanity check. We don't have to check quantity and price since they have default
         * value (=0)
         */
        String productName = values.getAsString(ProductEntry.COLUMN_NAME_PRODUCT_NAME);
        if (productName == null || productName.isEmpty())
            throw new IllegalArgumentException("Product Name must be set");

        Long supplierId = values.getAsLong(ProductEntry.COLUMN_NAME_SUPPLIER_ID);
        if (supplierId == null || supplierId < 0)
            throw new IllegalArgumentException("Supplier must be set");

        // getting writeable database reference
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        // add new entry
        long result = db.insert(ProductEntry.TABLE_NAME, null, values);
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
