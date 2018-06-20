package net.sytes.csongi.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import net.sytes.csongi.inventoryapp.data.ProductEntity;
import net.sytes.csongi.inventoryapp.data.ProductEntityManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_response_message)
    TextView mResponseMessage;
    @BindView(R.id.btn_dummy) Button mButton;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder= ButterKnife.bind(this);

        mButton.setOnClickListener(v -> {
            insertProduct();
        });

    }

    /**
     * Helper method for inserting product
     */
    private void insertProduct() {

        /**
         * First we create a dummy productEntity.
         * Later it can be generated using TextFields.
         */
        ProductEntity productEntity = generateEntity();

        // Creating an entityManager
        ProductEntityManager entityManager = new ProductEntityManager(getApplicationContext());
        List<Long> result = entityManager.insertProduct(productEntity);

        if (result.size() == 1 && result.get(0) > 0L) {
            String toDisplay = String.format(getApplicationContext().getString(R.string.response_string), result.get(0), productEntity.getProductName());
            mResponseMessage.setText(toDisplay);
        } else {
            String[] keys = getResources().getStringArray(R.array.error_code_keys);
            String[] messageStrings = getResources().getStringArray(R.array.error_code_values);
            Map<Long,String> messageMap = new HashMap<>();
            for(int i=0; i< keys.length; i++){
                messageMap.put(Long.parseLong(keys[i]),messageStrings[i]);
            }
            StringBuilder builder = new StringBuilder();
            Iterator<Long> iterator = result.iterator();
            builder.append(getString(R.string.first_error_code) + messageMap.get(iterator.next()));
            while (iterator.hasNext()) {
                builder.append(getString(R.string.following_error_codes) + messageMap.get(iterator.next()));
            }
            mResponseMessage.setText(builder.toString());
        }
    }

    private ProductEntity generateEntity() {
        Random random = new Random();
        ProductEntity entityToReturn = new ProductEntity();
        entityToReturn.setProductName("Product Name-" + String.valueOf(random.nextInt(10000)));
      //  entityToReturn.setSupplierName("Supplier-" + String.valueOf(random.nextInt(100)));
        entityToReturn.setSupplierPhone(String.valueOf(random.nextInt(999999) + 1000000));
      //  entityToReturn.setQuantity(random.nextInt(100));
        entityToReturn.setPrice(random.nextInt(100) + 10);
        return entityToReturn;
    }
}
