package com.meamobile.photokit.google;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class GoogleSource extends Source
{

    public GoogleSource()
    {
        Title = "Google";
        ImageResourceId = R.drawable.google_badge;
    }


}
