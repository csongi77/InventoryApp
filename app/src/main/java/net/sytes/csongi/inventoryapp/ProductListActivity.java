package net.sytes.csongi.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.sytes.csongi.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static net.sytes.csongi.inventoryapp.data.InventoryContract.*;

public class ProductListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER=42;

    Unbinder unbinder;
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        unbinder= ButterKnife.bind(this);
        ListView listView=findViewById(R.id.product_list_view);
        mAdapter=new ProductCursorAdapter(this,null);
        listView.setAdapter(mAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader loader=null;
        if(id==PRODUCT_LOADER) {
            loader = new CursorLoader(getApplicationContext(), ProductEntry.CONTENT_URI,null,null,null,null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
