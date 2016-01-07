package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.PrintService.FulfillmentType;

public class ManageOrderActivity extends BaseActivity
{
    private FulfillmentType mFulfillmentType = FulfillmentType.PICKUP;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            mFulfillmentType = (mFulfillmentType == FulfillmentType.PICKUP ? FulfillmentType.DELIVERY : FulfillmentType.PICKUP);
        }

        return super.onOptionsItemSelected(item);
    }
}
