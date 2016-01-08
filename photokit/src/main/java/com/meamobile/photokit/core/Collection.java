package com.meamobile.photokit.core;



import com.google.gson.Gson;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public class Collection implements Parcelable
{
    public enum CollectionType
    {
        Root        (1 << 0),
        Local       (1 << 1),
        Instagram   (1 << 2),
        Facebook    (1 << 3),
        Dropbox     (1 << 4),
        Flickr      (1 << 5),
        Google      (1 << 6),
        Twitter     (1 << 7),
        DeviantArt  (1 << 8),
        Pinterest   (1 << 9),
        OneDrive    (1 << 10),
        Amazon      (1 << 11),
        Photobucket (1 << 12);

        CollectionType(int val)
        {
            this.value = val;
        }

        public static final EnumSet<CollectionType> All = EnumSet.allOf(CollectionType.class);

        public int value;
    }

    public interface CollectionObserver
    {
        void collectionDidAddAssetAtIndex(Collection collection, Asset added, int index);
        void collectionDidAddCollectionAtIndex(Collection collection, Collection added, int index);
        void collectionRefresh(Collection collection);
    }

    private Date mLastLoaded;

    private ArrayList<Collection> mCollections;
    private ArrayList<Asset> mAssets;
    private CollectionObserver mObserver;

    protected Source mSource;
    protected String mTitle;
    protected Asset mCoverAsset;

    public Collection()
    {
        mCollections = new ArrayList<Collection>();
        mAssets = new ArrayList<Asset>();
    }



    public CollectionType getType()
    {
        return CollectionType.Root;
    }

    public Source getSource() { return mSource; }

    public String getTitle() { return mTitle; }

    public Asset getCoverAsset() { return mCoverAsset; }

    public String collectionIdentifier () {
        return getClass().getName() + mTitle;
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
        int i = mCollections.size();
        mCollections.add(collection);

        if (mObserver != null)
        {
            mObserver.collectionDidAddCollectionAtIndex(this, collection, i);
        }
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
        int i = mAssets.size();
        mAssets.add(asset);

        if (mObserver != null)
        {
            mObserver.collectionDidAddAssetAtIndex(this, asset, i);
        }
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
    //          ContentLoading
    //---------------------------------

    public void loadContents(Activity activity){

        if (getType() != CollectionType.Root)
        {
//            Date now = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(now);
//            calendar.add(Calendar.HOUR, 1);
//
//            if (mLastLoaded != null &&  now.before(mLastLoaded))
//            {
//                mLastLoaded = now;
                mCollections = new ArrayList<Collection>();
                mAssets = new ArrayList<Asset>();
//            }
        }
    }


    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Asset[] assets = new Asset[mAssets.size()];
        assets = mAssets.toArray(assets);
        dest.writeParcelableArray(assets, flags);

        Collection[] collections = new Collection[mCollections.size()];
        collections = mCollections.toArray(collections);
        dest.writeParcelableArray(collections, flags);

        dest.writeParcelable(mSource, flags);
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    protected Collection(Parcel in)
    {
        Parcelable[] assets = in.readParcelableArray(Asset.class.getClassLoader());
        mAssets = new ArrayList(Arrays.asList(assets));

        Parcelable[] collections = in.readParcelableArray(Collection.class.getClassLoader());
        mCollections = new ArrayList(Arrays.asList(collections));

        mSource = in.readParcelable(Source.class.getClassLoader());
    }

}
