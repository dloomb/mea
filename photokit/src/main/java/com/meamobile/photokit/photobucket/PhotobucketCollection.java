package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class PhotobucketCollection extends Collection
{

    public static PhotobucketCollection RootCollection()
    {
        PhotobucketCollection collection = new PhotobucketCollection();

        collection.Source = new PhotobucketSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

}
