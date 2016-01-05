package com.meamobile.photokit.dropbox;


import android.annotation.SuppressLint;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.instagram.InstagramSource;

@SuppressLint("ParcelCreator")
public class DropboxCollection extends Collection
{
    private DropboxCollection()
    {

    }

    public static DropboxCollection RootCollection()
    {
        DropboxCollection collection = new DropboxCollection();

        collection.Source = new DropboxSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Dropbox;
    }
}
