package com.meamobile.printicular_sdk.user_interface;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;

import com.meamobile.printicular_sdk.R;

public class CheckoutActivity extends ActionBarActivity
{
    private boolean mHasLaidOutActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.activity_start_slide_in, R.animator.activity_start_slide_out);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (!mHasLaidOutActionbar)
        {
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
