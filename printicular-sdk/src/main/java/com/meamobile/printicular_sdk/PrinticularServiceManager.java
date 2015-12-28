package com.meamobile.printicular_sdk;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.meamobile.printicular_sdk.models.AccessToken;

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

        validateAccessToken(new AccessTokenCallback()
        {
            @Override
            public void success()
            {
                Log.d(LOG_KEY, "OAuth Success");
            }

            @Override
            public void error(String reason)
            {
                Log.e(LOG_KEY, "OAuth Failure");
            }
        });
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

        mCurrentRequestAttempts = 0;
        APIClient.APIClientCallback internalCallback;
        internalCallback = new APIClient.APIClientCallback()
        {
            @Override
            public void success(Map<String, Object> response)
            {
                if (response.get("error") == null)
                {

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
                    callback.error("failed");
                    Log.e(LOG_KEY, "OAuth Failed, retrying...");
                    return;
                }

                client.get("oauth/access_token?grant_type=client_credentials&client_id=c9U2YsPDlis9gwPl&client_secret=AqHvqcPT11uRi8nSBt301SYNRtjlJb8h", params, this);
            }
        };
        // TODO: 28/12/15 Its a POST you dummy! 
        client.get("oauth/access_token?grant_type=client_credentials&client_id=UN0Re26fy7V0Rc368QW7&client_secret=deKAAuZJTi74eUjMVjaTFMoWfvH43jxluR3CQifB&scope=warehouse-stationery", params, internalCallback);
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
