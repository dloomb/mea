package com.meamobile.printicular_sdk.user_interface;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;

import com.meamobile.printicular_sdk.R;


public class CustomerDetailsActivity extends CheckoutActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        Button nextButton = (Button) findViewById(R.id.buttonNext);
        nextButton.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);
    }
}
