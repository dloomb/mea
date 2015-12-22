package com.meamobile.photokit.instagram;

import com.meamobile.photokit.core.Collection;

public class InstagramCollection extends Collection
{
    public static InstagramCollection RootCollection()
    {
        InstagramCollection collection = new InstagramCollection();

        collection.Source = new InstagramSource();

        return collection;
    }
}
