package com.meamobile.printicular;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.meamobile.printicular.settings.SettingsRecyclerViewAdapter;

public class SettingsActivity extends ActionBarActivity
{
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.animator.activity_start_slide_in, R.animator.activity_start_slide_out);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new SettingsRecyclerViewAdapter(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            goBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goBack()
    {
        finish();
        overridePendingTransition(R.animator.activity_end_slide_in, R.animator.activity_end_slide_out);
    }

    ///-----------------------------------------------------------
    /// @name Hardware Button Input
    ///-----------------------------------------------------------

    @Override
    public void onBackPressed()
    {
        goBack();
    }



}
