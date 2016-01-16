package com.meamobile.printicular_sdk.user_interface;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meamobile.printicular_sdk.R;


public class CustomerDetailsActivity extends CheckoutActivity
{
    enum SearchMode
    {
        MANUAL,
        AUTOMATIC
    }

    private SearchMode mSearchMode;

    private RelativeLayout
            mRelativeLayoutAutomaticSearch;

    private LinearLayout mLinearLayoutManualSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        Button nextButton = (Button) findViewById(R.id.buttonNext);
        nextButton.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);

        mRelativeLayoutAutomaticSearch = (RelativeLayout) findViewById(R.id.relativeLayoutAutomaticSearch);
        mLinearLayoutManualSearch = (LinearLayout) findViewById(R.id.linearLayoutManualSearch);
    }


    public void onSearchAddressClicked(View v)
    {
        mSearchMode = SearchMode.AUTOMATIC;
        layoutForCurrentSearchMode();
    }

    public void onTypeAddressClicked(View v)
    {
        mSearchMode = SearchMode.MANUAL;
        layoutForCurrentSearchMode();
    }

    protected void layoutForCurrentSearchMode()
    {
        switch (mSearchMode)
        {
            case AUTOMATIC:
                mLinearLayoutManualSearch.setVisibility(View.GONE);
                mRelativeLayoutAutomaticSearch.setVisibility(View.VISIBLE);
                break;

            case MANUAL:
                mLinearLayoutManualSearch.setVisibility(View.VISIBLE);
                mRelativeLayoutAutomaticSearch.setVisibility(View.GONE);
                break;
        }
    }


}
