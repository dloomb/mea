package com.meamobile.photokit.facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.core.UserDefaults;

import org.json.JSONObject;

import java.util.Arrays;

@SuppressLint("ParcelCreator")
public class FacebookSource extends Source
{
    private static final String FACEBOOK_USERNAME_KEY = "com.meamobile.photokit.facebook.username";

    private AccessTokenTracker mFacebookAccessTokenTracker;
    private ProfileTracker mFacebookProfileTracker;
    private AccessToken mFacebookAccessToken;

    public FacebookSource()
    {
        this.mTitle = "Facebook";
        this.mImageResourceId = R.drawable.facebook_badge;

        mFacebookAccessToken = AccessToken.getCurrentAccessToken();
        initilizeFacebookAccessTokenTracking();
    }

    @Override
    public boolean isActive() {
        return mFacebookAccessToken != null;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends"));
    }

    @Override
    public void invalidateSource()
    {
        super.invalidateSource();

        mFacebookAccessToken = null;

        LoginManager.getInstance().logOut();
    }

    @Override
    public int getBrandColor()
    {
        return 0xFF3b5998;
    }

    ///-----------------------------------------------------------
    /// @name Facebook Tokens
    ///-----------------------------------------------------------
    public void initilizeFacebookAccessTokenTracking()
    {
        mFacebookAccessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                mFacebookAccessToken = currentAccessToken;

                if (mFacebookAccessToken != null)
                {
                    handleSourceActivation(true, null);
                }
                else
                {
                    handleSourceActivation(false, "Facebook Token not found");
                }
            }
        };

        mFacebookProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
            {
                mUsername = (currentProfile == null) ? null : currentProfile.getName();
            }
        };
    }
}
