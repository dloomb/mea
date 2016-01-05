package com.meamobile.photokit.one_drive;


import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Collection;

@SuppressLint("ParcelCreator")
public class OneDriveCollection extends Collection
{
    private OneDriveCollection()
    {

    }

    public static OneDriveCollection RootCollection()
    {
        OneDriveCollection collection = new OneDriveCollection();

        collection.Source = new OneDriveSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.OneDrive;
    }
}
