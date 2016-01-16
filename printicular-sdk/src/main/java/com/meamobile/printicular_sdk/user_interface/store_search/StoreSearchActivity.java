package com.meamobile.printicular_sdk.user_interface.store_search;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.model.LatLng;
import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Store;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport.OnItemClickListener;
import com.meamobile.printicular_sdk.user_interface.UserInterfaceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StoreSearchActivity
        extends CheckoutActivity
        implements OnEditorActionListener, TextWatcher, ResultCallback, OnConnectionFailedListener, ConnectionCallbacks, OnItemClickListener
{
    private static final String TAG = "MEA.CheckoutActivity";

    private GoogleApiClient mGoogleApiClient;
    private GooglePlacesPredictionsRecyclerViewAdapter mPlacesRecyclerAdapter;

    private Long mLastSearchTextChangeTimestamp;
    private List<AutocompletePrediction> mAutocompletePredictions;
    private AutocompletePrediction mSelectedAutocompletePrediction;

    private StoreResultsRecyclerViewAdapter mStoreRecyclerAdapter;
    private List<Store> mStoreResults;

    private PrinticularServiceManager mServiceManager = PrinticularServiceManager.getInstance();
    private PrinticularCartManager mCartManager = PrinticularCartManager.getInstance();

    private RecyclerView
            mPlacesRecyclerView,
            mStoresRecyclerView;
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

        mPlacesRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewPlaces);
        mPlacesRecyclerAdapter = new GooglePlacesPredictionsRecyclerViewAdapter(mPlacesRecyclerView);
        mPlacesRecyclerView.setAdapter(mPlacesRecyclerAdapter);
        mPlacesRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        ItemClickSupport.addTo(mPlacesRecyclerView).setOnItemClickListener(this);

        mStoreRecyclerAdapter = new StoreResultsRecyclerViewAdapter();

        mStoresRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewStores);
        mStoresRecyclerView.setAdapter(mStoreRecyclerAdapter);
        mStoresRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        ItemClickSupport.addTo(mStoresRecyclerView).setOnItemClickListener(this);

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
        if (recyclerView == mPlacesRecyclerView)
        {
            mSelectedAutocompletePrediction = mAutocompletePredictions.get(position);
            mEditTextSearch.setText(mSelectedAutocompletePrediction.getFullText(null));
            mPlacesRecyclerAdapter.setGooglePlacesPredictions(null);
            UserInterfaceUtil.HideKeyboard(this);

            getLatLngFromUserSelection();
        }
        else
        {
            Store s = mStoreResults.get(position);
            mCartManager.setCurrentStore(s);
            finish();
        }
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
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {@Override public void run()
            {
                if (timestamp == mLastSearchTextChangeTimestamp)
                {
                    runGooglePlacesSearch();
                }
            }}, 200);
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
            mPlacesRecyclerAdapter.setGooglePlacesPredictions(mAutocompletePredictions);
            buffer.release();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

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
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter
                    .Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                    .build();

            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, text, null, autocompleteFilter);
            results.setResultCallback(this);
            return;
        }

        Log.e(TAG, "Google Api Client is not connected");
    }



    ///-----------------------------------------------------------
    /// @name Printicular Server
    ///-----------------------------------------------------------

    protected void runPrinticularStoreSearch(LatLng latLng)
    {
        PrintService currentPrintService = mCartManager.getCurrentPrintService();
        currentPrintService = mServiceManager.getPrintServiceWithId(3);

        mServiceManager.searchForStores(currentPrintService, latLng, null, new PrinticularServiceManager.StoreSearchCallback() {
            @Override
            public void success(Map<Long, Store> stores) {

                mStoreResults = new ArrayList<Store>(stores.values());
                Collections.sort(mStoreResults, Store.DistanceSortComparator());

                new Handler(Looper.getMainLooper()).post(new Runnable() {@Override public void run()
                {
                    mStoreRecyclerAdapter.setStores(new ArrayList<Store>(mStoreResults));
                }});
            }

            @Override
            public void error(String error) {

            }
        });
    }


    protected void getLatLngFromUserSelection()
    {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, mSelectedAutocompletePrediction.getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>()
                {
                    @Override
                    public void onResult(PlaceBuffer places)
                    {
                        if (places.getStatus().isSuccess() && places.getCount() > 0)
                        {
                            final Place myPlace = places.get(0);
                            Log.i(TAG, "Place found: " + myPlace.getName());
                            runPrinticularStoreSearch(myPlace.getLatLng());
                        }
                        else
                        {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                    }
                });
    }



}
