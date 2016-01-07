package com.meamobile.photokit.twitter;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class TwitterSource extends Source
{
    public TwitterSource()
    {
        mTitle = "Twitter";
        mImageResourceId = R.drawable.twitter_badge;
    }

}
