package com.meamobile.printicular_sdk.user_interface.address;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Territory;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport;
import com.meamobile.printicular_sdk.user_interface.common.GooglePlacesPredictionsRecyclerViewAdapter;
import com.meamobile.printicular_sdk.user_interface.common.GooglePlacesSearchView;
import com.meamobile.printicular_sdk.user_interface.manage_order.ManageOrderActivity;
import com.meamobile.printicular_sdk.user_interface.store_search.StoreSearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class AddressEntryActivity extends CheckoutActivity implements GooglePlacesSearchView.GooglePlacesSearchViewListener
{
    private static final String TAG = "MEA.AddressEntryAct";

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

    private RelativeLayout
            mRelativeLayoutMain,
            mRelativeLayoutAutomaticSearch,
            mRelativeLayoutNextButton;

    private LinearLayout
            mLinearLayoutSearchTabs,
            mLinearLayoutManualSearch;

    private View
            mViewAutomaticSelectionDetail,
            mViewManualSelectionDetail;

    private GooglePlacesSearchView mSearchView;

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
    private boolean mNextButtonEnabled = false;
    private PrinticularCartManager mCartManager = PrinticularCartManager.getInstance();
    private PrinticularServiceManager mServiceManager = PrinticularServiceManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_entry);

        setupEditTextListeners();

        mRelativeLayoutNextButton = (RelativeLayout) findViewById(R.id.relativeLayoutNextButton);

        mRelativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        mLinearLayoutSearchTabs = (LinearLayout) findViewById(R.id.linearLayoutSearchTabs);
        mRelativeLayoutAutomaticSearch = (RelativeLayout) findViewById(R.id.relativeLayoutAutomaticSearch);
        mLinearLayoutManualSearch = (LinearLayout) findViewById(R.id.linearLayoutManualSearch);

        mSearchView = new GooglePlacesSearchView(findViewById(R.id.includeSearchView), this);

        mViewAutomaticSelectionDetail = findViewById(R.id.viewAutomaticSelectionDetail);
        mViewManualSelectionDetail = findViewById(R.id.viewManualSelectionDetail);

        if(!getIntent().getBooleanExtra(EXTRA_DONE_BUTTON_ENABLED, false)) {
            mRelativeLayoutNextButton.setVisibility(View.GONE);
        }

        loadOrSetupAddress();
        layoutForCurrentSearchMode();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mSearchView.startClient();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        mSearchView.stopClient();
    }



    ///-----------------------------------------------------------
    /// @name GooglePlacesSearchView Listener
    ///-----------------------------------------------------------


    @Override
    public void onResultSetChanged()
    {

    }

    @Override
    public void onPlaceSelected(android.location.Address place)
    {
        mSearchMode = SearchMode.MANUAL;
        mEditTextAddressLine1.setText(place.getAddressLine(0));
        mEditTextAddressLine2.setText(place.getAddressLine(1));
        mEditTextCity.setText(place.getLocality());
        mEditTextState.setText(place.getAdminArea());
        mEditTextPostCode.setText(place.getPostalCode());
        mEditTextCountry.setText(place.getCountryName());
        layoutForCurrentSearchMode();
    }

    @Override
    public Activity getParentActvity()
    {
        return this;
    }






    ///-----------------------------------------------------------
    /// @name Actions
    ///-----------------------------------------------------------

    public void onDoneButtonClicked(View v)
    {
        if (!validateEnteredAddress()) { return; }

        mServiceManager.saveAddress(mAddress)
                .subscribe(x -> {
                    mCartManager.setCurrentAddress(x);
                    mCartManager.saveAddress(x);

                    Intent i = new Intent(AddressEntryActivity.this, ManageOrderActivity.class);

                    if (mCartManager.getCurrentPrintService().getFulFillmentType() == PrintService.FulfillmentType.PICKUP
                            && mCartManager.getCurrentStore() == null)
                    {
                        i.setClass(AddressEntryActivity.this, StoreSearchActivity.class);
                        i.putExtra(StoreSearchActivity.EXTRA_STORE_SEARCH_PUSHTO_MANAGE_ORDER, true);
                    }

                    startActivity(i);

                }, error -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Address Error")
                            .setMessage("Sorry, but we were unable to save your address at this time. Please check you internet connection and try again.")
                            .show();
                });
    }

    public void onSearchAddressClicked(View v)
    {
        mSearchMode = SearchMode.AUTOMATIC;
        layoutForCurrentSearchMode();
        mSearchView.requestFocus();
    }

    public void onTypeAddressClicked(View v)
    {
        mSearchMode = SearchMode.MANUAL;
        layoutForCurrentSearchMode();
        mEditTextAddressLine1.requestFocus();
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





    ///-----------------------------------------------------------
    /// @name Validation
    ///-----------------------------------------------------------

    protected boolean validateEnteredAddress() {
        PrintService.FulfillmentType fulfillmentType = mCartManager.getCurrentPrintService().getFulFillmentType();

        boolean valid = true;

        List<String> reasons = new ArrayList<>();

        switch (fulfillmentType) {

            case DELIVERY:
                //Validate Delivery Specific Fields

            case PICKUP:

                if (mAddress.getName() == null || mAddress.getName().length() == 0) {
                    valid = false;
                    reasons.add("Name Empty");
                }

                if (mAddress.getEmail() == null || mAddress.getEmail().length() == 0) {
                    valid = false;
                    reasons.add("Email Empty");
                }
                else if (!Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(mAddress.getEmail()).matches()) {
                    valid = false;
                    reasons.add("Email not valid");
                }

                if (mAddress.getPhone() == null || mAddress.getPhone().length() == 0) {
                    valid = false;
                    reasons.add("Phone Empty");
                }else if (!validatePhoneNumber(mAddress.getPhone())) {
                    valid = false;
                    reasons.add("Phone not valid");
                }

        }

        Log.d(TAG, TextUtils.join(",", reasons));
        if (reasons.size() != 0) {
            Toast.makeText(this, TextUtils.join(",", reasons), Toast.LENGTH_LONG).show();
        }

        return valid;
    }


    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }
}
