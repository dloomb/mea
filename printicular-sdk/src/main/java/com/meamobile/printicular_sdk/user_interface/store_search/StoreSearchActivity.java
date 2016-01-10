package com.meamobile.printicular_sdk.user_interface.store_search;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;

public class StoreSearchActivity extends CheckoutActivity implements SearchView.OnQueryTextListener
{
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_search_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            mSearchView = (SearchView) mSearchMenuItem.getActionView();
            mSearchView.setOnQueryTextListener(this);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
        return false;
    }
}
