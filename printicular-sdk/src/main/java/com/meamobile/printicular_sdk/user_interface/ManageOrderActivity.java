package com.meamobile.printicular_sdk.user_interface;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.PrintService.FulfillmentType;
import com.meamobile.printicular_sdk.core.models.Store;
import com.meamobile.printicular_sdk.user_interface.address.AddressDetailsViewHolder;
import com.meamobile.printicular_sdk.user_interface.address.AddressEntryActivity;
import com.meamobile.printicular_sdk.user_interface.common.OrderSummaryViewHolder;
import com.meamobile.printicular_sdk.user_interface.common.StoreDetailsViewHolder;
import com.meamobile.printicular_sdk.user_interface.store_search.StoreSearchActivity;

import java.util.Collection;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ManageOrderActivity extends CheckoutActivity
{
    private static final String TAG = "MEA.ManageOrderAct";

    private PrinticularCartManager mCartManager = PrinticularCartManager.getInstance();
    private PrinticularServiceManager mServiceManger = PrinticularServiceManager.getInstance();
    private Subscription mSubscriptionLoadingAddresses;

    //UI
    private OrderSummaryViewHolder mOrderSummaryViewHolder;
    private StoreDetailsViewHolder mStoreDetailsViewHolder;
    private AddressDetailsViewHolder mAddressDetailsViewHolder;
    private LinearLayout mLinearLayoutPaymentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        loadComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserInterface();

        displayCurrentSelectedStore();
        loadAddresses();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mSubscriptionLoadingAddresses != null) {
            mSubscriptionLoadingAddresses.unsubscribe();
        }
    }

    public void onConfirmButtonClicked(View v)
    {
        mCartManager.createNewOrderInstance()
                .flatMap(order -> {
                    return mServiceManger.fetchSavedAddresses();
                })
                .subscribe(order -> {

                }, error -> {

                });
    }

    protected void onPostalDetailsPressed(View v)
    {
        if (mSubscriptionLoadingAddresses == null || mSubscriptionLoadingAddresses.isUnsubscribed())
        {
            mSubscriptionLoadingAddresses = mServiceManger
                    .fetchSavedAddresses()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(x -> {
                        mSubscriptionLoadingAddresses.unsubscribe();

                        Intent i = null;
                        if (x.size() == 0)
                        {
                            i = new Intent(this, AddressEntryActivity.class);
                        }
                        else
                        {
                            i = new Intent(this, AddressEntryActivity.class); // No List Yet
//                            i = new Intent(this, AddressListActivity.class);
                        }
                        startActivity(i);
                    }, error -> {
                        mSubscriptionLoadingAddresses.unsubscribe();
                        new AlertDialog.Builder(this)
                                .setTitle("Address Error")
                                .setMessage("Sorry, we were unable to connect to the Printicular Server. Please check your internet connection and try again")
                                .show();
                    });
        }
    }


    protected void onStoreDetialsPressed(View v)
    {
        Intent i = new Intent(ManageOrderActivity.this, StoreSearchActivity.class);
        startActivity(i);
    }




    ///-----------------------------------------------------------
    /// @name User Interface
    ///-----------------------------------------------------------

    protected void loadComponents()
    {
        mOrderSummaryViewHolder = new OrderSummaryViewHolder(findViewById(R.id.relativeLayoutOrderSummary));

        mStoreDetailsViewHolder = new StoreDetailsViewHolder(findViewById(R.id.relativeLayoutOuter));
        mStoreDetailsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStoreDetialsPressed(v);
            }
        });

        mAddressDetailsViewHolder = new AddressDetailsViewHolder(findViewById(R.id.relativeLayoutAddressViewHolder));
        mAddressDetailsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onPostalDetailsPressed(v);
            }
        });
        mLinearLayoutPaymentDetails = (LinearLayout) findViewById(R.id.paymentDetails);
    }


    private void setupUserInterface()
    {
        FulfillmentType fulfillmentType = mCartManager.getCurrentPrintService().getFulFillmentType();
        switch (fulfillmentType) {

            case PICKUP:
                mStoreDetailsViewHolder.itemView.setVisibility(View.VISIBLE);
                mLinearLayoutPaymentDetails.setVisibility(View.GONE);
                break;

            case DELIVERY:
                mStoreDetailsViewHolder.itemView.setVisibility(View.GONE);
                mLinearLayoutPaymentDetails.setVisibility(View.VISIBLE);
                break;
        }

        mOrderSummaryViewHolder.setupWithLineItemsAndPrintService(mCartManager.getLineItems(), mCartManager.getCurrentPrintService());
        mAddressDetailsViewHolder.setAddress(mCartManager.getCurrentAddress(), fulfillmentType);
        mStoreDetailsViewHolder.applyLayoutForManageOrderScreen();
    }

    private void displayCurrentSelectedStore()
    {
        Store store = mCartManager.getCurrentStore();
        mStoreDetailsViewHolder.setStore(store);
    }



    ///-----------------------------------------------------------
    /// @name Address
    ///-----------------------------------------------------------


    private void loadAddresses()
    {
        mAddressDetailsViewHolder.setLoading(true);

        mSubscriptionLoadingAddresses = mServiceManger.fetchSavedAddresses()
                .retry(2)
                .subscribe(x -> {
                    mAddressDetailsViewHolder.setLoading(false);

                    Collection<Address> v = x.values();
                    mCartManager.setCurrentAddress(v.iterator().next());

                    mAddressDetailsViewHolder.setAddress(mCartManager.getCurrentAddress(), mCartManager.getCurrentPrintService().getFulFillmentType());

                }, error -> {
                    mAddressDetailsViewHolder.setLoading(false);
                });
    }


}
