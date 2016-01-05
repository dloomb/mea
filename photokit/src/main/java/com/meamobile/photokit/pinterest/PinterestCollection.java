package com.meamobile.photokit.pinterest;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class PinterestCollection extends Collection
{
    private PinterestCollection()
    {

    }

    public static PinterestCollection RootCollection()
    {
        PinterestCollection collection = new PinterestCollection();

        collection.Source = new PinterestSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Pinterest;
    }
}
