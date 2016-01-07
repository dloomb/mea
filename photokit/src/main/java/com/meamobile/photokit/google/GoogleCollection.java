package com.meamobile.photokit.google;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class GoogleCollection extends Collection
{
    private GoogleCollection()
    {

    }

    public static GoogleCollection RootCollection()
    {
        GoogleCollection collection = new GoogleCollection();

        collection.mSource = new GoogleSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Google;
    }
}
