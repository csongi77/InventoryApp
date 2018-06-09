package net.sytes.csongi.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

/**
 * This class is responsible for the following:
 * 1) creating ProductEntities from database records
 * 2) persisting ProductEntities into database sent by clients
 * 3) modifying database records through ProductEntities modified by clients
 * 4) deleting database records through ProductEntities
 */
public class ProductEntityManager extends InventoryEntityManager<ProductEntity> {

    private static final String LOG_TAG = ProductEntityManager.class.getSimpleName() + "--->";

    private static volatile ProductEntityManager sInstance;

    private ProductEntityManager() {
        super(InventoryContract.ProductEntry.TABLE_NAME);
    }

    public static ProductEntityManager getInstance() {
        if (sInstance == null) synchronized (ProductEntityManager.class) {
            sInstance = new ProductEntityManager();
        }
        return sInstance;
    }

    // Overriding abstract template methods from InventoryEntityManager
    @Override
    Entity getEntityFromCursor(Cursor cursor, Context context) {

        // parsing results from Cursor object
        long itemId=cursor.getLong(0);
        String productName=cursor.getString(1);
        int price=cursor.getInt(2);
        int quantity=cursor.getInt(3);
        long supplierId=cursor.getLong(4);

        // finding Supplier Entity belongs to this Product
        SupplierEntity supplierEntity= SupplierEntityManager.getInstance().findEntityById(supplierId, context);

        // creating Product Entity from result values and return it
        ProductEntity productEntity=new ProductEntity();
        productEntity.setId(itemId);
        productEntity.setProductName(productName);
        productEntity.setPrice(price);
        productEntity.setQuantity(quantity);
        productEntity.setSupplierEntity(supplierEntity);
        Log.d(LOG_TAG,"Product from cursor has been created");
        return productEntity;
    }

    @Override
    protected List<Long> checkFields(List<Long> resultList, Entity entity) {

        // checking whether all not-null fields has been filled.
        ProductEntity productEntity = (ProductEntity)entity;
        if (productEntity.getProductName() == null || productEntity.getProductName().length() < 1) {
            resultList.add((long) ErrorCodes.PRODUCT_NAME_EMPTY_ERROR);
            Log.d(LOG_TAG,"Product name empty error");
        }
        if (productEntity.getSupplierEntity() == null) {
            resultList.add((long) ErrorCodes.NO_SUPPLIER_SELECTED);
            Log.d(LOG_TAG,"No supplier selected error");
        }
        return resultList;
    }

    @Override
    protected ContentValues getContentValues(Entity entity) {
        ContentValues contentValuesToReturn = new ContentValues();
        ProductEntity productEntity=(ProductEntity)entity;
        contentValuesToReturn.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME, productEntity.getProductName());
        contentValuesToReturn.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE, productEntity.getPrice());
        contentValuesToReturn.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, productEntity.getQuantity());
        contentValuesToReturn.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_ID, productEntity.getSupplierEntity().getId());
        Log.d(LOG_TAG,"Product content values has been created");
        return contentValuesToReturn;
    }
}
