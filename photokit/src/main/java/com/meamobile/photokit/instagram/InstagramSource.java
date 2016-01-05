package com.meamobile.photokit.instagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.meamobile.photokit.core.JSONHttpClient;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class InstagramSource extends Source
{
    private SourceActivationCallback mActivationCallback;

    static private String TAG = "MEA.InstagramSource";

    static private String CLIENT_ID = "e0fcbb928e4048cea293347393c766e2";
    static private String CLIENT_SECRET = "475555bd27d44ddeadf7763def913f39";

    static private String IG_INSTAGRAM_TOKEN_KEY = "com.meamobile.photokit.instagram.access_token";
    private static final int IG_AUTHENTICATOR_REQUEST_CODE = 137;

    private String mToken;

    public InstagramSource()
    {
        this.ImageResourceId = R.drawable.instagram_badge;
        this.Title = "Instagram";

        loadAccessToken();
    }

    protected void loadAccessToken()
    {
        mToken = UserDefaults.getInstance().stringForKey(IG_INSTAGRAM_TOKEN_KEY);
    }

    protected void saveAccessToken(String token)
    {
        mToken = token;
        UserDefaults.getInstance().setStringValueForKey(token, IG_INSTAGRAM_TOKEN_KEY);
    }

    public String getAccessToken()
    {
        return mToken;
    }

    @Override
    public boolean isActive() {
        return mToken != null;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);

        final String redirectUrl = "ig" + CLIENT_ID + "://authorize";

        String authUrl = "https://api.instagram.com/oauth/authorize?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=" + redirectUrl;

        AuthenticatorCallbackManager.getInstance().addListener(getResultListener());

        Intent i = new Intent(activity, AuthenticatorActivity.class);
        i.putExtra(AuthenticatorActivity.AUTH_URL, authUrl);
        i.putExtra(AuthenticatorActivity.REDIRECT_URL, redirectUrl);
        i.putExtra(AuthenticatorActivity.TITLE, "Login to Instagram");
        activity.startActivityForResult(i, IG_AUTHENTICATOR_REQUEST_CODE);

    }

//    protected AuthenticatorDialogRedirectCallback getAuthenticatorDialogCallback(String redirectUrl)
//    {
//        final String finalRedirectUrl = redirectUrl;
//        return new AuthenticatorDialogRedirectCallback() {
//            @Override
//            public void didHitRedirectUrl(AuthenticatorDialog dialog, String url) {
//                String[] parts = url.split(finalRedirectUrl + "\\?code=");
//                if (parts.length > 1) {
//                    String code = parts[1];
//                    dialog.dismiss();
//                    postCodeForAuthentication(code);
//                }
//            }
//        };
//    }
    
    protected AuthenticatorCallbackManager.OnResultListener getResultListener()
    {
        return new AuthenticatorCallbackManager.OnResultListener()
        {
            @Override
            public boolean handleResult(int requestCode, int resultCode, Intent data)
            {
                if (requestCode == IG_AUTHENTICATOR_REQUEST_CODE)
                {
                    AuthenticatorCallbackManager.getInstance().removeListener(this);

                    if (resultCode == Activity.RESULT_OK)
                    {
                        String redirectUrl = data.getStringExtra(AuthenticatorActivity.REDIRECT_URL);
                        Log.d(TAG, "Auth Success: " + redirectUrl);
                        postCodeForAuthentication(redirectUrl);
                    }

                    return true;
                }
                return false;
            }
        };
    }

    protected void postCodeForAuthentication(String url)
    {
        String code = Uri.parse(url).getQueryParameter("code");

        String redirectUrl = "ig" + CLIENT_ID + "://authorize";

        List<NameValuePair> params = new ArrayList<NameValuePair>(5);
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("redirect_uri", redirectUrl));
        params.add(new BasicNameValuePair("code", code));

        new JSONHttpClient().post("https://api.instagram.com/oauth/access_token", params, new JSONHttpClient.JSONHttpClientCallback() {
            @Override
            public void success(Map<String, Object> response)
            {
                    String token = (String) response.get("access_token");
                    saveAccessToken(token);
                    handleSourceActivation(true, null);
            }

            @Override
            public void error(String error) {

                handleSourceActivation(false, error);
            }
        });
    }


}
