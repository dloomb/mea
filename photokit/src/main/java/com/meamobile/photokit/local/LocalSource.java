package com.meamobile.photokit.local;


import android.annotation.SuppressLint;
import android.content.Context;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class LocalSource extends Source
{

    public LocalSource()
    {
        this.Title = "Local Photos";
        this.ImageResourceId = R.mipmap.local_badge;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public void activateSource(Context context, SourceActivationCallback callback)
    {

    }
}
