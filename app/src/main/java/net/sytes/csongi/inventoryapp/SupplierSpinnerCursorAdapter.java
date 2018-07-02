package net.sytes.csongi.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.SupplierEntry;

/**
 * Cursor Adapter for Spinner in ProductEditActivity.
 * The user may choose only existing Supplier when creates or edits a Product
 */
public class SupplierSpinnerCursorAdapter extends CursorAdapter {

    // default ctor
    public SupplierSpinnerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View toReturn = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        return toReturn;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME));
        ((TextView) view.findViewById(R.id.supplier_spinner_item_txt)).setText(supplierName);
    }

    // know-how from: https://android--code.blogspot.com/2015/08/android-spinner-hint.html
    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }


}
