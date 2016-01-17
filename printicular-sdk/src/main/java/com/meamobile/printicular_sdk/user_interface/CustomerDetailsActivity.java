package com.meamobile.printicular_sdk.user_interface;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.user_interface.common.ObservableRelativeLayout;

import rx.android.schedulers.AndroidSchedulers;


public class CustomerDetailsActivity extends CheckoutActivity implements TextWatcher
{
    enum SearchMode
    {
        MANUAL,
        AUTOMATIC
    }

    private SearchMode mSearchMode = SearchMode.AUTOMATIC;

    private ObservableRelativeLayout
            mRelativeLayoutMain;

    private RelativeLayout
            mRelativeLayoutAutomaticSearch;

    private LinearLayout mLinearLayoutManualSearch;

    private Button mButtonNext;

    private View mViewAutomaticSelectionDetail,
            mViewManualSelectionDetail;

    private EditText mEditTextName, mEditTextEmail, mEditTextPhone, mEditTextAddressLine1, mEditTextAddressLine2, mEditTextCity, mEditTextState, mEditText;

    private int mOriginalHeight = 0;
    private Address mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextName);
        mEditTextPhone = (EditText) findViewById(R.id.editTextName);


        mButtonNext = (Button) findViewById(R.id.buttonNext);
        mButtonNext.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);

        mRelativeLayoutMain = (ObservableRelativeLayout) findViewById(R.id.relativeLayoutMain);
        mRelativeLayoutAutomaticSearch = (RelativeLayout) findViewById(R.id.relativeLayoutAutomaticSearch);
        mLinearLayoutManualSearch = (LinearLayout) findViewById(R.id.linearLayoutManualSearch);

        mViewAutomaticSelectionDetail = findViewById(R.id.viewAutomaticSelectionDetail);
        mViewManualSelectionDetail = findViewById(R.id.viewManualSelectionDetail);

        setupNextButtonHidingListener();
        layoutForCurrentSearchMode();
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

    protected void setupNextButtonHidingListener()
    {
        mRelativeLayoutMain.getObservable()
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {

                    if (mOriginalHeight == 0) {
                        mOriginalHeight = x;
                    }

                    if (mOriginalHeight == x) {
                        mButtonNext.setVisibility(View.VISIBLE);
                    } else {
                        mButtonNext.setVisibility(View.GONE);
                    }

                    Log.d("Height", "" + x);
                });
    }


    protected void layoutForCurrentSearchMode()
    {
        switch (mSearchMode)
        {
            case AUTOMATIC:
                mLinearLayoutManualSearch.setVisibility(View.GONE);
                mRelativeLayoutAutomaticSearch.setVisibility(View.VISIBLE);
                mViewAutomaticSelectionDetail.setVisibility(View.VISIBLE);
                mViewManualSelectionDetail.setVisibility(View.INVISIBLE);
                break;

            case MANUAL:
                mLinearLayoutManualSearch.setVisibility(View.VISIBLE);
                mRelativeLayoutAutomaticSearch.setVisibility(View.GONE);
                mViewAutomaticSelectionDetail.setVisibility(View.INVISIBLE);
                mViewManualSelectionDetail.setVisibility(View.VISIBLE);
                break;
        }
    }



    ///-----------------------------------------------------------
    /// @name TextWatcher
    ///-----------------------------------------------------------


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


}
