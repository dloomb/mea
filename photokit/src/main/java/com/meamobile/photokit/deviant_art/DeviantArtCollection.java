package com.meamobile.photokit.deviant_art;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class DeviantArtCollection extends Collection
{
    private DeviantArtCollection()
    {

    }

    public static DeviantArtCollection RootCollection()
    {
        DeviantArtCollection collection = new DeviantArtCollection();

        collection.mSource = new DeviantArtSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }
}
