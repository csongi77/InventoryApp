package net.sytes.csongi.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SupplierEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = SupplierEditActivity.class.getSimpleName();
    @BindView(R.id.edit_supplier_name)
    EditText mSupplierName;

    @BindView(R.id.edit_supplier_phone)
    EditText mSupplierPhone;

    Unbinder unbinder;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_edit);
        unbinder = ButterKnife.bind(this);

        // getting Intent data
        mUri = getIntent().getData();

        // check whether mUri has id (for editing existing Supplier). If no, a new Supplier will created
        if (mUri != null) {
            setTitle("Edit Supplier");
            setupViews();
        } else {
            setTitle("Insert new Supplier");
            invalidateOptionsMenu();
        }
    }

    /**
     * inflating menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem) {
            case R.id.edit_save:
                Log.w(LOG_TAG, "save has clicked");
                saveSupplier();
                finish();
                return true;
            case R.id.edit_delete:
                Log.w(LOG_TAG, "delete has clicked");
                deleteSupplier();
                finish();
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mUri==null){
            MenuItem item=menu.findItem(R.id.edit_delete);
            item.setVisible(false);
        }
        return true;
    }

    // helper method for fill in views for edit
    private void setupViews() {

        // first me make a query with the specified Uri
        Cursor supplierCursor=getContentResolver().query(mUri,null,null,null,null);

        // then we fill out the form (if we have valid result)
        if(supplierCursor.moveToFirst()){
            mSupplierName.setText(supplierCursor.getString(supplierCursor.getColumnIndexOrThrow(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_NAME)));
            mSupplierPhone.setText(supplierCursor.getString(supplierCursor.getColumnIndexOrThrow(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE)));
        }
    }

    // method for deleting current entity
    private void deleteSupplier() {
        getContentResolver().delete(mUri,null,null);
    }

    /**
     * Helper method for saving Supplier.
     */
    private void saveSupplier() {
        String supplierName, supplierPhone;
        supplierName = mSupplierName.getText().toString().trim();
        supplierPhone = mSupplierPhone.getText().toString().trim();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
        contentValues.put(InventoryContract.SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE, supplierPhone);

        // if it's an existing Supplier, modify it
        if (mUri != null) {
            // todo add update method
        } else {
            Uri uri = getContentResolver().insert(InventoryContract.SupplierEntry.CONTENT_URI, contentValues);
            if (uri != null)
                Toast.makeText(this, "Supplier with id: " + String.valueOf(ContentUris.parseId(uri)) + " has been added", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Error adding supplier", Toast.LENGTH_LONG).show();
        }
    }
}
