package net.sytes.csongi.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
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

    @BindView(R.id.supplier_phone_txt)
    TextView mSupplierPhoneTxt;

    Unbinder unbinder;
    private CursorAdapter mSpinnerAdapter;
    private Uri mUri;
    private String mProductName;
    private long mSupplierId;
    private int mProductPrice, mProductQuantity;
    private View.OnFocusChangeListener mFocusChangeListener;
    private boolean mStartedToEdit = false;
    private Cursor mSpinnerCursor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        unbinder = ButterKnife.bind(this);

        // set up product Loader callback
        LoaderManager.LoaderCallbacks<Cursor> mProductLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), mUri, null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                // check is there a result
                if (data.moveToNext()) {
                    // if yes, fill out the form with original data
                    mProductNameEdit.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRODUCT_NAME)));
                    mProductPriceEdit.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE)));
                    mProductQuantityEdit.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_QUANTITY)));
                }
                mSupplierId = data.getLong(data.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SUPPLIER_ID));
                mSupplierSpinner.setSelection(getPositionFromId(mSupplierId));
                mSupplierPhoneTxt.setText(mSpinnerCursor.getString(mSpinnerCursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)));
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                loader = null;
            }
        };

        // set up supplier Loader callback
        LoaderManager.LoaderCallbacks<Cursor> supplierLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), SupplierEntry.CONTENT_URI, null, null, null, null);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                // if there are no existing suppliers ask user to create it and go back
                if (!data.moveToNext()) {
                    Toast.makeText(ProductEditActivity.this, R.string.product_edit_no_existing_suppliers, Toast.LENGTH_LONG).show();
                    NavUtils.navigateUpFromSameTask(ProductEditActivity.this);
                }

                // we have to add some rows to result in order to display "Select supplier" spinner option
                String[] columnNames = data.getColumnNames();
                MatrixCursor additionalInfoCursor = new MatrixCursor(columnNames);

                // add Select supplier row:
                additionalInfoCursor.newRow().add(SupplierEntry._ID, -1L)
                        .add(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME, getString(R.string.product_edit_select_supplier))
                        .add(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE, getString(R.string.product_edit_not_available));

                Cursor[] cursors = {additionalInfoCursor, data};
                mSpinnerCursor = new MergeCursor(cursors);

                // add cursor to spinnerAdapter and set the 1st option ("Select supplier")
                mSpinnerAdapter.swapCursor(mSpinnerCursor);
                mSupplierSpinner.setSelection(0);

                // we move the cursor for the first option and set Phone text to "N/A".
                mSpinnerCursor.moveToFirst();
                mSupplierPhoneTxt.setText(mSpinnerCursor.getString(mSpinnerCursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)));
                if (mUri != null) {
                    // if we are in edit mode we start to load the products
                    getLoaderManager().initLoader(PRODUCT_LOADER, null, mProductLoaderCallback);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mSpinnerAdapter.swapCursor(null);
                mSpinnerCursor.close();
            }
        };

        // preloading Suppliers
        getLoaderManager().initLoader(SUPPLIER_LOADER, null, supplierLoaderCallback);

        /*
        receiving Intent. If mUri is null, then the purpose of this activity is to create a new
        Product entity. Otherwise (if mUri is not null) we have to edit an existing Product
         */
        Intent recievedIntent = getIntent();
        mUri = recievedIntent.getData();
        if (mUri != null) {
            // we are in edit mode
            setTitle(getString(R.string.product_edit_edit_product_title));
        } else {
            // we are in new product mode
            setTitle(getString(R.string.product_edit_add_new_product_title));
        }

        // set up spinner
        setupSpinner();

        /* set up listener in order to check whether user started to edit any field.
            If at least 1 field has been changed then we set mStartedToEdit to true.
            If we delete all infos from all fields, this value will set back to false.
            Also we can check which fields must filled with valid data
         */
        mFocusChangeListener = (v, hasFocus) -> {


            if (!hasFocus) {
                mStartedToEdit = true;
                int whichView = v.getId();
                switch (whichView) {
                    case R.id.product_quantity_edit:
                        if (!isProductNumberValid(mProductQuantityEdit)) {
                            mProductQuantityEdit.setText(null);
                            mProductQuantityEdit.setHint(R.string.product_edit_enter_valid_number);
                            mProductQuantityEdit.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        }
                        break;
                    case R.id.product_price_edit:
                        if (!isProductNumberValid(mProductPriceEdit)) {
                            mProductPriceEdit.setText(null);
                            mProductPriceEdit.setHint(R.string.product_edit_enter_valid_number);
                            mProductPriceEdit.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        }
                        break;
                    case R.id.product_name_edit:
                        if (!isProductNameValid(mProductNameEdit)) {
                            mProductNameEdit.setHint(R.string.product_edit_name_cannot_be_empty);
                            mProductNameEdit.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        }
                        break;
                }
            }
        };

        // assign listener to observed fields
        mProductPriceEdit.setOnFocusChangeListener(mFocusChangeListener);
        mProductQuantityEdit.setOnFocusChangeListener(mFocusChangeListener);
        mProductNameEdit.setOnFocusChangeListener(mFocusChangeListener);
        mSupplierSpinner.setOnFocusChangeListener(mFocusChangeListener);
    }

    /**
     * helper method for validating product name
     *
     * @param editTextToValidate
     * @return
     */
    private boolean isProductNameValid(EditText editTextToValidate) {
        String textToValidate = editTextToValidate.getText().toString().trim();
        return !TextUtils.isEmpty(textToValidate);
    }

    /**
     * helper method for checking product quantity
     *
     * @param editTextToValidate - the field we check
     * @return true if in mProductQuantityEdit EditText:
     * 1) is not empty AND
     * 2) a positive integer number is entered
     * false otherwise
     */
    private boolean isProductNumberValid(EditText editTextToValidate) {
        String textString = editTextToValidate.getText().toString().trim();
        if (TextUtils.isEmpty(textString) || !TextUtils.isDigitsOnly(textString)) return false;
        int numberToCheck = Integer.parseInt(textString);
        if (numberToCheck > 0) return true;
        return false;
    }


    // creating options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing        android:layout_height="48dp"
     * to
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
            warnUserAboutFinish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * helper method to warn user that they started to fill out fields but pressed back button
     */
    private void warnUserAboutFinish() {
        AlertDialog exitWithoutSave=new AlertDialog.Builder(this)
                .setTitle(R.string.product_edit_alert_dialog_title)
                .setMessage(R.string.product_edit_alert_dialog_message)
                .setPositiveButton(R.string.product_edit_alert_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProductEditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.product_edit_alert_dialog_negative_button,null)
                .setCancelable(true)
                .create();
        exitWithoutSave.show();
    }

    private void checkAndSaveProduct() {

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


        // sanity check. If all values are set then insert/update it
        if (isProductNameValid(mProductNameEdit) && isProductNumberValid(mProductPriceEdit) && isProductNumberValid(mProductQuantityEdit) && mSupplierSpinner.getSelectedItemId() > 0) {
            ContentValues productToSaveValues = new ContentValues();
            productToSaveValues.put(ProductEntry.COLUMN_NAME_PRODUCT_NAME, mProductName);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_PRICE, mProductPrice);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_QUANTITY, mProductQuantity);
            productToSaveValues.put(ProductEntry.COLUMN_NAME_SUPPLIER_ID, mSupplierId);

            // if mUri is null, then we insert a new product
            if (mUri == null) {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, productToSaveValues);
                if (newUri != null) {

                    // feedback about successful save
                    String message=String.format(getString(R.string.product_edit_saved_successfully),String.valueOf(ContentUris.parseId(newUri)));
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                } else {

                    // feedback about unsuccessful save
                    Toast.makeText(this, R.string.product_edit_save_was_unsuccessful, Toast.LENGTH_SHORT).show();
                }

                // if mUri is not null, then update an existing product
            } else {
                int affectedRows = getContentResolver().update(mUri, productToSaveValues, null, null);
                if (affectedRows == 1) {

                    // feedback about successful update
                    String message=String.format(getString(R.string.product_edit_updated_successfully),String.valueOf(ContentUris.parseId(mUri)));
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                } else {

                    // feedback about unsuccesful update
                    Toast.makeText(this, R.string.product_edit_update_unsuccessful, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        } else {

            // otherwise inform user that all fields must set before save
            Toast.makeText(this, R.string.product_edit_all_fields_must_set, Toast.LENGTH_SHORT).show();
        }
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
                mSpinnerCursor.moveToPosition(position);
                mSupplierPhoneTxt.setText(mSpinnerCursor.getString(mSpinnerCursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)));
                Log.d(TAG, "onItemSelected: ID=" + mSupplierId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * helper method for determining position of Supplier in spinner from supplierId
     *
     * @param supplierId - id of supplier
     * @return the position of supplier in spinner
     */
    private int getPositionFromId(long supplierId) {
        // the first element is always "select" cursor item
        mSpinnerCursor.moveToFirst();
        int position = 0;

        // let's search the supplier's position in Cursor which has the same id as in Product's supplierId
        while (mSpinnerCursor.moveToNext()) {
            position++;
            if (mSpinnerCursor.getLong(mSpinnerCursor.getColumnIndexOrThrow(SupplierEntry._ID)) == supplierId)
                return position;
        }
        return 0;
    }
}
