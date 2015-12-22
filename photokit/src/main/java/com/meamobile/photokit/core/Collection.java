package com.meamobile.photokit.core;



import com.google.gson.Gson;
import com.meamobile.photokit.instagram.InstagramCollection;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;


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

    private ArrayList<Collection> mCollections;
    private ArrayList<Asset> mAssets;

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
        String s = (new Gson()).toJson(this);
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
        Collection col = (new Gson()).fromJson(in.readString(), Collection.class);
        mAssets = col.mAssets;
        mCollections = col.mCollections;
        Title = col.Title;
        Source = col.Source;
        ThumbnailPath = col.ThumbnailPath;
    }

}
