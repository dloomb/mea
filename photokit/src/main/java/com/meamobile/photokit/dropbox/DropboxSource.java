package com.meamobile.photokit.dropbox;

import android.annotation.SuppressLint;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

@SuppressLint("ParcelCreator")
public class DropboxSource extends Source
{

    public DropboxSource()
    {
        Title = "Dropbox";
        ImageResourceId = R.drawable.dropbox_badge;
    }

}
