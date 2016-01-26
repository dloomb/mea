package com.meamobile.printicular_sdk.user_interface.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport;
import com.meamobile.printicular_sdk.user_interface.UserInterfaceUtil;

import java.util.List;

public class GooglePlacesSearchView implements
        ItemClickSupport.OnItemClickListener,
        TextView.OnEditorActionListener,
        TextWatcher,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback
{
    private static final String TAG = "MEA.GooglePlcsSrchView";

    public interface GooglePlacesSearchViewListener
    {
        void onPlaceSelected();
    }

    private long mLastSearchTextChangeTimestamp;
    private List<AutocompletePrediction> mAutocompletePredictions;
    private AutocompletePrediction mSelectedAutocompletePrediction;

    private GooglePlacesSearchViewListener mListener;
    private GoogleApiClient mGoogleApiClient;
    private GooglePlacesPredictionsRecyclerViewAdapter mPlacesRecyclerAdapter;

    private RelativeLayout mRelativeLayoutContent;
    private RecyclerView mRecyclerView;
    private EditText mEditText;

    public GooglePlacesSearchView(View view, Activity activity, GooglePlacesSearchViewListener listener)
    {
        mListener = listener;


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mPlacesRecyclerAdapter = new GooglePlacesPredictionsRecyclerViewAdapter(mRecyclerView);
        mRecyclerView.setAdapter(mPlacesRecyclerAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 1));
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);

        mEditText = (EditText) view.findViewById(R.id.editText);
        mEditText.setImeActionLabel(activity.getString(R.string.search), EditorInfo.IME_ACTION_SEARCH);
        mEditText.setOnEditorActionListener(this);
        mEditText.addTextChangedListener(this);


        mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void startClient()
    {
        mGoogleApiClient.connect();
    }

    public void stopClient()
    {
        mGoogleApiClient.connect();
    }

    public void requestFocus()
    {
        mEditText.requestFocus();
    }



    ///-----------------------------------------------------------
    /// @name RecyclerView
    ///-----------------------------------------------------------

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v)
    {
        mSelectedAutocompletePrediction = mAutocompletePredictions.get(position);
        mEditText.setText(mSelectedAutocompletePrediction.getFullText(null));
        mPlacesRecyclerAdapter.setGooglePlacesPredictions(null);
        UserInterfaceUtil.hideKeyboard(mEditText);

        getLatLngFromUserSelection();
    }



    ///-----------------------------------------------------------
    /// @name Search EditText
    ///-----------------------------------------------------------


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s) {}



    ///-----------------------------------------------------------
    /// @name GoogleApiClient
    ///-----------------------------------------------------------

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

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


    protected void runGooglePlacesAutoComplete()
    {
        if (mSelectedAutocompletePrediction != null)
        {
            //Dont run a search if the user already picked an address
            return;
        }

        String text = mEditText.getText().toString();

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
