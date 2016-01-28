package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;

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

    @Override
    protected void onStart() {
        super.onStart();

        if (!mHasLaidOutActionbar)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void onStartOverButtonClicked(View v) {
        Intent i = new Intent(ReceiptActivity.this, PrinticularCartManager.getInstance().getRootActivty());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
