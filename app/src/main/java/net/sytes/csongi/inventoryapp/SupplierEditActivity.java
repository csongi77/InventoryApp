package net.sytes.csongi.inventoryapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class SupplierEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = SupplierEditActivity.class.getSimpleName();
    private boolean mIsEdited = false;
    private static final int PRODUCT_LOADER = 391;

    @BindView(R.id.edit_supplier_name)
    EditText mSupplierName;

    @BindView(R.id.edit_supplier_phone)
    EditText mSupplierPhone;

    Unbinder unbinder;
    private Uri mUri;

    private LoaderManager.LoaderCallbacks<Cursor> mProductLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_edit);
        unbinder = ButterKnife.bind(this);

        // getting Intent data
        mUri = getIntent().getData();

        // check whether mUri has id (for editing existing Supplier). If no, a new Supplier will created
        if (mUri != null) {
            setTitle(getString(R.string.supplier_edit_edit_title));
            setupViews();
        } else {
            setTitle(getString(R.string.supplier_edit_insert_title));
            invalidateOptionsMenu();
        }

        // set up touch listeners
        View.OnTouchListener fieldTouchListener = (v, event) -> {
            mIsEdited = true;
            return false;
        };
        mSupplierName.setOnTouchListener(fieldTouchListener);
        mSupplierPhone.setOnTouchListener(fieldTouchListener);

        // set up onFocus listeners
        View.OnFocusChangeListener fieldCheckListener = (v, hasFocus) -> {
            int field = v.getId();
            if (!hasFocus) {
                switch (field) {
                    // checking on focus change whether compulsory field has been filled out...
                    case R.id.edit_supplier_name:
                        if (!isSupplierTextFieldValid(mSupplierName)) {
                            mSupplierName.setText(null);
                            mSupplierName.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mSupplierName.setHint(R.string.supplier_edit_error_name_empty);
                            break;
                        }
                    case R.id.edit_supplier_phone:
                        if (!isSupplierTextFieldValid(mSupplierPhone)) {
                            mSupplierPhone.setText(null);
                            mSupplierPhone.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mSupplierPhone.setHint(R.string.supplier_field_error_phone_empty);
                            break;
                        }
                }
            }
        };
        mSupplierName.setOnFocusChangeListener(fieldCheckListener);
        mSupplierPhone.setOnFocusChangeListener(fieldCheckListener);
    }

    /**
     * inflating menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_supplier_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // set up menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem) {
            case R.id.edit_supplier_save:
                Log.i(LOG_TAG, "save has clicked");
                saveSupplier();
                return true;
            case R.id.edit_supplier_delete:
                Log.i(LOG_TAG, "delete has clicked");
                deleteSupplier();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // preparing menu options. If user inserts a new supplier, delete menu items will be unavailable
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.edit_supplier_delete);
            deleteItem.setVisible(false);
        }
        return true;
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (mIsEdited) {
            warnUserBeforeExit();
        } else
            super.onBackPressed();
    }

    /**
     * helper method for validating product name
     *
     * @param editTextToValidate
     * @return - true if field is not empty (contains at least one character which is not a
     * single space character)
     */
    private boolean isSupplierTextFieldValid(EditText editTextToValidate) {
        String textToValidate = editTextToValidate.getText().toString().trim();
        return !TextUtils.isEmpty(textToValidate) && !(textToValidate.charAt(0) == (char) 32 && textToValidate.length() == 1);
    }

    // helper method to warn user before exit (when they started to fill out the forms)
    private void warnUserBeforeExit() {
        AlertDialog exitWarning = new AlertDialog.Builder(this)
                .setTitle(R.string.supplier_edit_confirm_leave_title)
                .setMessage(R.string.supplier_edit_confirm_leave_message)
                .setPositiveButton(R.string.supplier_edit_confirm_leave_positive,
                        (dialog, which) -> SupplierEditActivity.super.onBackPressed())
                .setNegativeButton(R.string.supplier_edit_confirm_leave_negative, null)
                .setCancelable(true)
                .create();
        exitWarning.show();
    }

    // helper method for fill in views for edit
    private void setupViews() {

        // first me make a query with the specified Uri
        Cursor supplierCursor = getContentResolver().query(mUri, null, null, null, null);

        // then we fill out the form (if we have valid result)
        if (supplierCursor.moveToFirst()) {
            mSupplierName.setText(supplierCursor.getString(supplierCursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME)));
            mSupplierPhone.setText(supplierCursor.getString(supplierCursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)));
        }
    }

    // method for deleting current entity
    private void deleteSupplier() {
        // ask user for confirmation
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.supplier_edit_delete_confirmation_title)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(mUri, null, null);
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .setCancelable(true)
                .show();
    }

    /**
     * Helper method for saving Supplier.
     */
    private void saveSupplier() {

        String supplierName, supplierPhone;
        supplierName = mSupplierName.getText().toString().trim();
        supplierPhone = mSupplierPhone.getText().toString().trim();

        // save/update data only if values are filled
        if (isSupplierTextFieldValid(mSupplierName) && isSupplierTextFieldValid(mSupplierPhone)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SupplierEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
            contentValues.put(SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE, supplierPhone);

            // if it's an existing Supplier, modify it
            if (mUri != null) {
                int affectedRows = getContentResolver().update(mUri, contentValues, null, null);
                if (affectedRows == 1)
                    // inform user that update was successful
                    Toast.makeText(this, String.format(getString(R.string.supplier_edit_update_successful), String.valueOf(ContentUris.parseId(mUri))), Toast.LENGTH_LONG).show();
                else
                    // inform user that update was unsuccessful
                    Toast.makeText(this, R.string.supplier_edit_update_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                Uri uri = getContentResolver().insert(SupplierEntry.CONTENT_URI, contentValues);
                if (uri != null)
                    // inform user that insert was successful
                    Toast.makeText(this, getString(R.string.supplier_edit_insert_successful,String.valueOf(ContentUris.parseId(uri))), Toast.LENGTH_LONG).show();
                else
                    // inform user that insert was unsuccessful
                    Toast.makeText(this, R.string.supplier_edit_insert_error, Toast.LENGTH_LONG).show();
            }
            finish();
        } else {
            Toast.makeText(this, R.string.supplier_edit_unfilled_field_error, Toast.LENGTH_LONG).show();
        }
    }
}
