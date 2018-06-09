package net.sytes.csongi.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Facade class for EntityManagers using template methods (As it's described in
 * the Gang of Four book template method pattern). We encapsulate all methods that vary.
 * This class implements methods for:
 * 1) finding a specified Entity (by Id),
 * 2) finding all Entities in a table,
 * 3) updating an Entity,
 * 4) removing an Entity.
 */
public abstract class InventoryEntityManager <T extends Entity> {

    private static final String LOG_TAG = InventoryEntityManager.class.getSimpleName() + "---->";

    private String mTableName;

    /**
     * default Ctor
     *
     * @param tableName - the Name of the table which is given when a concrete EntityManager
     *                  class is being instantiated.
     */
    protected InventoryEntityManager(String tableName) {
        mTableName = tableName;
    }

    /**
     * Find Entity by specified Id
     *
     * @param id      - an Id of Entity we searching for
     * @param context - Da Kontakst :)
     * @return The instantiated Entity, NullEntity with error code (which Id<=0)
     * if there was no single result.
     */
    public T findEntityById(long id, Context context) {
        InventoryDbHelper helper = new InventoryDbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(mTableName, null,
                BaseColumns._ID + " =?", new String[]{String.valueOf(id)},
                null, null, null);
        T entityToReturn;
        if (cursor.moveToNext()) {
            entityToReturn = (T) getEntityFromCursor(cursor, context);
            Log.d(LOG_TAG,"findEntityById has result!");
        } else {
            entityToReturn = (T) new NullEntity((long) ErrorCodes.NO_RESULTS);
            Log.d(LOG_TAG,"findEntityById has no result!");
        }
        // We close the Cursor in order to avoid memory leak
        cursor.close();
        return entityToReturn;
    }

    /**
     * Method for listing all Entities
     *
     * @param context - the Context called from
     * @return - Entity result list. If there were no results, a List with a single
     * NullEntity will be retunrned (containing the error code)
     */
    public List<T> findAllEntity(Context context) {
        InventoryDbHelper helper = new InventoryDbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(mTableName, null,
                null, null,
                null, null, null);
        Entity entity;
        List<T> entitiesResult = new ArrayList<>();
        while (cursor.moveToNext()) {
            entity = getEntityFromCursor(cursor, context);
            entitiesResult.add((T)entity);
            Log.d(LOG_TAG,"findAllEntity result with id:="+String.valueOf(entity.getId())+"added to result list");
        }

        // We close the Cursor in order to avoid memory leak
        cursor.close();
        if (entitiesResult.isEmpty()) {
            entity = new NullEntity((long)ErrorCodes.NO_RESULTS);
            entitiesResult.add((T) entity);
            Log.d(LOG_TAG,"findAllEntity has no result!");
        }
        return entitiesResult;
    }

    /**
     * Abstract method for generating Entity from Cursor
     *
     * @param cursor
     * @return
     */
    abstract Entity getEntityFromCursor(Cursor cursor, Context context);

    /**
     * Persist Entity into database.
     *
     * @param entity the Entity which has to be persisted
     * @return - a List<Long> contains codes:
     * 1) _id(long) of product row (only 1 element List) OR
     * 2) -1L in case of db error (only 1 element List) OR
     * 3) more than one element List of error codes. For error codes
     * @see @{@link ErrorCodes}
     */
    public List<Long> insert(Entity entity, Context context) {
        // Creating empty result list
        List<Long> resultMessageList = new ArrayList<>();

        /* Checking not null fields that has no default values. If some of those
        fields are empty, an appropriate error code will be added to resultList */
        resultMessageList = checkFields(resultMessageList, entity);

        /*
        If there are no errors, we try to persist the Entity into database
         * First we create ContentValues then
         * we get writable database from dbHelper and
         * insert Entity into appropriate table
         */
        if (resultMessageList.isEmpty()) {
            ContentValues contentValues = getContentValues(entity);
            InventoryDbHelper helper = new InventoryDbHelper(context);
            SQLiteDatabase database = helper.getWritableDatabase();
            Long result = database.insert(mTableName, null, contentValues);
            resultMessageList.add(result);
            helper.close();
            Log.d(LOG_TAG, entity.getClass().getSimpleName() + " has been created. Id: " + result.toString());
        }
        return resultMessageList;
    }

    /* Checking not null fields that has no default values. If some of those
            fields are empty, an appropriate error code will be added to resultList */
    protected abstract List<Long> checkFields(List<Long> resultList, Entity entity);

    // Creating ContentValues for inserting rows into appropriate table
    protected abstract ContentValues getContentValues(Entity entity);

    /* Not implemented yet
    public abstract boolean update(Entity entity, Context context);

    public abstract boolean delete(Entity entity, Context context);
    */

}
