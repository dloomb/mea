package com.meamobile.photokit.google;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class GoogleSource extends Source
{

    public GoogleSource()
    {
        mTitle = "Google";
        mImageResourceId = R.drawable.google_badge;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public int getBrandColor()
    {
        return 0xFFdc4e41;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);


    }
}
