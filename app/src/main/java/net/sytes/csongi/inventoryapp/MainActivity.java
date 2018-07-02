package net.sytes.csongi.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Unbinder unbinder;
    @BindView(R.id.product_list_view)
    ListView mListView;
    @BindView(R.id.list_is_empty)
    ImageView mListIsEmptyImage;
    @BindView(R.id.fab_new_product)
    FloatingActionButton mFabNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        mListView.setEmptyView(mListIsEmptyImage);
        mFabNewProduct.setOnClickListener(v -> {
            Intent addNewProductIntent = new Intent(getApplicationContext(), ProductEditActivity.class);
            startActivity(addNewProductIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.edit_suppliers:
                Log.d(LOG_TAG, "suppliers menu item selected");
                Intent intent = new Intent(getApplicationContext(), SuppliersListActivity.class);
                startActivity(intent);
                return true;
            default:
                Log.d(LOG_TAG, "nothing happened");
                return super.onOptionsItemSelected(item);
        }
    }


}
