package com.meamobile.photokit.flickr;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class FlickrSource extends Source
{

    public FlickrSource()
    {
        mTitle = "Flickr";
        mImageResourceId = R.drawable.flickr_badge;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public int getBrandColor()
    {
        return 0xFFff0084;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);



    }
}
