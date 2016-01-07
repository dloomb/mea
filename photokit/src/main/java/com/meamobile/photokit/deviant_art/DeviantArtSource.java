package com.meamobile.photokit.deviant_art;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class DeviantArtSource extends Source
{
    public DeviantArtSource()
    {
        mTitle = "Deviant Art";
        mImageResourceId = R.drawable.deviantart_badge;
    }
}
