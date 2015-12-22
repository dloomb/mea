package com.meamobile.photokit.core;


import com.meamobile.photokit.instagram.InstagramCollection;

import java.util.ArrayList;
import java.util.List;

public class Collection
{
    private ArrayList<Collection> mCollections;
    private ArrayList<Asset> mAssets;

    public Source Source;

    public Collection()
    {
        mCollections = new ArrayList<Collection>();
        mAssets = new ArrayList<Asset>();
    }

    public static Collection RootCollection()
    {
        Collection collection = new Collection();

        InstagramCollection instagram = InstagramCollection.RootCollection();
        collection.addCollection(instagram);

        return collection;
    }

    public int numberOfAll()
    {
        return numberOfAssets() + numberOfCollections();
    }

    //---------------------------------
    //          Collections
    //---------------------------------

    public void addCollection(Collection collection)
    {
        mCollections.add(collection);
    }

    public int numberOfCollections()
    {
        return mCollections.size();
    }

    public Collection collectionAtIndex(int index)
    {
        if (index >= 0 && index < mCollections.size())
        {
            return mCollections.get(index);
        }
        return null;
    }

    //---------------------------------
    //            Assets
    //---------------------------------

    public int numberOfAssets()
    {
        return mAssets.size();
    }

}
