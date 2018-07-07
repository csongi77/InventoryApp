package net.sytes.csongi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class SuppliersListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SUPPLIER_LOADER = 42;
    private static final String TAG = SuppliersListActivity.class.getSimpleName();

    @BindView(R.id.fab_new_product)
    FloatingActionButton mNewSupplierButton;
    @BindView(R.id.product_list_view)
    ListView mSupplierListView;
    @BindView(R.id.list_is_empty)
    View mEmptyView;

    Unbinder unbinder;
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setTitle(getString(R.string.suppliers_list_title));

        // assigning function to FAB
        mNewSupplierButton.setOnClickListener(v ->
                openEditSupplierWindow(SupplierEntry.CONTENT_URI)
        );

        // set up listView
        mSupplierListView = findViewById(R.id.product_list_view);
        mEmptyView = findViewById(R.id.list_is_empty);
        mSupplierListView.setEmptyView(mEmptyView);

        // set up Adapter for listView
        mAdapter = new SupplierCursorAdapter(this, null);
        mSupplierListView.setAdapter(mAdapter);

        // loading data
        getLoaderManager().initLoader(SUPPLIER_LOADER, null, this);
    }

    // overriding Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                SupplierEntry._ID,
                SupplierEntry.COLUMN_NAME_SUPPLIER_NAME,
                SupplierEntry.COLUMN_NAME_SUPPLIER_PHONE
        };
        return new CursorLoader(this, SupplierEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        // set up OnItemClickListener in order to open edit Supplier Activity
        mSupplierListView.setOnItemClickListener((parent, view, position, id) -> {
            Uri itemToOpen= ContentUris.withAppendedId(SupplierEntry.CONTENT_URI,id);
            Intent openItem=new Intent(SuppliersListActivity.this,SupplierEditActivity.class);
            openItem.setDataAndType(itemToOpen,SupplierEntry.CONTENT_ITEM_TYPE);
            Log.i(TAG, "onItemClick: "+id);
            startActivity(openItem);
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Helper method for opening SupplierEditActivity.
     *
     * @param mTargetUri - the Uri to set for intent.
     *                   If Uri has appended Id, the SupplierEditActivity has
     *                   to run as editor. Else new values must be added
     */
    private void openEditSupplierWindow(Uri mTargetUri) {
        Intent intent = new Intent(SuppliersListActivity.this, SupplierEditActivity.class);
        startActivity(intent);
    }
}
