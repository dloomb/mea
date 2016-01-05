package com.meamobile.photokit.twitter;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class TwitterCollection extends Collection
{
    private TwitterCollection()
    {

    }

    public static TwitterCollection RootCollection()
    {
        TwitterCollection collection = new TwitterCollection();

        collection.Source = new TwitterSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Twitter;
    }
}
