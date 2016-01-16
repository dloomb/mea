package com.meamobile.photokit.instagram;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.meamobile.photokit.core.JSONHttpClient;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import java.util.HashMap;
import java.util.Map;

public class InstagramSource extends Source
{
    private static String TAG = "MEA.InstagramSource";

    private static String CLIENT_ID = "e0fcbb928e4048cea293347393c766e2";
    private static String CLIENT_SECRET = "475555bd27d44ddeadf7763def913f39";
    private static String INSTAGRAM_TOKEN_KEY = "com.meamobile.photokit.instagram.access_token";
    private static String INSTAGRAM_USERNAME_KEY = "com.meamobile.photokit.instagram.username";
    private static final int INSTAGRAM_AUTHENTICATOR_REQUEST_CODE = 137;

    private String mToken;

    public InstagramSource()
    {
        this.mImageResourceId = R.drawable.instagram_badge;
        this.mTitle = "Instagram";

        loadInstagramSession();
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

        AuthenticatorCallbackManager.getInstance().addListener(this);

        Intent i = new Intent(activity, AuthenticatorActivity.class);
        i.putExtra(AuthenticatorActivity.AUTH_URL, authUrl);
        i.putExtra(AuthenticatorActivity.REDIRECT_URL, redirectUrl);
        i.putExtra(AuthenticatorActivity.TITLE, "Login to Instagram");
        activity.startActivityForResult(i, INSTAGRAM_AUTHENTICATOR_REQUEST_CODE);

    }

    @Override
    public int getBrandColor()
    {
        return 0xFF3f729b;
    }

    protected void loadInstagramSession()
    {
        mToken = UserDefaults.getInstance().stringForKey(INSTAGRAM_TOKEN_KEY);
        mUsername = UserDefaults.getInstance().stringForKey(INSTAGRAM_USERNAME_KEY);
    }

    protected void saveInstagramSession()
    {
        UserDefaults.getInstance().setStringValueForKey(mToken, INSTAGRAM_TOKEN_KEY);
        UserDefaults.getInstance().setStringValueForKey(mUsername, INSTAGRAM_USERNAME_KEY);
    }

    public String getAccessToken()
    {
        return mToken;
    }


    protected void postCodeForAuthentication(String url)
    {
        String code = Uri.parse(url).getQueryParameter("code");

        String redirectUrl = "ig" + CLIENT_ID + "://authorize";

        Map<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", redirectUrl);
        params.put("code", code);

        new JSONHttpClient().post("https://api.instagram.com/oauth/access_token", params)
                .subscribe(x -> {

                    try
                    {
                        mToken = (String) x.get("access_token");
                        mUsername = (String) ((Map) x.get("user")).get("username");
                        saveInstagramSession();
                        handleSourceActivation(true, null);
                    }
                    catch (Exception e)
                    {
                        handleSourceActivation(false, e.getLocalizedMessage());
                    }

                }, error -> {
                    handleSourceActivation(false, error.getLocalizedMessage());
                });
    }




    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------

    public static final Parcelable.Creator<InstagramSource> CREATOR = new Parcelable.Creator<InstagramSource>() {
        public InstagramSource createFromParcel(Parcel in) {
            return new InstagramSource(in);
        }
        public InstagramSource[] newArray(int size) {
            return new InstagramSource[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(mToken);
    }

    protected InstagramSource(Parcel in)
    {
        super(in);

        mToken = in.readString();
    }

    ///-----------------------------------------------------------
    /// @name Authenticator Callback
    ///-----------------------------------------------------------


    @Override
    public boolean handleResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == INSTAGRAM_AUTHENTICATOR_REQUEST_CODE)
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
}
