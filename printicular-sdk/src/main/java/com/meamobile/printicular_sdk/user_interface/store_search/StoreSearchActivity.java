package com.meamobile.printicular_sdk.user_interface.store_search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;

public class StoreSearchActivity extends CheckoutActivity implements SearchView.OnQueryTextListener
{
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_search_menu, menu);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            mSearchMenuItem = menu.findItem(R.id.search);
            mSearchView = (SearchView) mSearchMenuItem.getActionView();
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setQueryHint("Enter City & State or Zip");
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        mSearchView.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            mSearchMenuItem.expandActionView();
        }
        return super.onPrepareOptionsMenu(menu);
    }


    ///-----------------------------------------------------------
    /// @name SearchView Text Change Listener
    ///-----------------------------------------------------------

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, newText, null, null);
        result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>()
        {
            @Override
            public void onResult(AutocompletePredictionBuffer autocompletePredictions)
            {
                Log.d("", "");
            }
        });
        return false;
    }

}
