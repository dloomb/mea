package com.meamobile.photokit.core;



import com.google.gson.Gson;
import com.meamobile.photokit.instagram.InstagramCollection;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Collection implements Parcelable
{
    public enum CollectionType
    {
        Instagram(1),
        Facebook(2),
        Dropbox(2),
        All(Integer.MAX_VALUE);

        CollectionType(int val)
        {
            this.value = val;
        }

        public int value;
    }

    public interface CollectionObserver
    {
        public void collectionDidAddAsset(Collection collection, Asset added);
        public void collectionDidAddCollection(Collection collection, Collection added);
        public void collectionRefresh(Collection collection);
    }

    private ArrayList<Collection> mCollections;
    private ArrayList<Asset> mAssets;
    private CollectionObserver mObserver;

    public Source Source;
    public String Title;
    public String ThumbnailPath;

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

    public CollectionType type()
    {
        return null;
    }

    public void setCollectionObserver(CollectionObserver observer)
    {
        mObserver = observer;
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

    public void addAsset(Asset asset) {
        mAssets.add(asset);
        mObserver.collectionDidAddAsset(this, asset);
    }

    public Asset assetAtIndex(int index)
    {
        if (index >= 0 && index < mAssets.size())
        {
            return mAssets.get(index);
        }
        return null;
    }

    public int numberOfAssets()
    {
        return mAssets.size();
    }


    //---------------------------------
    //          Loading
    //---------------------------------

    public void loadContents(){};


    //---------------------------------
    //          Parcelable
    //---------------------------------


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("assets", mAssets);
        data.put("collections", mCollections);
        data.put("title", Title);
        data.put("source", Source);
        data.put("thumbnail_path", ThumbnailPath);

        String s = (new Gson()).toJson(data);
        dest.writeString(s);
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    private Collection(Parcel in)
    {
        Map<String, Object> data = (new Gson()).fromJson(in.readString(), Map.class);
        mAssets = (ArrayList<Asset>) data.get("assets");
        mCollections = (ArrayList<Collection>) data.get("collections");
        Title = (String) data.get("title");
        Source = (com.meamobile.photokit.core.Source) data.get("source");
        ThumbnailPath = (String) data.get("thumbnail_path");
    }

}
