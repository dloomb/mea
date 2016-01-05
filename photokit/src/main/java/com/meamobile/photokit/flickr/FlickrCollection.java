package com.meamobile.photokit.flickr;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class FlickrCollection extends Collection
{
    private FlickrCollection()
    {

    }

    public static FlickrCollection RootCollection()
    {
        FlickrCollection collection = new FlickrCollection();

        collection.Source = new FlickrSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Flickr;
    }
}
