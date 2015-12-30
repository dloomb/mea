package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

@SuppressLint("ParcelCreator")
public class PhotobucketSource extends Source
{
    private static String TAG = "MEA.PhotobucketSource";

    private String mConsumerKey, mConsumerSecret;
    private Token mRequestToken, mAccessToken;
    private OAuthService mOAuthService;

    public PhotobucketSource()
    {
        this.Title = "Photobucket";
        this.ImageResourceId = R.drawable.photobucket_badge;

        mConsumerKey = "149833797";
        mConsumerSecret = "4a90d33bbac6183f8f3a389b4fbab300";

        mOAuthService = new ServiceBuilder()
                .provider(PhotobucketScribeAPI.class)
                .apiKey(mConsumerKey)
                .apiSecret(mConsumerSecret)
                .callback("https://printicular.com/bleep")
                .build();
    }


    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);

        final Activity _activity = activity;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                mRequestToken = mOAuthService.getRequestToken();
                final String authUrl = mOAuthService.getAuthorizationUrl(mRequestToken);

                _activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showAuthenticatorDialogWithUrl(_activity, authUrl);
                    }
                });
            }
        }).start();
    }

    private void showAuthenticatorDialogWithUrl(Activity activity, String url)
    {
        final int _requestCode = 111;

        AuthenticatorCallbackManager.getInstance().addListener(new AuthenticatorCallbackManager.OnResultListener()
        {
            @Override
            public boolean handleResult(int requestCode, int resultCode, Intent data)
            {
                if (requestCode == _requestCode)
                {
                    AuthenticatorCallbackManager.getInstance().removeListener(this);

                    if (resultCode == Activity.RESULT_OK)
                    {
                        String redirectUrl = data.getStringExtra(AuthenticatorActivity.REDIRECT_URL);
                        Log.d(TAG, "Auth Success: " + redirectUrl);

                        handleAccessTokenGet();
                    }

                    return true;
                }
                return false;
            }
        });

        Intent i = new Intent(activity, AuthenticatorActivity.class);
        i.putExtra(AuthenticatorActivity.AUTH_URL, url);
        i.putExtra(AuthenticatorActivity.REDIRECT_URL, "http://api.photobucket.com/apilogin/done");
        i.putExtra(AuthenticatorActivity.TITLE, "Login to Photobucket");
        activity.startActivityForResult(i, _requestCode);
    }

    private void handleAccessTokenGet()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                mAccessToken = mOAuthService.getAccessToken(mRequestToken, new Verifier(" "));
                handleSourceActivation(true, null);
                Log.d(TAG, "AccessToken Got: " + mAccessToken.getToken());
            }
        }).start();
    }

}
