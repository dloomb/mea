package com.meamobile.printicular_sdk.user_interface.address;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.meamobile.printicular_sdk.R;

public class AddressListActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
    }
}
