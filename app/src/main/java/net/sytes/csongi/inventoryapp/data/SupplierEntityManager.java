package net.sytes.csongi.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

/**
 * Facade Singleton Class for SupplierEntities.
 * This class is responsible for the following:
 * 1) creating SupplierEntities from database records
 * 2) persisting SupplierEntities into database sent by clients
 * 3) modifying database records through SupplierEntities modified by clients
 * 4) deleting database records through SupplierEntities
 */
public class SupplierEntityManager extends InventoryEntityManager<SupplierEntity> {
    private static final String LOG_TAG = InventoryEntityManager.class.getSimpleName() + "--->";

    private static volatile SupplierEntityManager sInstance;

    private SupplierEntityManager() {
        super(InventoryContract.SupplierEntry.TABLE_NAME);
    }

    /**
     * Method for getting the only instance of this class (since it's a Singleton)
     * @return - the SupplierEntityManager instance
     */
    public static SupplierEntityManager getInstance() {
        if (sInstance == null) synchronized (SupplierEntityManager.class) {
            sInstance = new SupplierEntityManager();
        }
        return sInstance;
    }

    // Overriding abstract template methods from InventoryEntityManager
    @Override
    protected SupplierEntity getEntityFromCursor(Cursor cursor, Context context) {

        // parsing results from Cursor object
        long supplierId=cursor.getLong(0);
        String supplierName=cursor.getString(1);
        String supplierPhone=cursor.getString(2);

        // creating Supplier Entity and set it's values then return the result
        SupplierEntity supplierEntity=new SupplierEntity();
        supplierEntity.setId(supplierId);
        supplierEntity.setSupplierName(supplierName);
        supplierEntity.setSupplierPhone(supplierPhone);
        Log.d(LOG_TAG,"Supplier from cursor has been created");
        return supplierEntity;
    }

    @Override
    protected List<Long> checkFields(List<Long> resultList, SupplierEntity entity) {

        // checking whether all not-null fields has been filled.
        if(entity.getSupplierName()==null||entity.getSupplierName().length()<1) {
            resultList.add((long) ErrorCodes.SUPPLIER_NAME_EMPTY_ERROR);
            Log.d(LOG_TAG,"Supplier name empty error");
        }
        if(entity.getSupplierPhone()==null||entity.getSupplierName().length()<1) {
            resultList.add((long) ErrorCodes.SUPPLIER_PHONE_EMPTY_ERROR);
            Log.d(LOG_TAG,"Supplier phone empty error");
        }
        return resultList;
    }

    @Override
    protected ContentValues getContentValues(SupplierEntity entity) {

        // creating appropriate ContentValues
        ContentValues contentValuesToReturn=new ContentValues();
        contentValuesToReturn.put(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_NAME,entity.getSupplierName());
        contentValuesToReturn.put(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE,entity.getSupplierPhone());
        Log.d(LOG_TAG,"supplier content values has been created");
        return contentValuesToReturn;
    }
}
