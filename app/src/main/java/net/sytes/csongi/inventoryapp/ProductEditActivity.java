package net.sytes.csongi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private long mSupplierId;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        unbinder = ButterKnife.bind(this);

        /*
        receiving Intent. If mUri is null, then the purpose of this activity is to create a new
        Product entity. Otherwise (if mUri is not null) we have to edit an existing Product
         */
        Intent recievedIntent = getIntent();
        mUri = recievedIntent.getData();
        if (mUri != null) {
            setTitle("Edit Product");
            // TODO: 2018.07.02. load product
        } else {
            setTitle("Add new Product");
        }

        // TODO: 2018.07.02. set up menu/invalidate menu options

        // set up spinner
        setupSpinner();

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

        // TODO: 2018.07.02. set up loader callback for product

        // preloading Suppliers
        getLoaderManager().initLoader(SUPPLIER_LOADER, null, supplierLoaderCallback);

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
        while (cursorOfSuppliers.moveToNext()) {
            if (supplierId == cursorOfSuppliers.getLong(cursorOfSuppliers.getColumnIndexOrThrow(SupplierEntry._ID)))
                return position;
            position++;
        }
        return -2;

    }
}
