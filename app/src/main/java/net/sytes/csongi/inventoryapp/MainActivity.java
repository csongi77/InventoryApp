
package net.sytes.csongi.inventoryapp;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.sytes.csongi.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProductDbHelper dbHelper=new ProductDbHelper(this);
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        
    }
}
