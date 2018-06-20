package net.sytes.csongi.inventoryapp.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static net.sytes.csongi.inventoryapp.data.ErrorCodes.DB_WRITE_ERROR;
import static net.sytes.csongi.inventoryapp.data.ErrorCodes.NO_RESULTS;
import static net.sytes.csongi.inventoryapp.data.ErrorCodes.PRODUCT_NAME_EMPTY_ERROR;
import static net.sytes.csongi.inventoryapp.data.ErrorCodes.SUPPLIER_NAME_EMPTY_ERROR;
import static net.sytes.csongi.inventoryapp.data.ErrorCodes.SUPPLIER_PHONE_EMPTY_ERROR;
import static net.sytes.csongi.inventoryapp.data.ErrorCodes.UNKNOWN_ERROR;

/**
 * intDef for error messages. These values can be sent to clients in case of CRUD operations errors
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({DB_WRITE_ERROR, PRODUCT_NAME_EMPTY_ERROR, SUPPLIER_NAME_EMPTY_ERROR, SUPPLIER_PHONE_EMPTY_ERROR, UNKNOWN_ERROR, NO_RESULTS})
public @interface ErrorCodes {
    static final int DB_WRITE_ERROR=-1;
    static final int PRODUCT_NAME_EMPTY_ERROR=-2;
    static final int SUPPLIER_NAME_EMPTY_ERROR=-3;
    static final int SUPPLIER_PHONE_EMPTY_ERROR=-4;
    static final int UNKNOWN_ERROR=-5;
    static final int NO_RESULTS=-6;
}
