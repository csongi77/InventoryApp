package net.sytes.csongi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;
import static net.sytes.csongi.inventoryapp.data.InventoryContract.SupplierEntry;

public class ProductEditActivity extends AppCompatActivity {

    private static final String TAG = ProductEditActivity.class.getSimpleName();
    private static final int SUPPLIER_LOADER = 42;
    private static final int PRODUCT_LOADER = 534;

    @BindView(R.id.product_name_edit)
    EditText mProductNameEdit;

    @BindView(R.id.product_price_edit)
    EditText mProductPriceEdit;

    @BindView(R.id.product_quantity_edit)
    EditText mProductQuantityEdit;

    @BindView(R.id.supplier_spinner)
    Spinner mSupplierSpinner;

    private Unbinder unbinder;
    private CursorAdapter mSpinnerAdapter;
    private Uri mUri;
    private String mProductName;
    private long mSupplierId;
    private int mProductPrice, mProductQuantity;
    private View.OnFocusChangeListener mFocusChangeListener;
    private boolean mStartedToEdit=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        unbinder = ButterKnife.bind(this);

        // set up supplier Loader callback
        LoaderManager.LoaderCallbacks<Cursor> supplierLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), SupplierEntry.CONTENT_URI, null, null, null, null);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                // we have to add some rows to result in order to
                String[] columnNames = data.getColumnNames();
                MatrixCursor additionalInfoCursor = new MatrixCursor(columnNames);

                // add Select supplier row:
                additionalInfoCursor.newRow().add(SupplierEntry._ID, -1L)
                        .add(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME, "Select Supplier")
                        .add(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE, "N/A");

                Cursor[] cursors = {additionalInfoCursor, data};
                MergeCursor mergeCursor = new MergeCursor(cursors);

                mSpinnerAdapter.swapCursor(mergeCursor);

                /*
                 * if mUri is specified we are in Edit mode so there must be a previously
                 * selected Supplier.
                 */
                if (mUri == null) {
                    mSupplierSpinner.setSelection(0);
                } else {
                    // we are in edit mode, and set spinner to
                    mSupplierSpinner.setSelection(getPositionFromId(mUri, data));
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mSpinnerAdapter.swapCursor(null);
            }
        };
        /*
        receiving Intent. If mUri is null, then the purpose of this activity is to create a new
        Product entity. Otherwise (if mUri is not null) we have to edit an existing Product
         */
        Intent recievedIntent = getIntent();
        mUri = recievedIntent.getData();
        if (mUri != null) {
            setTitle("Edit Product");
            // TODO: 2018.07.02. load product
            loadProduct();
        } else {
            setTitle("Add new Product");
            invalidateOptionsMenu();
        }

        // set up spinner
        setupSpinner();

        /* set up listener in order to check whether user started to edit any field.
            If at least 1 field has been changed then we set mStartedToEdit to true.
            If we delete all infos from all fields, this value will set back to false.
         */
        mFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mProductNameEdit.getText().toString().trim().length() > 0 ||
                            mProductQuantityEdit.getText().toString().trim().length() > 0 ||
                            mProductPriceEdit.getText().toString().trim().length() > 0 ||
                            mSupplierSpinner.getSelectedItemId() > 0) mStartedToEdit = true;
                    else mStartedToEdit = false;
                }
            }
        };

        // assign listener to observed fields
        mProductPriceEdit.setOnFocusChangeListener(mFocusChangeListener);
        mProductQuantityEdit.setOnFocusChangeListener(mFocusChangeListener);
        mProductNameEdit.setOnFocusChangeListener(mFocusChangeListener);
        mSupplierSpinner.setOnFocusChangeListener(mFocusChangeListener);


        // TODO: 2018.07.02. set up loader callback for product for edit

        // preloading Suppliers
        getLoaderManager().initLoader(SUPPLIER_LOADER, null, supplierLoaderCallback);

    }

    /**
     * this helper method queries the product which we want to edit, and fills out the
     * empty form (product name, supplier etc.)
     */
    private void loadProduct() {

    }

    // creating options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // preparing options menu in case we add new product (delete item must be hide).
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.edit_delete);
        deleteItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
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
            case R.id.edit_save:
                checkAndSaveProduct();
                return true;
            case R.id.edit_delete:
                warnAndDeleteProduct();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (mStartedToEdit) {
//            warnUserAboutFinish();
            Toast.makeText(this, "back pressed, but user started to edit", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "back pressed, mStartedToEdit=" + mStartedToEdit, Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);
            //super.onBackPressed();
        }
    }

    private void checkAndSaveProduct() {
        Toast.makeText(this, "CheckAndSaveProduct", Toast.LENGTH_SHORT).show();
        // retrieving values from spinner and edit texts
        mProductName = mProductNameEdit.getText().toString().trim();
        mProductPrice = 0;
        mProductQuantity = 0;
        String priceString = mProductPriceEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(priceString) && TextUtils.isDigitsOnly(priceString) && priceString.length() > 0) {
            mProductPrice = Integer.parseInt(priceString);
        }
        String quantityString = mProductQuantityEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(priceString) && TextUtils.isDigitsOnly(quantityString) && quantityString.length() > 0) {
            mProductQuantity = Integer.parseInt(quantityString);
        }

        // sanity check. If all values are set then insert it
        if (!TextUtils.isEmpty(mProductName) && mSupplierSpinner.getSelectedItemId() > 0) {
            ContentValues productToSaveValues = new ContentValues();
            productToSaveValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, mProductName);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_PRICE, mProductPrice);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_QUANTITY, mProductQuantity);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_SUPPLIER_ID, mSupplierId);

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, productToSaveValues);
            if (newUri != null) {
                Toast.makeText(this, "Product saved with ID: " + ContentUris.parseId(newUri), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Saving product is unsuccessful", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(this, "Product name and Supplier must be set!", Toast.LENGTH_SHORT).show();
        }
    }

    private void warnAndDeleteProduct() {
        Toast.makeText(this, "warnAndDeleteProduct", Toast.LENGTH_SHORT).show();
    }

    /**
     * helper method for setting up spinner for selecting supplier
     */
    private void setupSpinner() {
        mSpinnerAdapter = new SupplierSpinnerCursorAdapter(this, null);
        mSupplierSpinner.setAdapter(mSpinnerAdapter);
        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSupplierId = id;
                Log.d(TAG, "onItemSelected: ID=" + mSupplierId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private int getPositionFromId(Uri uriOfProduct, Cursor cursorOfSuppliers) {
        long supplierId = ContentUris.parseId(uriOfProduct);
        // // TODO: 2018.07.01. query for product and set up views

        // check id before cast it
        if (supplierId < Integer.MIN_VALUE || supplierId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (supplierId + " cannot be cast to int without changing its value.");
        }

        // move through data. when
       /* while (cursorOfSuppliers.moveToNext()) {
            if (supplierId == cursorOfSuppliers.getLong(cursorOfSuppliers.getColumnIndexOrThrow(SupplierEntry._ID)))
                return position;
            position++;
        }*/
        return -2;

    }
}
