package net.sytes.csongi.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SupplierCursorAdapter extends CursorAdapter {

    //@BindView(R.id.list_item_supplier_name) TextView mSupplierNameTxt;
    //@BindView(R.id.list_item_supplier_phone) TextView mSupplierPhoneTxt;

    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View toReturn = LayoutInflater.from(context).inflate(R.layout.supplier_list_item,parent,false);
        return toReturn;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int supplierNameIndex=cursor.getColumnIndexOrThrow(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_NAME);
        int supplierPhoneIndex=cursor.getColumnIndexOrThrow(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE);
        int supplierIdIndex = cursor.getColumnIndexOrThrow(InventoryContract.SupplierEntry._ID);
        TextView mSupplierNameTxt=view.findViewById(R.id.list_item_supplier_name);
        TextView mSupplierPhoneTxt=view.findViewById(R.id.list_item_supplier_phone);
        mSupplierNameTxt.setText(cursor.getString(supplierNameIndex));
        mSupplierPhoneTxt.setText(cursor.getString(supplierPhoneIndex));
        long _Id=cursor.getLong(supplierIdIndex);

    }
}
