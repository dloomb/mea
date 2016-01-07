package com.meamobile.printicular;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.Collection.CollectionType;
import com.meamobile.photokit.core.CollectionFactory;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.dropbox.DropboxSource;
import com.meamobile.photokit.user_interface.ExplorerFragment;
import com.meamobile.photokit.user_interface.ExplorerFragment.ExplorerFragmentDelegate;
import com.meamobile.printicular.cart.CartFragment;
import com.meamobile.printicular.cart.PhotoKitCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager.PrinticularEnvironment;
import com.meamobile.printicular_sdk.user_interface.ManageOrderActivity;

import java.util.EnumSet;
import java.util.Locale;


public class MainActivity extends AuthenticatableActivity implements ExplorerFragmentDelegate
{
    public static String TAG = "MEA.ActivityMain";

    private long lastBackTap = 0;
    private CallbackManager mFacebookCallbackManager;
    private Locale mCountryLocale;
    private PhotoKitCartManager mCart;
    private CartFragment mCartFragment;
    private ExplorerFragment mRootFragment;
    private Collection mRootCollection;

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
        DropboxSource.APP_KEY = getString(R.string.dropbox_app_key);

        pushRootExplorer();
    }

    private void setupUserInterface()
    {
        Log.d(TAG, "Updating UI for " + mCountryLocale.getISO3Country());

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

        switch (mCountryLocale.getISO3Country())
        {
            case "NZL":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(blue, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Warehouse Stationery");
                mBtnDeliver.getBackground().setColorFilter(grey, PorterDuff.Mode.MULTIPLY);
                break;

            case "USA":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(red, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Walgreens");
                mBtnDeliver.getBackground().setColorFilter(blue, PorterDuff.Mode.MULTIPLY);
                break;

            case "DEU":
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(orange, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Kodak");
                mBtnDeliver.getBackground().setColorFilter(grey, PorterDuff.Mode.MULTIPLY);
                break;

            default:
                mBtnContainer.setVisibility(View.VISIBLE);
                mBtnPickup.getBackground().setColorFilter(red, PorterDuff.Mode.MULTIPLY);
                mBtnPickup.setText("Pickup");
                mBtnDeliver.getBackground().setColorFilter(blue, PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    private void determineLocation()
    {
        //Location has already been set
        mCountryLocale = LocationUtil.getCurrentCountry();
        if (mCountryLocale != null)
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
                        Locale locale = getApplicationContext().getResources().getConfiguration().locale;

                        if (address != null)
                        {
                            locale = address.getLocale();
                            LocationUtil.setAutoSavedCountry(locale);
                        }

                        mCountryLocale = locale;
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

        if (id == R.id.action_settings)
        {
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
    protected void onRestart()
    {
        super.onRestart();

        PrinticularServiceManager.getInstance()
                .initialize(this, PrinticularEnvironment.STAGING);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        determineLocation();
    }

    ///-----------------------------------------------------------
    /// @name Hardware Button Input
    ///-----------------------------------------------------------

    @Override
    public void onBackPressed()
    {
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
            return mRootFragment;
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
//            else
//            {
//                setTitle("Select a Source");
//                setDisplaysBackButton(false);
//            }
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
        Source.BASE_BRAND_COLOR = getResources().getColor(R.color.printicular_blue);

        mRootCollection = CollectionFactory.initRootCollectionWithSourceTypes(EnumSet.of(
                CollectionType.Local,
                CollectionType.Instagram,
                CollectionType.Facebook,
                CollectionType.Dropbox,
                CollectionType.Photobucket,
                CollectionType.Flickr,
                CollectionType.Google
        ));

        mRootFragment = ExplorerFragment.newInstance(mRootCollection);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, mRootFragment)
                .commit();

        getSupportFragmentManager().executePendingTransactions();
        mRootFragment.onFragmentWillAppear();
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
    public void setNavigationColor(int color)
    {
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
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
