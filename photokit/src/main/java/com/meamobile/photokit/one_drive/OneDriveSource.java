package com.meamobile.photokit.one_drive;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class OneDriveSource extends Source
{
    public OneDriveSource()
    {
        Title = "OneDrive";
        ImageResourceId = R.drawable.onedrive_badge;
    }
}
