package com.meamobile.photokit.twitter;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class TwitterSource extends Source
{
    public TwitterSource()
    {
        Title = "Twitter";
        ImageResourceId = R.drawable.twitter_badge;
    }

}
