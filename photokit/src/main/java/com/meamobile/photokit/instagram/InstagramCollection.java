package com.meamobile.photokit.instagram;

import android.util.Log;

import com.meamobile.photokit.core.Collection;

public class InstagramCollection extends Collection
{
    public static InstagramCollection RootCollection()
    {
        InstagramCollection collection = new InstagramCollection();

        collection.Source = new InstagramSource();

        return collection;
    }

    @Override
    public CollectionType type()
    {
        return CollectionType.Instagram;
    }

    @Override
    public void loadContents()
    {
        Log.d("Collection", "Hello");
    }
}
