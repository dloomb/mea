package com.meamobile.printicular;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;
import com.meamobile.photokit.user_interface.ExplorerFragment;
import com.meamobile.photokit.user_interface.ExplorerFragment.ExplorerFragmentDelegate;
import com.meamobile.printicular.cart.CartFragment;
import com.meamobile.printicular.cart.PhotoKitCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager.PrinticularEnvironment;
import com.meamobile.printicular_sdk.user_interface.ManageOrderActivity;


public class MainActivity extends ActionBarActivity implements ExplorerFragmentDelegate
{
    public static String TAG = "MEA.ActivityMain";
    public static String AUTO_SAVED_COUNTRY = "com.meamobile.printicular.country_selection.auto";
    public static String USER_SAVED_COUNTRY = "com.meamobile.printicular.country_selection.user";

    private long lastBackTap = 0;
    private CallbackManager mFacebookCallbackManager;
    private String mCountryCode;
    private PhotoKitCartManager mCart;
    private CartFragment mCartFragment;

    //UI
    private Button mBtnNext, mBtnDeliver, mBtnPickup;
    private LinearLayout mBtnContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup UserDefaults Singleton
        UserDefaults.getInstance().setContext(this);

        PrinticularServiceManager.getInstance()
                .initialize(this, PrinticularEnvironment.STAGING);

        mCart = PhotoKitCartManager.getInstance();
        mCartFragment = (CartFragment) getSupportFragmentManager().findFragmentById(R.id.cartFragment);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        determineLocation();

        pushRootExplorer();
    }

    private void setupUserInterface()
    {
        Log.d(TAG, "Updating UI for " + mCountryCode);

        int red = getResources().getColor(R.color.printicular_wag_red);
        int blue = getResources().getColor(R.color.printicular_hack_blue);
        int grey = getResources().getColor(R.color.printicular_primary_lightgrey);
        int orange = getResources().getColor(R.color.printicular_kodak_orange);

        mBtnNext = (Button) findViewById(R.id.nextButton);
        mBtnPickup = (Button) findViewById(R.id.pickupButton);
        mBtnDeliver = (Button) findViewById(R.id.deliveryButton);
        mBtnContainer = (LinearLayout) findViewById(R.id.buttonContainer);

        mBtnNext.setVisibility(View.INVISIBLE);
        mBtnContainer.setVisibility(View.INVISIBLE);

        switch (mCountryCode)
        {
            case "NZ":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(blue, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Warehouse Stationery");
                mBtnDeliver.getBackground().setColorFilter(grey, PorterDuff.Mode.MULTIPLY);
                break;

            case "US":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(red, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Walgreens");
                mBtnDeliver.getBackground().setColorFilter(blue, PorterDuff.Mode.MULTIPLY);
                break;

            case "DE":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(orange, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Kodak");
                mBtnDeliver.getBackground().setColorFilter(grey, PorterDuff.Mode.MULTIPLY);
                break;

            default:

                break;
        }
    }

    private void determineLocation()
    {
        //Location has already been set
        mCountryCode = UserDefaults.getInstance().stringForKey(USER_SAVED_COUNTRY);
        if (mCountryCode != null)
        {
            setupUserInterface();
            return;
        }

        //If we have a saved one, start with that
        mCountryCode = UserDefaults.getInstance().stringForKey(AUTO_SAVED_COUNTRY);
        if (mCountryCode != null)
        {
            setupUserInterface();
            return;
        }

        //No Location Info, Get new (Should only execute on first launch)
        LocationUtil.getLocation(LocationUtil.Precision.FAST, this,
                new LocationUtil.LocationUtilCallback()
                {
                    @Override
                    public void completion(String error, Address address)
                    {
                        String code = getApplicationContext().getResources().getConfiguration().locale.getISO3Country();

                        if (address != null)
                        {
                            code = address.getCountryCode();
                            UserDefaults.getInstance().setStringValueForKey(code, AUTO_SAVED_COUNTRY);
                        }

                        mCountryCode = code;
                        setupUserInterface();
                    }
                });
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

            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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

        boolean handled = mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (!handled)
        {
            handled = AuthenticatorCallbackManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

        PrinticularServiceManager.getInstance()
                .initialize(this, PrinticularEnvironment.STAGING);
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
    /// @name Button Actions
    ///-----------------------------------------------------------

    private boolean checkCartValidity()
    {
        if (mCart.getImageCount() == 0)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Oops you haven't selected any images")
                    .setMessage("Please select some images to continue")
                    .setPositiveButton("Ok", null)
                    .create()
                    .show();

            return false;
        }
        return true;
    }

    public void onNextButtonPressed(View v)
    {

    }

    public void onDeliverButtonPressed(View v)
    {

    }

    public void onPickupButtonPressed(View v)
    {
        if (checkCartValidity())
        {
            Intent i = new Intent(MainActivity.this, ManageOrderActivity.class);
            startActivity(i);
        }
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
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
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

    @Override
    public void onAssetSelect(Asset asset, int index)
    {
        int cartIndex = mCart.indexOfAsset(asset);

        if (mCart.isAssetSelected(asset))
        {
            mCart.removeAssetFromCart(asset);
        }
        else
        {
            mCart.addAssetToCart(asset);
        }

        mCartFragment.onDataChanged(index);
    }

    @Override
    public boolean isAssetSelected(Asset asset, int index)
    {
        return mCart.isAssetSelected(asset);
    }
}
