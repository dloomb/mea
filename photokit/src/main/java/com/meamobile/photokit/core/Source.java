package com.meamobile.photokit.core;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Source implements Parcelable
{
    public interface SourceActivationCallback
    {
        void success();
        void error(String error);
    }

    public String Title;
    public int ImageResourceId;
    private SourceActivationCallback mActivationCallback;

    public Source(){}

    public boolean isActive(){return false;}

    public void activateSource(Activity activity, SourceActivationCallback callback){
        mActivationCallback = callback;
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
                }
            });
        }
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
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", Title);
        data.put("image_resource_id", ImageResourceId);

        String s = (new Gson()).toJson(data);
        dest.writeString(s);
    }

    private Source(Parcel in)
    {
        Map<String, Object> data = (new Gson()).fromJson(in.readString(), Map.class);
        Title = (String) data.get("title");
        ImageResourceId = (int) data.get("image_resource_id");
    }

    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };




}


