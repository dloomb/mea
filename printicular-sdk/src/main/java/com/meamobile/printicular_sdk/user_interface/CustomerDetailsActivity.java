package com.meamobile.printicular_sdk.user_interface;

import android.content.Intent;
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
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.Territory;
import com.meamobile.printicular_sdk.user_interface.common.ObservableRelativeLayout;

import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;


public class CustomerDetailsActivity extends CheckoutActivity
{
    enum SearchMode
    {
        MANUAL,
        AUTOMATIC
    }

    enum Field
    {
        NAME,
        EMAIL,
        PHONE,
        LINE_1,
        LINE_2,
        CITY,
        STATE,
        POSTCODE,
        COUNTRY
    }

    private SearchMode mSearchMode = SearchMode.AUTOMATIC;

    private ObservableRelativeLayout
            mRelativeLayoutMain;

    private RelativeLayout
            mRelativeLayoutAutomaticSearch;

    private LinearLayout mLinearLayoutManualSearch;

    private Button mButtonNext;

    private View
            mViewAutomaticSelectionDetail,
            mViewManualSelectionDetail;

    private EditText
            mEditTextName,
            mEditTextEmail,
            mEditTextPhone,
            mEditTextAddressLine1,
            mEditTextAddressLine2,
            mEditTextCity,
            mEditTextState,
            mEditTextPostCode,
            mEditTextCountry;

    private int mOriginalHeight = 0;
    private Address mAddress;
    private PrinticularCartManager mCartManager = PrinticularCartManager.getInstance();
    private PrinticularServiceManager mServiceManager = PrinticularServiceManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        setupEditTextListeners();

        mButtonNext = (Button) findViewById(R.id.buttonNext);
        mButtonNext.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);

        mRelativeLayoutMain = (ObservableRelativeLayout) findViewById(R.id.relativeLayoutMain);
        mRelativeLayoutAutomaticSearch = (RelativeLayout) findViewById(R.id.relativeLayoutAutomaticSearch);
        mLinearLayoutManualSearch = (LinearLayout) findViewById(R.id.linearLayoutManualSearch);

        mViewAutomaticSelectionDetail = findViewById(R.id.viewAutomaticSelectionDetail);
        mViewManualSelectionDetail = findViewById(R.id.viewManualSelectionDetail);

        setupNextButtonHidingListener();
        layoutForCurrentSearchMode();
        loadOrSetupAddress();
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


    protected void loadOrSetupAddress()
    {
        mAddress = mCartManager.getCurrentAddress();
        if (mAddress == null)
        {
            mAddress = new Address();
        }
    }


    ///-----------------------------------------------------------
    /// @name Actions
    ///-----------------------------------------------------------

    public void onNextButtonClick(View v)
    {
        PrinticularServiceManager manager = PrinticularServiceManager.getInstance();

        manager.saveAddress(mAddress)
                .subscribe(x-> {
                    Log.d("", "");
                }, error -> {
                    Log.e("", "");
                });
    }


    ///-----------------------------------------------------------
    /// @name TextWatcher / Address Changing
    ///-----------------------------------------------------------

    protected void setupEditTextListeners()
    {
        (mEditTextName = (EditText) findViewById(R.id.editTextName))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextName, Field.NAME));

        (mEditTextEmail = (EditText) findViewById(R.id.editTextEmail))
            .addTextChangedListener(new DetailsTextWatcher(mEditTextEmail, Field.EMAIL));

        (mEditTextPhone = (EditText) findViewById(R.id.editTextPhone))
            .addTextChangedListener(new DetailsTextWatcher(mEditTextPhone, Field.PHONE));

        (mEditTextAddressLine1 = (EditText) findViewById(R.id.editTextLine1))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextAddressLine1, Field.LINE_1));

        (mEditTextAddressLine2 = (EditText) findViewById(R.id.editTextLine2))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextAddressLine2, Field.LINE_2));

        (mEditTextCity = (EditText) findViewById(R.id.editTextCity))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextCity, Field.CITY));

        (mEditTextState = (EditText) findViewById(R.id.editTextState))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextState, Field.STATE));

        (mEditTextCountry = (EditText) findViewById(R.id.editTextCountry))
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
    }

    class DetailsTextWatcher implements TextWatcher
    {
        private EditText mEditText;
        private Field mField;

        public DetailsTextWatcher(EditText editText, Field field)
        {
            mEditText = editText;
            mField = field;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            String text = mEditText.getText().toString();

            switch (mField)
            {
                case NAME:
                    mAddress.setName(text);
                    break;

                case EMAIL:
                    mAddress.setEmail(text);
                    break;

                case PHONE:
                    mAddress.setPhone(text);
                    break;



                case LINE_1:
                    mAddress.setLine1(text);
                    break;

                case LINE_2:
                    mAddress.setLine2(text);
                    break;

                case CITY:
                    mAddress.setCity(text);
                    break;

                case STATE:
                    mAddress.setState(text);
                    break;

                case POSTCODE:
                    mAddress.setPostcode(text);
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    public Territory territoryForCountry(Locale locale)
    {
        return new Territory(locale);
    }

}
