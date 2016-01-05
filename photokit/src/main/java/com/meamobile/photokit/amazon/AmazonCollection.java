package com.meamobile.photokit.amazon;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class AmazonCollection extends Collection
{
    private AmazonCollection()
    {

    }

    public static AmazonCollection RootCollection()
    {
        AmazonCollection collection = new AmazonCollection();

        collection.Source = new AmazonSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Amazon;
    }
}
