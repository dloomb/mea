package com.meamobile.printicular.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.meamobile.printicular.AuthenticatableActivity;
import com.meamobile.printicular.utilities.LocationUtil;
import com.meamobile.printicular.R;
import com.meamobile.printicular.main.cart.CartFragment;
import com.meamobile.printicular.main.cart.PhotoKitCartManager;
import com.meamobile.printicular.settings.LocationPickerDialog;
import com.meamobile.printicular.settings.SettingsActivity;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager.PrinticularEnvironment;
import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.user_interface.CheckoutActivity;
import com.meamobile.printicular_sdk.user_interface.address.AddressEntryActivity;
import com.meamobile.printicular_sdk.user_interface.ManageOrderActivity;
import com.meamobile.printicular_sdk.user_interface.common.BlockingLoadIndicator;
import com.meamobile.printicular_sdk.user_interface.store_search.StoreSearchActivity;

import java.util.List;
import java.util.Locale;


import android.os.Handler;
import android.os.Looper;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AuthenticatableActivity implements ExplorerFragmentDelegate
{
    public static String TAG = "MEA.ActivityMain";

    private long mLastBackTap = 0;
    private CallbackManager mFacebookCallbackManager;
    private Locale mCountryLocale;
    private CartFragment mCartFragment;
    private ExplorerFragment mRootFragment;
    private Collection mRootCollection;
    private int mNavigationColor;

    private PrinticularServiceManager mServiceManager = PrinticularServiceManager.getInstance();
    private PhotoKitCartManager mCartManager = PhotoKitCartManager.getInstance();

    //UI
    private FrameLayout mFrameLayoutDeliver, mFrameLayoutPickup;
    private ImageView mImageViewDeliver, mImageViewPickup;
    private LinearLayout mBtnContainer;
    private TextView mCartTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //Setup UserDefaults Singleton
        UserDefaults.getInstance().setContext(this);

        mCartManager.setRootActivty(MainActivity.class);

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
        int grey = getResources().getColor(R.color.printicular_primary_grey);
        int orange = getResources().getColor(R.color.printicular_kodak_orange);

        mFrameLayoutDeliver = (FrameLayout) findViewById(R.id.frameLayoutDeliver);
        mFrameLayoutPickup = (FrameLayout) findViewById(R.id.frameLayoutPickup);
        mImageViewDeliver = (ImageView) findViewById(R.id.imageViewDeliver);
        mImageViewPickup = (ImageView) findViewById(R.id.imageViewPickup);
        mBtnContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        mCartTextView = (TextView) findViewById(R.id.textViewCart);

        mBtnContainer.setVisibility(View.INVISIBLE);
        mCartTextView.setBackgroundColor(mNavigationColor);

        switch (mCountryLocale.getISO3Country())
        {
            case "NZL":
                mBtnContainer.setVisibility(View.VISIBLE);
                mFrameLayoutPickup.setBackgroundColor(blue);
                mFrameLayoutDeliver.setBackgroundColor(grey);
                mImageViewPickup.setImageResource(R.drawable.print_to_ws);
                break;

            case "USA":
                mBtnContainer.setVisibility(View.VISIBLE);
                mFrameLayoutPickup.setBackgroundColor(red);
                mFrameLayoutDeliver.setBackgroundColor(blue);
                mImageViewPickup.setImageResource(R.drawable.print_to_wag);
                break;

            case "DEU":
                mBtnContainer.setVisibility(View.VISIBLE);
                mFrameLayoutPickup.setBackgroundColor(red);
                mFrameLayoutDeliver.setBackgroundColor(blue);
                mImageViewPickup.setImageResource(R.drawable.print_to_dm);
                break;

            default:
                mBtnContainer.setVisibility(View.VISIBLE);
                mFrameLayoutPickup.setBackgroundColor(blue);
                mFrameLayoutDeliver.setBackgroundColor(grey);
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
    protected void onStart()
    {
        super.onStart();

        mServiceManager.initialize(this, PrinticularEnvironment.STAGING);

        mCartManager.setContext(this);
        mCartManager.setCurrentStore(mCartManager.loadSavedStore());
        mCartManager.setCurrentAddress(mCartManager.loadSavedAddress());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        determineLocation();

        //For Testing a Activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {@Override public void run()
        {
            Intent i = new Intent(MainActivity.this, AddressEntryActivity.class);
//            startActivity(i);
        }}, 500);
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

    public void onNextButtonPressed(View v)
    {

    }

    public void onDeliverButtonPressed(View v)
    {

    }

    public void onPickupButtonPressed(View v)
    {
        if (!checkCartValidity())
        {
            return;
        }

        if (mCountryLocale == null)
        {
            LocationPickerDialog picker = new LocationPickerDialog(this);
            picker.setLocationPickerDialogInterface(new LocationPickerDialog.LocationPickerDialogInterface()
            {
                @Override
                public void OnCountrySelected(LocationPickerDialog dialog, Locale country) {}

                @Override
                public void OnOkClicked(LocationPickerDialog dialog, Locale country)
                {
                    mCountryLocale = country;
                    LocationUtil.setUserSavedCountry(country);
                    handlePickupPressed();
                    dialog.dismiss();
                }

                @Override
                public void OnCancelClicked(LocationPickerDialog dialog, Locale country) {dialog.dismiss();}
            });
            picker.setCurrentLocale(LocationUtil.getCurrentCountry());
            picker.show();

            return;
        }

        handlePickupPressed();
    }

    protected void handlePickupPressed()
    {
        PrintService service = mServiceManager.getPrintServiceWithId(3); // Should eventually be pulled based on Territory Model

        switch (mCountryLocale.getISO3Country())
        {
            case "NZL":
                service = PrinticularServiceManager.getInstance().getPrintServiceWithId(3);
                break;
        }

        mCartManager.setCurrentPrintService(service);
        mServiceManager.registerImages(mCartManager.getImages())
                .retry(5)
                .lift(new BlockingLoadIndicator(this))
                .subscribe(rr -> {

                    PrintService currentService = mCartManager.getCurrentPrintService();
                    Intent i = new Intent(MainActivity.this, ManageOrderActivity.class);

                    if (mCartManager.getCurrentAddress() == null) {
                        i.setClass(MainActivity.this, AddressEntryActivity.class);
                        i.putExtra(CheckoutActivity.EXTRA_DONE_BUTTON_ENABLED, true);
                    } else if (mCartManager.getCurrentStore() == null
                            && currentService.getFulFillmentType() == PrintService.FulfillmentType.PICKUP) {
                        i.setClass(MainActivity.this, StoreSearchActivity.class);
                        i.putExtra(CheckoutActivity.EXTRA_DONE_BUTTON_ENABLED, true);
                    }

                    startActivity(i);

                }, error -> {

                });

    }

    private boolean checkCartValidity()
    {
        if (mCartManager.getImageCount() == 0)
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

        if (fragment instanceof ExplorerFragment && fragment != mRootFragment)
        {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.executePendingTransactions();

            ExplorerFragment frag = (ExplorerFragment) getCurrentFragment();
            if (frag != null)
            {
                frag.onFragmentWillAppear();
            }
        }
        else
        {
            long now = System.currentTimeMillis();
            long diff = now - mLastBackTap;

            if (diff < 2000)
            {
                moveTaskToBack(true);
            }
            else
            {
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
                mLastBackTap = now;
            }
        }
    }

    protected void pushRootExplorer()
    {
        Source.BASE_BRAND_COLOR = getResources().getColor(R.color.printicular_blue);

//        mRootCollection = CollectionFactory.initRootCollectionWithSourceTypes(EnumSet.of(
//                CollectionType.Local,
//                CollectionType.Instagram,
//                CollectionType.Facebook,
//                CollectionType.Dropbox,
//                CollectionType.Photobucket,
//                CollectionType.Flickr,
//                CollectionType.Google
//        ));

        mRootCollection = CollectionFactory.initRootCollectionWithSourceTypes(CollectionType.All);

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
        mNavigationColor = color;

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] = hsv[2] * 0.9f;
            color = Color.HSVToColor(hsv);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }

        if (mCartTextView != null)
        {
            mCartTextView.setBackgroundColor(color);
        }
    }

    @Override
    public void setDisplaysBackButton(Boolean shouldDisplay) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplay);
    }

    @Override
    public void onAssetSelect(Asset asset, int index)
    {
        if (mCartManager.isAssetSelected(asset))
        {
            mCartManager.removeAssetFromCart(asset);
        }
        else
        {
            mCartManager.addAssetToCart(asset);
        }

        mCartFragment.onDataChanged(index);
    }

    @Override
    public boolean isAssetSelected(Asset asset, int index)
    {
        return mCartManager.isAssetSelected(asset);
    }
}
