package net.sytes.csongi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

import butterknife.BindView;

public class ProductCursorAdapter extends CursorAdapter {

    private static final String TAG = ProductCursorAdapter.class.getSimpleName();
    private Context mContext;
    private static final int SINGLE_ITEM = 1;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // retrieving data from cursor
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
        int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY));
        int productPrice = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRICE));
        long _id = cursor.getLong(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry._ID));

        // assign the values to the appropriate views
        ((TextView) view.findViewById(R.id.product_name_txt)).setText(productName);
        ((TextView) view.findViewById(R.id.price_txt)).setText(mContext.getString(R.string.product_details_price, productPrice));
        if(productQuantity>SINGLE_ITEM) {
            ((TextView) view.findViewById(R.id.quantity_txt)).setText(mContext.getString(R.string.product_details_quantity, productQuantity));
        } else {
            ((TextView) view.findViewById(R.id.quantity_txt)).setText(mContext.getString(R.string.product_details_quantity_one_item));
        }

        // set up onClickListener in order to open product details activity
        LinearLayout toDetails = view.findViewById(R.id.to_details);
        toDetails.setOnClickListener(v -> {
            Intent openProductDetails = new Intent(mContext, ProductDetailsAcitvity.class);
            openProductDetails.setDataAndType(ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, _id),
                    InventoryContract.ProductEntry.CONTENT_ITEM_TYPE);
            mContext.startActivity(openProductDetails);
        });

        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(v -> {
            sellItem(_id, productQuantity);
        });
    }

    /**
     * helper method for sell a product
     * @param id - id of product
     * @param productQuantity - original quantity which must decreased by one
     */
    private void sellItem(long id, int productQuantity) {
        Log.i(TAG, "sellItem:: has called. Original quantity: "+productQuantity);
        int newQuantity = --productQuantity;

        // check that new quantity is greater than 0. In this case we update product
        if (newQuantity > 0) {
            ContentValues sellItem = new ContentValues();
            sellItem.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,newQuantity);
            int result= mContext.getContentResolver().update(ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI,id),sellItem,null,null);
            if(result==1){
                Log.i(TAG, "sellItem:: Product quantity has successfully changed to "+newQuantity);
            }
        }
    }

}
