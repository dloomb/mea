package com.meamobile.photokit.core;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import java.util.HashMap;
import java.util.Map;

public class Source implements Parcelable, AuthenticatorCallbackManager.OnResultListener
{
    public static int BASE_BRAND_COLOR = 0xFFAAAAAA;

    public interface SourceActivationCallback
    {
        void success();
        void error(String error);
    }

    protected String mTitle, mUsername;
    protected int mImageResourceId;

    private SourceActivationCallback mActivationCallback;

    public Source(){}

    public boolean isActive(){return false;}


    public void activateSource(Activity activity, SourceActivationCallback callback){
        mActivationCallback = callback;
    }

    public void invalidateSource()
    {
        mUsername = null;
    }

    protected void handleSourceActivation(boolean success, String error)
    {
        if (mActivationCallback != null)
        {
            final boolean _success = success;
            final String _error = error;

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (_success || _error != null)
                    {
                        mActivationCallback.success();
                    }
                    else
                    {
                        mActivationCallback.error(_error);
                    }

                    mActivationCallback = null;
                }
            });
        }
    }



    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public String getTitle()
    {
        return mTitle;
    }

    public int getImageResource()
    {
        return mImageResourceId;
    }

    public String getUsername(){return mUsername;}

    public int getBrandColor()
    {
        return BASE_BRAND_COLOR;
    }


    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mTitle);
        dest.writeInt(mImageResourceId);
        dest.writeString(mUsername);
    }

    protected Source(Parcel in)
    {
        mTitle = in.readString();
        mImageResourceId = in.readInt();
        mUsername = in.readString();
    }

    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };



    ///-----------------------------------------------------------
    /// @name Authentication Callback
    ///-----------------------------------------------------------

    @Override
    public boolean handleResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }

    @Override
    public void onResume() {}


}


