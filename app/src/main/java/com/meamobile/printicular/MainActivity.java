package com.meamobile.printicular;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.ExplorerFragment;
import com.meamobile.photokit.user_interface.ExplorerFragment.ExplorerFragmentNavigator;
import com.meamobile.printicular_sdk.PrinticularServiceManager;
import com.meamobile.printicular_sdk.PrinticularServiceManager.PrinticularEnvironment;
import com.meamobile.printicular_sdk.models.AccessToken;

public class MainActivity extends ActionBarActivity implements ExplorerFragmentNavigator {

    private long lastBackTap = 0;
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup UserDefaults Singleton
        UserDefaults.getInstance().setContext(this);

        new AccessToken("", null, null).deleteToken(this);

        PrinticularServiceManager.getInstance()
                .initialize(this, PrinticularEnvironment.STAGING);

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.getBackground().setColorFilter(0xFFF20017, PorterDuff.Mode.MULTIPLY);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        pushRootExplorer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            popExplorerFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    ///-----------------------------------------------------------
    /// @name Hardware Button Input
    ///-----------------------------------------------------------

    @Override
    public void onBackPressed()
    {
        Log.d("!!", "Back");
        popExplorerFragment();
    }



    ///-----------------------------------------------------------
    /// @name Explorer Fragment Navigation
    ///-----------------------------------------------------------

    protected Fragment getCurrentFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(tag);
    }

    protected void popExplorerFragment()
    {
        Fragment fragment = getCurrentFragment();

        if (fragment instanceof ExplorerFragment)
        {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.executePendingTransactions();

            ExplorerFragment frag = (ExplorerFragment) getCurrentFragment();
            if (frag != null)
            {
                frag.onFragmentWillAppear();
            }
            else
            {
                setTitle("Select a Source");
                setDisplaysBackButton(false);
            }
        }
        else
        {
            long now = System.currentTimeMillis();
            long diff = now - lastBackTap;

            if (diff < 2000)
            {
                moveTaskToBack(true);
            }
            else
            {
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
                lastBackTap = now;
            }
        }
    }

    protected void pushRootExplorer()
    {
        ExplorerFragment fragment = ExplorerFragment.newInstance(Collection.RootCollection());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        getSupportFragmentManager().executePendingTransactions();
        fragment.onFragmentWillAppear();
    }


    @Override
    public void pushExplorerWithCollection(Collection collection) {
        ExplorerFragment fragment = ExplorerFragment.newInstance(collection);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.fragmentContainer, fragment, collection.collectionIdentifier())
                .addToBackStack(collection.collectionIdentifier())
                .commit();

        getSupportFragmentManager().executePendingTransactions();

        fragment.onFragmentWillAppear();
    }

    @Override
    public void setNavigationTitle(String title) {
        setTitle(title);
    }

    @Override
    public void setDisplaysBackButton(Boolean shouldDisplay) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplay);
    }
}
