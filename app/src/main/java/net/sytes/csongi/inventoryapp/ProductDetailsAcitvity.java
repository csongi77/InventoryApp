package net.sytes.csongi.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class ProductDetailsAcitvity extends AppCompatActivity {

    // declaring constants
    private static final int PRODUCT_LOADER = 824;
    private static final int SUPPLIER_LOADER = 282;
    private static final long SUPPLIER_UNDEFINED = 0L;
    private static final int DECREASE_VALUE = -1;
    private static final int INCREASE_VALUE = 1;
    private static final String TAG = ProductDetailsAcitvity.class.getSimpleName();
    private static final int SINGLE_ITEM = 1;

    // declaring and assinging views with ButterKnife
    @BindView(R.id.product_details_decrease_by_one)
    ImageView mDecreaseQuantityByOne;
    @BindView(R.id.product_details_increase_by_one)
    ImageView mIncreaseQuantityByOne;
    @BindView(R.id.product_details_call_supplier)
    ImageView mCallSupplier;
    @BindView(R.id.product_details_price)
    TextView mProductPriceTxt;
    @BindView(R.id.product_details_product_name)
    TextView mProductName;
    @BindView(R.id.product_details_quantity)
    TextView mProductQuantityText;
    @BindView(R.id.product_details_suppler)
    TextView mSupplierName;
    @BindView(R.id.product_details_supplier_phone)
    TextView mSupplierPhone;

    // declaring integer and long variables
    private int mProductQuantity, mProductPrice;
    private long mSupplierId;
    private Uri mUri;
    private LoaderManager.LoaderCallbacks<Cursor> mSupplierLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_acitvity);
        Unbinder unbinder = ButterKnife.bind(this);

        //  receive intent data (uri of product). If no uri has been set then go back
        mUri = getIntent().getData();
        if (mUri == null) {
            Toast.makeText(this, R.string.product_details_uri_didnt_set_up_error, Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);
        }

        // set up Product Loader Callback
        LoaderManager.LoaderCallbacks<Cursor> mProductLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), mUri, null, null, null, null);
            }

            /*
            set product onLoadFinished callback. If everything is OK we set up appropriate product related
             views and start to load Supplier data.
             */
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                // first we set supplier id to 0L
                mSupplierId = SUPPLIER_UNDEFINED;
                if (data.moveToFirst()) {

                    // getting and displaying data
                    mProductName.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME)));
                    mProductQuantity = data.getInt(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_QUANTITY));

                    // if there is a single item we change 'pcs' to '1 piece'
                    if (mProductQuantity > SINGLE_ITEM)
                        mProductQuantityText.setText(String.format(getString(R.string.product_details_quantity), mProductQuantity));
                    else
                        mProductQuantityText.setText(getString(R.string.product_details_quantity_one_item));
                    mProductPrice = data.getInt(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));
                    mProductPriceTxt.setText(String.format(getString(R.string.product_details_price), mProductPrice));
                    mSupplierId = data.getLong(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SUPPLIER_ID));
                }
                if (mSupplierId != SUPPLIER_UNDEFINED) {
                    // when loading of product was succesful we start to load the supplier with appropriate id
                    getLoaderManager().initLoader(SUPPLIER_LOADER, null, mSupplierLoaderCallback);
                } else {
                    Toast.makeText(ProductDetailsAcitvity.this, R.string.product_details_no_supplier_set_up_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                loader = null;
            }
        };

        // declare supplier callback
        mSupplierLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), ContentUris.withAppendedId(SupplierEntry.CONTENT_URI, mSupplierId), null, null, null, null);
            }

            /*
             when product is loaded set up appropriate views and load supplier data. Moreover we
             make call icon clickable and set onClickListener on them.
             */
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data.moveToFirst()) {
                    mSupplierName.setText(data.getString(data.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME)));
                    String phoneNumber = data.getString(data.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE));
                    mSupplierPhone.setText(phoneNumber);
                    mCallSupplier.setClickable(true);
                    mCallSupplier.setFocusable(true);

                    // set onClickListener on the call button
                    mCallSupplier.setOnClickListener(v ->
                            callSupplier(getApplicationContext(), phoneNumber)
                    );
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                loader = null;
            }
        };

        // init product loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, mProductLoaderCallback);

        mDecreaseQuantityByOne.setOnClickListener(v -> {
            // decreasing quantity and save it
            updateProductQuantity(DECREASE_VALUE);
            mProductQuantityText.setText(String.format(getString(R.string.product_details_quantity), mProductQuantity));
        });

        mIncreaseQuantityByOne.setOnClickListener(v -> {
            // increasing quantity and save it
            updateProductQuantity(INCREASE_VALUE);
            mProductQuantityText.setText(String.format(getString(R.string.product_details_quantity), mProductQuantity));
        });
    }

    // inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return true;
    }

    /**
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.product_details_menu_edit_product:
                // set intent to edit activity
                Intent editProductIntent = new Intent(ProductDetailsAcitvity.this, ProductEditActivity.class);
                editProductIntent.setData(mUri);
                startActivity(editProductIntent);
                return true;
            case R.id.product_details_menu_delete_product:
                // deleting product after displaying a warning dialog
                warnAndDeleteProduct();
                return true;
            case android.R.id.home:
                // go back
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper method for calling Supplier (by clicking on phone icon in list item
     *
     * @param context     - the context
     * @param phoneNumber - the phone number (String)
     */
    private void callSupplier(Context context, String phoneNumber) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);

        // parsing the phone number string into Uri and set it
        Uri phoneNumberUri = Uri.parse("tel://" + phoneNumber);
        callIntent.setData(phoneNumberUri);

        context.startActivity(callIntent);
    }

    /**
     * helper method for updating product quantity with the new value.
     * This method has the responsibility for checking that the quantity must greater than 0.
     *
     * @param value - the new value to persist
     */
    private void updateProductQuantity(int value) {
        int newValue = mProductQuantity + value;
        if (newValue > 0) {
            ContentValues quantityToUpdate = new ContentValues();
            quantityToUpdate.put(ProductEntry.COLUMN_NAME_QUANTITY, newValue);
            int result = getContentResolver().update(mUri, quantityToUpdate, null, null);
            if (result == 1) {
                Log.i(TAG, "updateProductQuantity:: updated by " + value);
            }
        }
    }

    /**
     * helper method for deleting product
     */
    private void warnAndDeleteProduct() {
        AlertDialog warnBeforeDelete = new AlertDialog.Builder(this)
                .setTitle(R.string.product_details_confirm_delete_title)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.product_details_delete_warning_message)
                .setPositiveButton(R.string.product_details_positive_button, (dialog, which) -> {
                    int removingResult = getContentResolver().delete(mUri, null, null);
                    if (removingResult == 1) {
                        getLoaderManager().destroyLoader(SUPPLIER_LOADER);
                        getLoaderManager().destroyLoader(PRODUCT_LOADER);
                        finish();
                    } else
                        Toast.makeText(ProductDetailsAcitvity.this, R.string.product_details_delete_unsuccessful_message, Toast.LENGTH_SHORT).show();
                })
                .setCancelable(true)
                .setNegativeButton(R.string.product_details_negative_button, null)
                .create();
        warnBeforeDelete.show();
    }

}
