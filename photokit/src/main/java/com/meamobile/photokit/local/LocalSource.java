package com.meamobile.photokit.local;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class LocalSource extends Source
{

    public LocalSource()
    {
        this.Title = "Local Photos";
        this.ImageResourceId = R.drawable.local_badge;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }
}
