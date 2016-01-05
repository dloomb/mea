package com.meamobile.photokit.amazon;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class AmazonSource extends Source
{

    public AmazonSource()
    {
        Title = "Amazon";
        ImageResourceId = R.drawable.amazon_badge;
    }

}
