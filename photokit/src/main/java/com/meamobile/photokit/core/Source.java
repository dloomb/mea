package com.meamobile.photokit.core;


import android.content.Context;

public abstract class Source
{
    public interface SourceActivationCallback
    {
        void success();
        void error(String error);
    }

    public String Title;
    public int ImageResourceId;

    public abstract boolean isActive();

    public abstract void activateSource(Context context, SourceActivationCallback callback);
}


