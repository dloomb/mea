package com.meamobile.photokit.instagram;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meamobile.photokit.core.JSONHttpClient;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorDialog;
import com.meamobile.photokit.user_interface.AuthenticatorDialog.AuthenticatorDialogRedirectCallback;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstagramSource extends Source
{
    private SourceActivationCallback mActivationCallback;

    static private String CLIENT_ID = "e0fcbb928e4048cea293347393c766e2";
    static private String CLIENT_SECRET = "475555bd27d44ddeadf7763def913f39";

    static private String IG_INSTAGRAM_TOKEN_KEY = "com.meamobile.photokit.instagram.access_token";

    private String mToken;

    public InstagramSource()
    {
        this.ImageResourceId = R.mipmap.instagram_badge;
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
    public void activateSource(Context context, SourceActivationCallback callback)
    {
        mActivationCallback = callback;

        final String redirectUrl = "ig" + CLIENT_ID + "://authorize";

        AuthenticatorDialog dialog = new AuthenticatorDialog(context);
        dialog.setAuthenticationUrl("https://api.instagram.com/oauth/authorize?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=" + redirectUrl);
        dialog.setRedirectUrl(redirectUrl, getAuthenticatorDialogCallback(redirectUrl));
        dialog.show();
    }

    protected AuthenticatorDialogRedirectCallback getAuthenticatorDialogCallback(String redirectUrl)
    {
        final String finalRedirectUrl = redirectUrl;
        return new AuthenticatorDialogRedirectCallback() {
            @Override
            public void didHitRedirectUrl(AuthenticatorDialog dialog, String url) {
                String[] parts = url.split(finalRedirectUrl + "\\?code=");
                if (parts.length > 1) {
                    String code = parts[1];
                    dialog.dismiss();
                    postCodeForAuthentication(code);
                }
            }
        };
    }

    protected void postCodeForAuthentication(String code)
    {
        String redirectUrl = "ig" + CLIENT_ID + "://authorize";

        List<NameValuePair> params = new ArrayList<NameValuePair>(5);
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("redirect_uri", redirectUrl));
        params.add(new BasicNameValuePair("code", code));

        new JSONHttpClient().post("https://api.instagram.com/oauth/access_token", params, new JSONHttpClient.JSONHttpClientCallback() {
            @Override
            public void success(JSONObject response)
            {
                try
                {
                    if (response.has("access_token"))
                    {
                        String token = response.getString("access_token");
                        saveAccessToken(token);
                        mActivationCallback.success();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String error) {

            }
        });
    }


}
