package net.sytes.csongi.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for the following:
 * 1) creating ProductEntities from database records
 * 2) persisting ProductEntities into database sent by clients
 * 3) modifying database records through ProductEntities modified by clients
 * 4) deleting database records through ProductEntities
 */
public class ProductEntityManager {

    private static final String LOG_TAG=ProductEntityManager.class.getSimpleName()+"--->";
    private Context mContext;

    public ProductEntityManager(Context mContext) {
        this.mContext = mContext;
    }

    public ProductEntity findProductById(long id) {
        return null;
    }

    public List<ProductEntity> findAllProducts() {
        return null;
    }

    /**
     * Persist ProductEntity into database.
     * @param productEntity the Product which has to be persisted
     * @return - a List<Long> contains codes:
     *          1) _id(long) of product row (only 1 element List) OR
     *          2) -1L in case of db error (only 1 element List) OR
     *          3) more than one element List of error codes. For error codes
     *              @see @{@link ErrorCodes}
     */
    public List<Long> insertProduct(ProductEntity productEntity) {
        // Creating empty result list
        List<Long> resultList = new ArrayList<>();

        /* Checking those not null fields that has no default values. If some of those
        fields are empty, an appropriate error code will be added to resultList */
        if (productEntity.getProductName() == null || productEntity.getProductName().length() == 0)
            resultList.add((long) ErrorCodes.PRODUCT_NAME_EMPTY_ERROR);
        if (productEntity.getSupplierEntity() == null)
            resultList.add((long) ErrorCodes.NO_SUPPLIER_SELECTED);

        /* If there are no errors, we try to persist the ProductEntity into database
         * First we create ContentValues then
         * we get writable database from dbHelper and
         * insert Product into table
         */
        if(resultList.isEmpty()){
            ContentValues contentValues=new ContentValues();
            contentValues.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME,productEntity.getProductName());
            contentValues.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE,productEntity.getPrice());
            contentValues.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,productEntity.getQuantity());
            contentValues.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_ID,productEntity.getSupplierEntity().getmId());

            ProductDbHelper helper=new ProductDbHelper(mContext);
            SQLiteDatabase database=helper.getWritableDatabase();
            Long result=database.insert(InventoryContract.ProductEntry.TABLE_NAME,null,contentValues);
            resultList.add(result);
            Log.d(LOG_TAG,"Product has been created. Id: "+result.toString());
        }
        return resultList;
    }

    public boolean updateProduct(ProductEntity productEntity) {
        return false;
    }

    public boolean deleteProduct(ProductEntity productEntity) {
        return false;
    }
}
