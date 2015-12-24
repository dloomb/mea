package com.meamobile.photokit.facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

import java.util.Arrays;

@SuppressLint("ParcelCreator")
public class FacebookSource extends Source
{
//    CallbackManager mFBCallbackManager;
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private AccessToken mFacebookAccessToken;
    private SourceActivationCallback mActivationCallback;

    public FacebookSource()
    {
        this.Title = "Facebook";
        this.ImageResourceId = R.mipmap.facebook_badge;

        mFacebookAccessToken = AccessToken.getCurrentAccessToken();
        initilizeFacebookAccessTokenTracking();
    }

    @Override
    public boolean isActive() {
        return mFacebookAccessToken != null;
    }

    @Override
    public void activateSource(Context context, final SourceActivationCallback callback)
    {
        mActivationCallback = callback;
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "user_friends"));
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

                if (mActivationCallback != null)
                {
                    if (currentAccessToken != null)
                    {
                        mActivationCallback.success();
                    }
                    mActivationCallback.error("No Access Token");
                }
            }
        };
    }


}
