package com.meamobile.printicular_sdk.user_interface.store_search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.location.places.Place;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Store;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport;
import com.meamobile.printicular_sdk.user_interface.ItemClickSupport.OnItemClickListener;
import com.meamobile.printicular_sdk.user_interface.common.GooglePlacesSearchView;
import com.meamobile.printicular_sdk.user_interface.manage_order.ManageOrderActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoreSearchActivity
        extends CheckoutActivity
        implements OnItemClickListener, GooglePlacesSearchView.GooglePlacesSearchViewListener
{
    public static final String EXTRA_STORE_SEARCH_PUSHTO_MANAGE_ORDER = "com.meamobile.printicular_sdk.user_interface.store_search.pusto.mange_order_activity";

    private static final String TAG = "MEA.CheckoutActivity";

    private StoreResultsRecyclerViewAdapter mStoreRecyclerAdapter;
    private List<Store> mStoreResults;

    private android.location.Address mSelectedAutocompletePlace;

    private PrinticularServiceManager mServiceManager = PrinticularServiceManager.getInstance();
    private PrinticularCartManager mCartManager = PrinticularCartManager.getInstance();

    private RecyclerView
            mStoresRecyclerView;

    private GooglePlacesSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        mSearchView = new GooglePlacesSearchView(findViewById(R.id.includeSearchView), this);

        mStoreRecyclerAdapter = new StoreResultsRecyclerViewAdapter();

        mStoresRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewStores);
        mStoresRecyclerView.setAdapter(mStoreRecyclerAdapter);
        mStoresRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        ItemClickSupport.addTo(mStoresRecyclerView).setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSearchView.startClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSearchView.stopClient();
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
        if (recyclerView == mStoresRecyclerView)
        {
            Store s = mStoreResults.get(position);
            mCartManager.setCurrentStore(s);
            mCartManager.saveStore(s);

            if (getIntent().getBooleanExtra(EXTRA_STORE_SEARCH_PUSHTO_MANAGE_ORDER, false)) {
                Intent i = new Intent(StoreSearchActivity.this, ManageOrderActivity.class);
                startActivity(i);
            } else {
                finish();
            }
        }
    }



    ///-----------------------------------------------------------
    /// @name Printicular Server
    ///-----------------------------------------------------------

    protected void runPrinticularStoreSearch(double lat, double lng)
    {
        PrintService currentPrintService = mCartManager.getCurrentPrintService();
        currentPrintService = mServiceManager.getPrintServiceWithId(3);

        mServiceManager.searchForStores(currentPrintService, lat, lng, null)
                .subscribe(
                        x -> {
                            mStoreResults = new ArrayList<>(x.values());
                            Collections.sort(mStoreResults, Store.DistanceSortComparator());
                            mStoreRecyclerAdapter.setStores(new ArrayList<Store>(mStoreResults));
                        },
                        error -> {
                            //TODO Inform the user that the search failed
                        });


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
        mSelectedAutocompletePlace = place;
        runPrinticularStoreSearch(place.getLatitude(), place.getLongitude());
    }

    @Override
    public Activity getParentActvity()
    {
        return this;
    }
}
