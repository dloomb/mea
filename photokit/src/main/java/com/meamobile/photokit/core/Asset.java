package com.meamobile.photokit.core;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Asset implements Parcelable
{
    static public enum AssetType
    {
        Invalid,
        Remote,
        Local
    }

    public String Title;
    public long TimeStamp;
    public int Width;
    public int Height;

    public Asset(){}

    public String assetIdentifer()
    {
        return getClass().toString() + "-" + Title + "-" + TimeStamp;
    }

    public AssetType getType(){
        return AssetType.Invalid;
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
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", Title);
        data.put("timestamp", TimeStamp);
        data.put("width", Width);
        data.put("height", Height);

        String s = (new Gson()).toJson(data);
        dest.writeString(s);
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
        Map<String, Object> data = (new Gson()).fromJson(in.readString(), Map.class);
        Title = (String) data.get("title");
        TimeStamp = (long) data.get("timestamp");
        Width = (int) data.get("width");
        Height = (int) data.get("height");
    }

}
