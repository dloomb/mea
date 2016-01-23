package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;

public class ReceiptActivity extends CheckoutActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(getIntent().getStringExtra("ORDER_JSON"));
    }
}
