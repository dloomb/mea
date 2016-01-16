package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.models.PrintService.FulfillmentType;
import com.meamobile.printicular_sdk.core.models.Store;
import com.meamobile.printicular_sdk.user_interface.common.StoreDetailsViewHolder;
import com.meamobile.printicular_sdk.user_interface.store_search.StoreSearchActivity;

public class ManageOrderActivity extends CheckoutActivity
{
    private FulfillmentType mFulfillmentType = FulfillmentType.PICKUP;
    private PrinticularCartManager mCartManager;

    //UI
    private StoreDetailsViewHolder mStoreDetailsViewHolder;
    private RelativeLayout mRelativeLayoutPostalDetails;
    private LinearLayout mLinearLayoutPaymentDetails;
    private TextView mTextViewQuantity, mTextViewShipping, mTextViewTotal;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        mCartManager = PrinticularCartManager.getInstance();

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

        loadComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUserInterface();
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
            setupUserInterface();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void loadComponents()
    {
        mTextViewQuantity = (TextView) findViewById(R.id.textViewQuantity);
        mTextViewShipping = (TextView) findViewById(R.id.textViewShipping);
        mTextViewTotal = (TextView) findViewById(R.id.textViewTotal);

        mStoreDetailsViewHolder = new StoreDetailsViewHolder(findViewById(R.id.relativeLayoutOuter));
        mStoreDetailsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStoreDetialsPressed(v);
            }
        });

        mRelativeLayoutPostalDetails = (RelativeLayout) findViewById(R.id.postalDetails);
        mLinearLayoutPaymentDetails = (LinearLayout) findViewById(R.id.paymentDetails);
    }


    protected void onPostalDetailsPressed(View v)
    {

    }

    protected void onStoreDetialsPressed(View v)
    {
        Intent i = new Intent(ManageOrderActivity.this, StoreSearchActivity.class);
        startActivity(i);
    }


    private void setupUserInterface()
    {
        if (mFulfillmentType == FulfillmentType.PICKUP)
        {
            mStoreDetailsViewHolder.itemView.setVisibility(View.VISIBLE);
            mRelativeLayoutPostalDetails.setVisibility(View.GONE);
            mLinearLayoutPaymentDetails.setVisibility(View.GONE);
        }
        else
        {
            mStoreDetailsViewHolder.itemView.setVisibility(View.GONE);
            mRelativeLayoutPostalDetails.setVisibility(View.VISIBLE);
            mLinearLayoutPaymentDetails.setVisibility(View.VISIBLE);
        }

        displayCurrentSelectedStore();
    }

    private void displayCurrentSelectedStore()
    {
        Store store = mCartManager.getCurrentStore();
        mStoreDetailsViewHolder.setStore(store);
    }

}
