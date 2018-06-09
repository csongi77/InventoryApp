package net.sytes.csongi.inventoryapp.data;

import android.support.annotation.NonNull;

/**
 * An interface for getting Cursor objects via DB query methods
 * No implementation yet but later it can be used to create simple queries for EntityManagers.
 */
interface InventoryQuery {
    /**
     * @return The table to query, must be not null.
     */
    @NonNull
    String getTable();

    /**
     * @return The array of columns to return
     * default value: null for all columns. Else implement it:
     * String[] projection = {
     *      BaseColumns._ID,
     *      TableEntry.COLUMN_NAME_1,
     *      TableEntry.COLUMN_NAME_2
     *     // ... etc.
     *     };
     */
    default String[] getColumns(){
        return null;
    }

    /**
     * @return The columns for the WHERE clause, default null.
     * Using WHERE clause SelectionArgs MUST be implemented, too.
     * Example:
     * String selection = TableEntry.COLUMN_NAME_X + " = ?";
     */
    default String getSelection(){
        return null;
    }

    /**
     * @return The values for the WHERE clause, default null (if Selection is null)
     * Example:
     * String[] selectionArgs = { "Value" };
     */
    default String[] getSelectionArgs(){
        return null;
    }

    /**
     * At the moment it's unnecessary but we left it here for case of later implementation
     * @return null
     */
    default String getGroupBy(){
        return null;
    }

    /**
     * At the moment it's unnecessary but we left it here for case of later implementation
     * @return null
     */
    default String getHaving(){
        return null;
    }

    /**
     * @return The order of results
     * Implement it:
     * String sortOrder = TableEntry.COLUMN_NAME + " ASC|DESC";
     */
    default String getOrderBy(){
        return null;
    }

    /**
     * The number of results has to be returned by Cursor. Default is null (retrieve all results)
     * @return
     */
    default String getLimit(){
        return null;
    }

    /**
     * Method for resetting the Query object, since it's an inner class of the concrete EntityManager
     * @return true if resetting was succesful, false in any other case.
     */
    boolean resetQuery();
}
