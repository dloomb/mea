package com.meamobile.printicular_sdk.core;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.meamobile.printicular_sdk.core.models.AccessToken;
import com.meamobile.printicular_sdk.core.models.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import rx.Observable;


public class PrinticularServiceManager
{
    private static String TAG = "MEA.PrinticularServiceManager";

    public enum PrinticularEnvironment
    {
        STAGING,
        DEVELOPMENT,
        PRODUCTION
    }

    private static PrinticularServiceManager sInstance = null;
    private static final String SAVED_UNIQUE_IDENTIFIER_KEY = "com.meamobile.printicular_sdk.unique_identifier";


    private Context mContext;
    private String mUniqueIdenfier;
    private PrinticularEnvironment mEnvironment;
    private long mCurrentRequestAttempts;
    private AccessToken mAccessToken;
    private Map<Long, PrintService> mPrintServices;
    private Date mPrintServicesTimeStamp;


    public static PrinticularServiceManager getInstance()
    {
        if(sInstance == null)
        {
            sInstance = new PrinticularServiceManager();
        }
        return sInstance;
    }

    public Context getContext()
    {
        return mContext;
    }

    public void initialize(Context context, PrinticularEnvironment environment)
    {
        mContext = context;
        mEnvironment = environment;

        Observable.concat(validateAccessToken(), refreshPrintServices())
                .retry(5)
                .doOnError(e -> {
                    Log.e(TAG, e.getLocalizedMessage());
                })
                .subscribe();
    }



    ///-----------------------------------------------------------
    /// @name OAuth
    ///-----------------------------------------------------------

    public Observable<AccessToken> validateAccessToken()
    {
        if (mAccessToken == null)
        {
            try
            {
                mAccessToken = AccessToken.loadToken(mContext);
            }
            catch (Exception e)
            {
                mAccessToken = null;
            }
        }

        if (mAccessToken != null && !mAccessToken.hasExpired())
        {
            Log.d(TAG, "OAuth Success, Reusing.");
            return Observable.just(mAccessToken);
        }

        APIClient client = new APIClient(getBaseUrlForEnvironment());

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "UN0Re26fy7V0Rc368QW7");
        params.put("client_secret", "deKAAuZJTi74eUjMVjaTFMoWfvH43jxluR3CQifB");
        params.put("scope", "warehouse-stationery");

        return client.post("oauth/access_token", params)
                .flatMap(response -> {
                    if (response.get("error") == null)
                    {
                        setAccessTokenFromResponse(response);
                        Log.d(TAG, "OAuth Success");
                        return Observable.just(mAccessToken);
                    }
                    else
                    {
                        return Observable.error(new RuntimeException((String) response.get("error_description")));
                    }
                });
    }


    ///-----------------------------------------------------------
    /// @name PrintService
    ///-----------------------------------------------------------

    public Observable<Map<Long, PrintService>> refreshPrintServices()
    {
        //If we already have an Array of PrintServices and they are recent, return them.
        if (mPrintServices != null && mPrintServicesTimeStamp != null && mPrintServicesTimeStamp.after(new Date()))
        {
            Log.d(TAG, "PrintService Refresh Success, Reusing.");
            return Observable.just(mPrintServices);
        }

        APIClient client = new APIClient(getBaseUrlForEnvironment());


        return client.get("printServices?include=products.prices", null, mAccessToken)
                .flatMap(res -> {
                    setPrintServicesFromResponse(res);

                    Log.d(TAG, "PrintService Refresh Success");

                    return Observable.just(mPrintServices);
                });
    }


    public PrintService getPrintServiceWithId(long id)
    {
        return mPrintServices.get(id);
    }



    ///-----------------------------------------------------------
    /// @name Stores
    ///-----------------------------------------------------------

    public Observable<Map<Long, Store>> searchForStores(PrintService printService, LatLng location, String[] productIds)
    {
        long printServiceId = printService.getId();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("sort[latitude]", location.latitude + "");
        parameters.put("sort[longitude]", location.longitude + "");

        if (productIds != null && productIds.length > 0)
        {
            String productIdsString = TextUtils.join(",", productIds);
            parameters.put("filter[products]", productIdsString);
        }

        APIClient client = new APIClient(getBaseUrlForEnvironment());

        return client.get("printServices/" + printServiceId + "/stores", parameters, mAccessToken)
                .flatMap(response -> {
                    Map<Long, Store> stores = getStoresFromResponse(response);
                    return Observable.just(stores);
                });
    }


    ///-----------------------------------------------------------
    /// @name Addresses
    ///-----------------------------------------------------------

    public Observable<Map<Long, Address>> fetchSavedAddresses()
    {
        APIClient client = new APIClient(getBaseUrlForEnvironment());

        Map<String, String> params = new HashMap<>();
        params.put("deviceToken", getUniqueIdentifer());

        return client.get("users/0/addresses", params, mAccessToken)
                .flatMap(response -> {
                    Map objects = (Map) Model.hydrate(response);
                    return Observable.just((Map<Long, Address>) objects.get("address"));
                });
    }

    public Observable<Address> saveAddress(Address address)
    {
        APIClient client = new APIClient(getBaseUrlForEnvironment());

        Map<String, Object> params = address.evaporate();

        return client.post("users/0/addresses?deviceToken=" + getUniqueIdentifer() , params, mAccessToken)
                .flatMap(response -> {
                    Map objects = (Map) Model.hydrate(response);
                    return Observable.just((Address) objects.get("address"));
                });
    }


    ///-----------------------------------------------------------
    /// @name Private Helpers
    ///-----------------------------------------------------------

    public String getUniqueIdentifer()
    {
        if (mUniqueIdenfier != null)
        {
            return mUniqueIdenfier;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(SAVED_UNIQUE_IDENTIFIER_KEY, Context.MODE_PRIVATE);
        mUniqueIdenfier = prefs.getString(SAVED_UNIQUE_IDENTIFIER_KEY, null);

        if (mUniqueIdenfier == null)
        {
            mUniqueIdenfier = UUID.randomUUID().toString();
            SharedPreferences.Editor e = prefs.edit();
            e.putString(SAVED_UNIQUE_IDENTIFIER_KEY, mUniqueIdenfier);
            e.apply();
        }
        return mUniqueIdenfier;
    }

    private void setAccessTokenFromResponse(Map<String, Object> response)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, ((Number) response.get("expires_in")).intValue());

        mAccessToken = new AccessToken((String)response.get("access_token"), null, calendar.getTime());

        try {
            mAccessToken.storeToken(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPrintServicesFromResponse(Map<String, Object> response)
    {
        Map objects = (Map) Model.hydrate(response);
        mPrintServices = (Map) objects.get("print_services");
        mPrintServicesTimeStamp = new Date();
    }

    private Map<Long, Store> getStoresFromResponse(Map<String, Object> response)
    {
        Map objects = (Map) Model.hydrate(response);
        return (Map<Long, Store>) objects.get("stores");
    }


    private String getBaseUrlForEnvironment()
    {
        switch (mEnvironment)
        {
            case DEVELOPMENT:
                return "http://printicular.dev/api/1.0/";
            case PRODUCTION:
                return "https://api.printicular.com/api/1.0/";
            default:
                return "https://stagingapi.printicular.com/api/1.0/";
        }
    }






}
