package com.meamobile.photokit.core;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Asset implements Parcelable
{
    static public enum AssetType
    {
        Invalid,
        Remote,
        Local
    }

    protected String mTitle;
    protected long mTimestamp;
    protected int mWidth;
    protected int mHeight;

    public Asset(){}

    public String getAssetIdentifier()
    {
        return getClass().toString() + "-" + mTitle + "-" + mTimestamp;
    }

    public AssetType getType(){
        return AssetType.Invalid;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean equals = super.equals(o);

        if (!equals && o instanceof Asset)
        {
            Asset a = (Asset) o;
            equals = a.getAssetIdentifier() == getAssetIdentifier();
        }

        return equals;
    }




    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
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

    }

    public static final Parcelable.Creator<Asset> CREATOR = new Parcelable.Creator<Asset>() {
        public Asset createFromParcel(Parcel in) {
            return new Asset(in);
        }

        public Asset[] newArray(int size) {
            return new Asset[size];
        }
    };

    private Asset(Parcel in)
    {

    }

}
