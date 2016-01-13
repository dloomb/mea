package com.meamobile.printicular_sdk.user_interface.store_search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport.OnItemClickListener;
import com.meamobile.printicular_sdk.user_interface.UserInterfaceUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoreSearchActivity
        extends CheckoutActivity
        implements OnEditorActionListener, TextWatcher, ResultCallback, OnConnectionFailedListener, ConnectionCallbacks, OnItemClickListener
{
    private static final String TAG = "MEA.CheckoutActivity";

    private GoogleApiClient mGoogleApiClient;
    private StoreSearchResultsRecyclerViewAdapter mResultsRecyclerAdapter;

    private Long mLastSearchTextChangeTimestamp;
    private List<AutocompletePrediction> mAutocompletePredictions;
    private AutocompletePrediction mSelectedAutocompletePrediction;

    private RecyclerView mRecyclerView;
    private EditText mEditTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLastSearchTextChangeTimestamp = new Date().getTime();

        mResultsRecyclerAdapter = new StoreSearchResultsRecyclerViewAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mResultsRecyclerAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);

        mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);
        mEditTextSearch.setImeActionLabel(getString(R.string.search), EditorInfo.IME_ACTION_SEARCH);
        mEditTextSearch.setOnEditorActionListener(this);
        mEditTextSearch.addTextChangedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_search_menu, menu);
        return true;
    }


    ///-----------------------------------------------------------
    /// @name RecyclerView
    ///-----------------------------------------------------------

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v)
    {
        mSelectedAutocompletePrediction = mAutocompletePredictions.get(position);
        mEditTextSearch.setText(mSelectedAutocompletePrediction.getFullText(null));
        mResultsRecyclerAdapter.setGooglePlacesPredictions(null);
        UserInterfaceUtil.HideKeyboard(this);
    }



    ///-----------------------------------------------------------
    /// @name SearchViewText
    ///-----------------------------------------------------------

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (actionId == EditorInfo.IME_ACTION_SEARCH)
        {
            runGooglePlacesSearch();
            UserInterfaceUtil.HideKeyboard(this);
            return true;
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        mLastSearchTextChangeTimestamp = new Date().getTime();
        final long timestamp = mLastSearchTextChangeTimestamp;

        if (s.length() > 3)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (timestamp == mLastSearchTextChangeTimestamp)
                    {
                        runGooglePlacesSearch();
                    }
                }
            }, 200);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}



    ///-----------------------------------------------------------
    /// @name GooglePlaces
    ///-----------------------------------------------------------

    @Override
    public void onResult(Result result)
    {
        if (result.getStatus().isSuccess())
        {
            AutocompletePredictionBuffer buffer = (AutocompletePredictionBuffer) result;
            mAutocompletePredictions = DataBufferUtils.freezeAndClose(buffer);
            mResultsRecyclerAdapter.setGooglePlacesPredictions(mAutocompletePredictions);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    protected void runGooglePlacesSearch()
    {
        if (mSelectedAutocompletePrediction != null)
        {
            //Dont run a search if the user already picked an address
            return;
        }

        String text = mEditTextSearch.getText().toString();

        if (mGoogleApiClient.isConnected())
        {
            Log.d(TAG, "Google Api Client will make query: " + text);
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, text, null, null);
            results.setResultCallback(this);
        }

        Log.e(TAG, "Google Api Client is not connected");
    }
}
