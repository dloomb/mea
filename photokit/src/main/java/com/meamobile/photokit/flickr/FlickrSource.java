package com.meamobile.photokit.flickr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import java.util.Scanner;

@SuppressLint("ParcelCreator")
public class FlickrSource extends Source
{
    private static final int FLICKR_AUTHENTICATOR_REQUEST_CODE = 441;
    private static final String FLICKR_TOKEN_KEY = "com.meamobile.photokit.flickr.token";
    private static final String FLICKR_TOKEN_SECRET_KEY = "com.meamobile.photokit.flickr.token_secret";
    private static final String FLICKR_USERNAME_KEY = "com.meamobile.photokit.flickr.token";
    private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";


    private OAuthService mOAuthService;
    private Token mRequestToken, mAccessToken;
    private String mApiKey, mApiSecret;

    public FlickrSource()
    {
        mTitle = "Flickr";
        mImageResourceId = R.drawable.flickr_badge;

        loadFlickrSession();

        mApiKey = "dc494566c4fc381a8d137dfecc789a6e";
        mApiSecret = "769f3eae5eed5106";
        String callbackUrl = "fk" + mApiKey + "://auth";

        mOAuthService = new ServiceBuilder()
                .provider(FlickrApi.class)
                .apiKey(mApiKey)
                .apiSecret(mApiSecret)
                .callback(callbackUrl)
                .build();
    }

    @Override
    public boolean isActive()
    {
        return mAccessToken != null;
    }

    @Override
    public int getBrandColor()
    {
        return 0xFFff0084;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);

        final Activity _activity = activity;

        new Thread(new Runnable() { @Override public void run()
        {
            startAuthentication(_activity);
        }}).start();
    }

    @Override
    public void invalidateSource()
    {
        super.invalidateSource();
        mAccessToken = null;

        UserDefaults defaults = UserDefaults.getInstance();
        defaults.setStringValueForKey(null, FLICKR_TOKEN_KEY);
        defaults.setStringValueForKey(null, FLICKR_TOKEN_SECRET_KEY);
        defaults.setStringValueForKey(null, FLICKR_USERNAME_KEY);
    }

    public OAuthRequest newRequestWithVerb(Verb verb)
    {
        OAuthRequest req = new OAuthRequest(verb, PROTECTED_RESOURCE_URL, mOAuthService);
        req.addQuerystringParameter("api_key", mApiKey);
        return req;
    }

    public void signRequest(OAuthRequest request)
    {
        mOAuthService.signRequest(mAccessToken, request);
    }

    protected void startAuthentication(Activity activity)
    {
        mRequestToken = mOAuthService.getRequestToken();
        String authorizationUrl = mOAuthService.getAuthorizationUrl(mRequestToken);

        AuthenticatorCallbackManager.getInstance().addListener(this);

        Intent i = new Intent(activity, AuthenticatorActivity.class);
        i.putExtra(AuthenticatorActivity.AUTH_URL, authorizationUrl);
        i.putExtra(AuthenticatorActivity.REDIRECT_URL, mOAuthService.getConfig().getCallback());
        i.putExtra(AuthenticatorActivity.TITLE, "Login to Flickr");
        activity.startActivityForResult(i, FLICKR_AUTHENTICATOR_REQUEST_CODE);
    }

    protected void postCodeForAuthentication(String url)
    {
        final String _url = url;
        new Thread(new Runnable() { @Override public void run()
        {
            String code = Uri.parse(_url).getQueryParameter("oauth_verifier");

            Verifier verifier = new Verifier(code);
            mAccessToken = mOAuthService.getAccessToken(mRequestToken, verifier);

            if (mAccessToken != null)
            {
                mUsername = Uri.parse("https://printicular.com?" + mAccessToken.getRawResponse()).getQueryParameter("username");
                saveFlickrSession();
                handleSourceActivation(true, null);
            }
            else
            {
                handleSourceActivation(false, "Flickr failed to get an OAuth token.");
            }
        }
        }).start();
    }

    protected void saveFlickrSession()
    {
        UserDefaults defaults = UserDefaults.getInstance();
        defaults.setStringValueForKey(mUsername, FLICKR_USERNAME_KEY);
        defaults.setStringValueForKey(mAccessToken.getToken(), FLICKR_TOKEN_KEY);
        defaults.setStringValueForKey(mAccessToken.getSecret(), FLICKR_TOKEN_SECRET_KEY);
    }

    protected void loadFlickrSession()
    {
        UserDefaults defaults = UserDefaults.getInstance();

        String token = defaults.stringForKey(FLICKR_TOKEN_KEY);
        if (token != null)
        {
            String secret = defaults.stringForKey(FLICKR_TOKEN_SECRET_KEY);
            mUsername = defaults.stringForKey(FLICKR_USERNAME_KEY);
            mAccessToken = new Token(token, secret);
        }
    }

    ///-----------------------------------------------------------
    /// @name Authenticator Callback
    ///-----------------------------------------------------------

    @Override
    public boolean handleResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == FLICKR_AUTHENTICATOR_REQUEST_CODE)
        {
            AuthenticatorCallbackManager.getInstance().removeListener(this);

            if (resultCode == Activity.RESULT_OK)
            {
                String redirectUrl = data.getStringExtra(AuthenticatorActivity.REDIRECT_URL);
                postCodeForAuthentication(redirectUrl);
            }
            else
            {
                handleSourceActivation(false, "AuthenticatorActivity did not finish with RESULT_OK");
            }
            return true;
        }
        return false;
    }
}
