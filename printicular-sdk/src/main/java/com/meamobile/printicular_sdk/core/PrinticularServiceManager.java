package com.meamobile.printicular_sdk.core;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.meamobile.printicular_sdk.core.models.AccessToken;
import com.meamobile.printicular_sdk.core.APIClient.APIClientCallback;
import com.meamobile.printicular_sdk.core.models.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class PrinticularServiceManager
{
    public enum PrinticularEnvironment
    {
        STAGING,
        DEVELOPMENT,
        PRODUCTION
    }

    private static PrinticularServiceManager sInstance = null;
    private static String LOG_KEY = "MEA.PrinticularServiceManager";

    private Context mContext;
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

        validateAccessToken(new AccessTokenCallback()
        {
            @Override
            public void success()
            {
                refreshPrintServices(null);
            }

            @Override
            public void error(String reason) {}
        });
    }



    ///-----------------------------------------------------------
    /// @name OAuth
    ///-----------------------------------------------------------

    public interface AccessTokenCallback
    {
        void success();
        void error(String reason);
    }

    public void validateAccessToken(AccessTokenCallback callback)
    {
        final AccessTokenCallback _callback = callback;

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

        if (mAccessToken != null && mAccessToken.hasExpired() == false)
        {
            Log.d(LOG_KEY, "OAuth Success, Reusing token.");
            if(callback != null) _callback.success();
            return;
        }

        final APIClient client = new APIClient(getBaseUrlForEnvironment());

        final Bundle params = new Bundle();
        params.putString("grant_type", "client_credentials");
        params.putString("client_id", "UN0Re26fy7V0Rc368QW7");
        params.putString("client_secret", "deKAAuZJTi74eUjMVjaTFMoWfvH43jxluR3CQifB");
        params.putString("scope", "warehouse-stationery");

        mCurrentRequestAttempts = 0;
        APIClientCallback internalCallback;
        internalCallback = new APIClientCallback()
        {
            @Override
            public void success(Map<String, Object> response)
            {
                if (response.get("error") == null)
                {
                    Log.d(LOG_KEY, "OAuth Success");
                    setAccessTokenFromResponse(response);
                    if(_callback != null) _callback.success();
                }
                else
                {
                    this.error((String)response.get("error_description"));
                }
            }

            @Override
            public void error(String reason)
            {
                mCurrentRequestAttempts++;
                if (mCurrentRequestAttempts > 5)
                {
                    Log.e(LOG_KEY, "OAuth Failed!");
                    if(_callback != null) _callback.error("failed");
                    return;
                }

                Log.e(LOG_KEY, "OAuth Failed, retrying...");
                client.post("oauth/access_token", params, this, null);
            }
        };
        client.post("oauth/access_token", params, internalCallback, null);
    }




    ///-----------------------------------------------------------
    /// @name PrintService
    ///-----------------------------------------------------------

    public interface RefreshPrintServiceCallback
    {
        void success(Map<Long, PrintService> services);
        void error(String reason);
    }

    public void refreshPrintServices(final RefreshPrintServiceCallback callback)
    {
        //If we already have an Array of PrintServices and they are recent, return them.
        if (mPrintServices != null && mPrintServicesTimeStamp != null && mPrintServicesTimeStamp.after(new Date()))
        {
            Log.d(LOG_KEY, "PrintService Refresh Success, Reusing.");
            if (callback != null) callback.success(mPrintServices);
        }

        final RefreshPrintServiceCallback _callback = callback;

        final APIClient client = new APIClient(getBaseUrlForEnvironment());
        mCurrentRequestAttempts = 0;
        APIClientCallback internalCallback;

        internalCallback = new APIClientCallback()
        {
            @Override
            public void success(Map<String, Object> response)
            {
                Log.d(LOG_KEY, "PrintService Refresh Success.");
                setPrintServicesFromResponse(response);
                if (_callback != null) _callback.success(mPrintServices);
            }

            @Override
            public void error(String reason)
            {
                mCurrentRequestAttempts++;
                if (mCurrentRequestAttempts > 5)
                {
                    mPrintServices = null;
                    mPrintServicesTimeStamp = null;
                    Log.e(LOG_KEY, "PrintService Refresh Failed: " + reason);
                    if(_callback != null) _callback.error("failed");
                    return;
                }

                Log.e(LOG_KEY, "PrintService Refresh Failed, retrying...");
                client.get("printServices?include=products.prices", null, this, mAccessToken);
            }
        };

        client.get("printServices?include=products.prices", null, internalCallback, mAccessToken);
    }


    public PrintService getPrintServiceWithId(long id)
    {
        return mPrintServices.get(id);
    }



    ///-----------------------------------------------------------
    /// @name Stores
    ///-----------------------------------------------------------

    public interface StoreSearchCallback
    {
        void success(Map<Long, Store>stores);
        void error(String error);
    }

    public void searchForStores(PrintService printService, LatLng location, String[] productIds, StoreSearchCallback callback)
    {
        final StoreSearchCallback _callback = callback;

        long printServiceId = printService.getId();

        Bundle parameters = new Bundle();
        parameters.putString("sort[latitude]", location.latitude + "");
        parameters.putString("sort[longitude]", location.longitude + "");

        if (productIds != null && productIds.length > 0)
        {
            String productIdsString = TextUtils.join(",", productIds);
            parameters.putString("filter[products]", productIdsString);
        }

        APIClient client = new APIClient(getBaseUrlForEnvironment());
        mCurrentRequestAttempts = 0;

        APIClientCallback internalCallback;
        internalCallback = new APIClientCallback()
        {
            @Override
            public void success(Map<String, Object> response)
            {
                Log.d(LOG_KEY, "Store Search Success.");
                Map stores = getStoresFromResponse(response);
                if (_callback != null) { _callback.success(stores); }
            }

            @Override
            public void error(String reason)
            {
                mCurrentRequestAttempts++;
                if (mCurrentRequestAttempts > 5)
                {
                    Log.e(LOG_KEY, "Store Search Failed: " + reason);
                    if (_callback != null) { _callback.error(reason); }
                    return;
                }

                Log.e(LOG_KEY, "Store Search Failed, retrying...");
            }
        };

        client.get("printServices/" + printServiceId + "/stores", parameters, internalCallback, mAccessToken);
    }




    ///-----------------------------------------------------------
    /// @name Private Helpers
    ///-----------------------------------------------------------

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
