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

        collection.Source = new DeviantArtSource();
        collection.Title = collection.Source.Title;

        return collection;
    }
}
