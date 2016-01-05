package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class PhotobucketSource extends Source
{
    private static String TAG = "MEA.PhotobucketSource";
    private static final int AUTHENTICATOR_REQUEST_CODE = 929;
    private static final String PHOTOBUCKET_SAVED_DATA = "com.meamobile.photokit.photobucket.saved_data";


    private String mConsumerKey, mConsumerSecret, mUsername, mHomeUrl, mBaseUrl;
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
//                .callback("https://api.photobucket.com")
                .signatureType(SignatureType.QueryString)
                .build();

        refreshToken();
    }

    public OAuthService getOAuthService()
    {
        return mOAuthService;
    }

    public Token getOAuthToken()
    {
        return mAccessToken;
    }

    public String getUsername()
    {
        return mUsername;
    }

    public String getHomeUrl()
    {
        return mHomeUrl;
    }

    public String getBaseUrl()
    {
        return mBaseUrl;
    }

//    public OAuthRequest signUrl(Verb verb, String url)
//    {
//        OAuthRequest request = new OAuthRequest(verb, url, mOAuthService);
//
//        request.addOAuthParameter(OAuthConstants.CONSUMER_KEY, mConsumerKey);
//        request.addOAuthParameter(OAuthConstants.SIGN_METHOD, "HMAC-SHA1");
//        request.addOAuthParameter(OAuthConstants.TIMESTAMP, "" + new Date().getTime());
//
//        return request;
//    }

    @Override
    public boolean isActive()
    {
        return mAccessToken != null;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);

        final Activity _activity = activity;

        new Thread(new Runnable() {@Override public void run()
        {
            mRequestToken = mOAuthService.getRequestToken();
            final String authUrl = mOAuthService.getAuthorizationUrl(mRequestToken);

            _activity.runOnUiThread(new Runnable() {@Override public void run()
            {
                showAuthenticatorDialogWithUrl(_activity, authUrl);
            }});
        }}).start();
    }

    private void refreshToken()
    {
        String in = UserDefaults.getInstance().stringForKey(PHOTOBUCKET_SAVED_DATA);
        if (in != null)
        {
            Map<String, String> m = new Gson().fromJson(in, new TypeToken<Map<String, String>>(){}.getType());
            mUsername = m.get("username");
            mBaseUrl = m.get("baseurl");
            mHomeUrl = m.get("homeurl");
            String token = m.get("token");
            String secret = m.get("secret");
            mAccessToken = new Token(token, secret);
        }
    }

    private void showAuthenticatorDialogWithUrl(Activity activity, String url)
    {
        AuthenticatorCallbackManager.getInstance().addListener(getResultListener());

        Intent i = new Intent(activity, AuthenticatorActivity.class);
        i.putExtra(AuthenticatorActivity.AUTH_URL, url);
        i.putExtra(AuthenticatorActivity.REDIRECT_URL, "http://api.photobucket.com/apilogin/done");
        i.putExtra(AuthenticatorActivity.TITLE, "Login to Photobucket");
        activity.startActivityForResult(i, AUTHENTICATOR_REQUEST_CODE);
    }

    private AuthenticatorCallbackManager.OnResultListener getResultListener()
    {
        return new AuthenticatorCallbackManager.OnResultListener()
        {
            @Override
            public boolean handleResult(int requestCode, int resultCode, Intent data)
            {
                if (requestCode == AUTHENTICATOR_REQUEST_CODE)
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
        };
    }

    private void handleAccessTokenGet()
    {
        new Thread(new Runnable() {@Override public void run()
        {
            try
            {
                mAccessToken = mOAuthService.getAccessToken(mRequestToken, new Verifier(" "));

                extractAndSaveAccessToken(mAccessToken);

                handleSourceActivation(true, null);
                Log.d(TAG, "AccessToken Got: " + mAccessToken.getToken());
            }
            catch (Exception e)
            {
                handleSourceActivation(false, "Failed to get OAuth Token from Photobucket.");
            }

        }}).start();
    }

    private void extractAndSaveAccessToken(Token token) throws UnsupportedEncodingException
    {
        //append printicular.com so the url can be Parsed
        String raw = "https://printicular.com?" + mAccessToken.getRawResponse();
        String url = URLDecoder.decode(raw, "UTF-8");
        Uri uri = Uri.parse(url);

        mUsername = uri.getQueryParameter("username");
        mHomeUrl = uri.getQueryParameter("homeurl");
        mBaseUrl = uri.getQueryParameter("subdomain");

        Map m = new HashMap();
        m.put("token", token.getToken());
        m.put("secret", token.getSecret());
        m.put("username", mUsername);
        m.put("baseurl", mBaseUrl);
        m.put("homeurl", mHomeUrl);

        String out = new Gson().toJson(m);
        UserDefaults.getInstance().setStringValueForKey(out, PHOTOBUCKET_SAVED_DATA);
    }
}
