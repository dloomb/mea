package com.meamobile.photokit.pinterest;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class PinterestSource extends Source
{
    public PinterestSource()
    {
        Title = "Pinterest";
        ImageResourceId = R.drawable.pinterest_badge;
    }
}
