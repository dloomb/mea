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

        collection.mSource = new TwitterSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Twitter;
    }
}
