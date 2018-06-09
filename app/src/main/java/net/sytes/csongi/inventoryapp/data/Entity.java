package net.sytes.csongi.inventoryapp.data;

import android.os.Parcelable;

/**
 * Basic Entity
 */
public interface Entity extends Parcelable{
    long getId();
    void setId(long id);
}
