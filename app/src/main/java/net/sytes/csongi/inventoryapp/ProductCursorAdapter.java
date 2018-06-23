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

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView=LayoutInflater.from(context).inflate(R.layout.product_list_item,parent,false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String productName=cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
        int productQuantity=cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY));
        int productPrice=cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRICE));
        ((TextView)view.findViewById(R.id.product_name_txt)).setText(productName);
        ((TextView)view.findViewById(R.id.price_txt)).setText(String.valueOf(productPrice));
        ((TextView)view.findViewById(R.id.quantity_txt)).setText(String.valueOf(productQuantity));
    }
}
