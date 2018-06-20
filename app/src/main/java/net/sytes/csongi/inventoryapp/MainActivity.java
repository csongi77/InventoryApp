package net.sytes.csongi.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final long MINIMUM_NUMBER_OF_SUPPLIERS = 3L;
    private static final int ONLY_ONE_RESULT = 1;
    private static final int FIRST_RESULT=0;
    @BindView(R.id.txt_response_message)
    TextView mResponseMessage;
    @BindView(R.id.btn_dummy)
    Button mButton;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);



    }


}
