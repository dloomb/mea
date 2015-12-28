package com.meamobile.printicular_sdk;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.meamobile.printicular_sdk.models.AccessToken;

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

    public interface AccessTokenCallback
    {
        void success();
        void error(String reason);
    }

    private static PrinticularServiceManager mInstance = null;
    private static String LOG_KEY = "MEA.PrinticularServiceManager";

    private Context mContext;
    private PrinticularEnvironment mEnvironment;
    private AccessToken mAccessToken;
    private long mCurrentRequestAttempts;

    public static PrinticularServiceManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new PrinticularServiceManager();
        }
        return mInstance;
    }

    public void initialize(Context context, PrinticularEnvironment environment)
    {
        mContext = context;
        mEnvironment = environment;

        validateAccessToken(null);
    }

    public void validateAccessToken(final AccessTokenCallback callback)
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

        if (mAccessToken != null && mAccessToken.hasExpired() == false)
        {
            callback.success();
            return;
        }

        final APIClient client = new APIClient(getBaseUrlForEnvironment());

        final Bundle params = new Bundle();
        params.putString("grant_type", "client_credentials");
        params.putString("client_id", "UN0Re26fy7V0Rc368QW7");
        params.putString("client_secret", "deKAAuZJTi74eUjMVjaTFMoWfvH43jxluR3CQifB");
        params.putString("scope", "warehouse-stationery");

        mCurrentRequestAttempts = 0;
        APIClient.APIClientCallback internalCallback;
        internalCallback = new APIClient.APIClientCallback()
        {
            @Override
            public void success(Map<String, Object> response)
            {
                if (response.get("error") == null)
                {
                    Log.d(LOG_KEY, "OAuth Success");
                    setAccessTokenFromResponse(response);
                    if(callback != null) callback.success();
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
                    if(callback != null) callback.error("failed");
                    return;
                }

                Log.e(LOG_KEY, "OAuth Failed, retrying...");
                client.post("oauth/access_token", params, this);
            }
        };
        client.post("oauth/access_token", params, internalCallback);
    }


    private void setAccessTokenFromResponse(Map<String, Object> response)
    {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, ((Number) response.get("expires_in")).intValue());

        mAccessToken = new AccessToken((String)response.get("access_token"), null, calendar.getTime());

        try {
            mAccessToken.storeToken(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
