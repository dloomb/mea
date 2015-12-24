package com.meamobile.photokit.local;

import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;


@SuppressLint("ParcelCreator")
public class LocalCollection extends Collection
{

    public static LocalCollection RootCollection()
    {
        LocalCollection collection = new LocalCollection();

        collection.Source = new LocalSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

}
