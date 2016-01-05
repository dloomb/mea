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

        collection.Source = new GoogleSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Google;
    }
}
