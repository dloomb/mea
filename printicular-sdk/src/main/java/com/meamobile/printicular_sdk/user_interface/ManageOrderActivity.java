package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.meamobile.printicular_sdk.R;

public class ManageOrderActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        ImageButton postEdit = (ImageButton) findViewById(R.id.buttonPostalEdit);
        postEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(ManageOrderActivity.this, CustomerDetailsActivity.class);
                startActivity(i);
            }
        });

        Button nextButton = (Button) findViewById(R.id.buttonNext);
        nextButton.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);
    }


    protected void onPostalDetailsPressed(View v)
    {

    }


}
