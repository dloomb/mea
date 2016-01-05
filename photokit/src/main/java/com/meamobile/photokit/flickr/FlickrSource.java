package com.meamobile.photokit.flickr;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class FlickrSource extends Source
{

    public FlickrSource()
    {
        Title = "Flickr";
        ImageResourceId = R.drawable.flickr_badge;
    }


}
