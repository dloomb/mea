package com.meamobile.printicular_sdk.user_interface.address;

import android.app.AlertDialog;
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
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Territory;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.common.ObservableRelativeLayout;

import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;


public class AddressEntryActivity extends CheckoutActivity
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

    private LinearLayout
            mLinearLayoutSearchTabs,
            mLinearLayoutManualSearch;

    private Button mButtonNext;

    private View
            mViewAutomaticSelectionDetail,
            mViewManualSelectionDetail;

    private EditText
            mEditTextSearch,
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
        setContentView(R.layout.activity_address_entry);

        setupEditTextListeners();

        mButtonNext = (Button) findViewById(R.id.buttonNext);
        mButtonNext.getBackground().setColorFilter(getResources().getColor(R.color.button_red), PorterDuff.Mode.MULTIPLY);

        mRelativeLayoutMain = (ObservableRelativeLayout) findViewById(R.id.relativeLayoutMain);
        mLinearLayoutSearchTabs = (LinearLayout) findViewById(R.id.linearLayoutSearchTabs);
        mRelativeLayoutAutomaticSearch = (RelativeLayout) findViewById(R.id.relativeLayoutAutomaticSearch);
        mLinearLayoutManualSearch = (LinearLayout) findViewById(R.id.linearLayoutManualSearch);

        mViewAutomaticSelectionDetail = findViewById(R.id.viewAutomaticSelectionDetail);
        mViewManualSelectionDetail = findViewById(R.id.viewManualSelectionDetail);

        setupNextButtonHidingListener();
        loadOrSetupAddress();
        layoutForCurrentSearchMode();
    }


    public void onSearchAddressClicked(View v)
    {
        mSearchMode = SearchMode.AUTOMATIC;
        layoutForCurrentSearchMode();
        mEditTextSearch.requestFocus();
    }

    public void onTypeAddressClicked(View v)
    {
        mSearchMode = SearchMode.MANUAL;
        layoutForCurrentSearchMode();
        mEditTextAddressLine1.requestFocus();
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
        if (mCartManager.getCurrentPrintService().getFulFillmentType() == PrintService.FulfillmentType.PICKUP)
        {
            mLinearLayoutSearchTabs.setVisibility(View.GONE);
            mLinearLayoutManualSearch.setVisibility(View.GONE);
            mRelativeLayoutAutomaticSearch.setVisibility(View.GONE);
        }
        else
        {
            mLinearLayoutManualSearch.setVisibility(View.VISIBLE);

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
    }


    protected void loadOrSetupAddress()
    {
        mAddress = mCartManager.getCurrentAddress();
        if (mAddress == null)
        {
            mAddress = new Address();
        }
        else {
            mSearchMode = SearchMode.MANUAL;

            mEditTextName.setText(mAddress.getName());
            mEditTextEmail.setText(mAddress.getEmail());
            mEditTextPhone.setText(mAddress.getPhone());
            mEditTextAddressLine1.setText(mAddress.getLine1());
            mEditTextAddressLine2.setText(mAddress.getLine2());
            mEditTextCity.setText(mAddress.getCity());
            mEditTextState.setText(mAddress.getState());
            mEditTextPostCode.setText(mAddress.getPostcode());
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
                    mCartManager.setCurrentAddress(x);
                    finish();
                }, error -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Address Error")
                            .setMessage("Sorry, but we were unable to save your address at this time. Please check you internet connection and try again.")
                            .show();
                });
    }


    ///-----------------------------------------------------------
    /// @name TextWatcher / Address Changing
    ///-----------------------------------------------------------

    protected void setupEditTextListeners()
    {
        (mEditTextSearch = (EditText) findViewById(R.id.editTextSearch))
                .addTextChangedListener(new SearchTextWatcher());

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

        (mEditTextPostCode = (EditText) findViewById(R.id.editTextPostcode))
                .addTextChangedListener(new DetailsTextWatcher(mEditTextPostCode, Field.STATE));

        (mEditTextCountry = (EditText) findViewById(R.id.editTextCountry))
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
    }


    class SearchTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
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
