package net.sytes.csongi.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

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
        String phoneNumber=cursor.getString(supplierPhoneIndex);
        mSupplierPhoneTxt.setText(phoneNumber);

        ImageView phoneIcon=view.findViewById(R.id.call_supplier_icon);
        phoneIcon.setOnClickListener(v->{
            callSupplier(context,phoneNumber);
        });

        ImageView editSupplierIcon=view.findViewById(R.id.edit_supplier_icon);
        editSupplierIcon.setOnClickListener(v->{
            editSupplier(context,cursor.getLong(supplierIdIndex));
        });

        LinearLayout nameAndPhoneField=view.findViewById(R.id.supplier_name_and_phone_field);
        nameAndPhoneField.setOnClickListener(v->{
            listProductsBySupplier(context,cursor.getLong(supplierIdIndex));
        });
    }

    /**
     * Helper method for listing Supplier's products (by clicking on list item)
     * @param context the context
     * @param supplierId the id of Supplier (type: long)
     */
    private void listProductsBySupplier(Context context, long supplierId) {
    }

    /**
     * Helper method for editing Supplier data (by clicking on edit icon in list item)
     * @param context the context
     * @param supplierId the id of Supplier (type: long)
     */
    private void editSupplier(Context context, long supplierId) {
        Intent editIntent=new Intent(context,SupplierEditActivity.class);
        Uri supplierToEditUri= ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI,supplierId);
        editIntent.setData(supplierToEditUri);
        context.startActivity(editIntent);
    }

    /**
     * Helper method for calling Supplier (by clicking on phone icon in list item
     * @param context - the context
     * @param phoneNumber - the phone number (String)
     */
    private void callSupplier(Context context, String phoneNumber) {
        Intent callIntent=new Intent(Intent.ACTION_DIAL);

        // parsing the phone number string into Uri and set it
        Uri phoneNumberUri=Uri.parse("tel://"+phoneNumber);
        callIntent.setData(phoneNumberUri);

        context.startActivity(callIntent);
    }
}
