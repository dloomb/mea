package com.meamobile.photokit.core;


public class Asset
{
    static public enum AssetType
    {
        Invalid,
        Remote
    }

    public String Title;
    public long TimeStamp;
    public int Width;
    public int Height;

    public String assetIdentifer()
    {
        return getClass().toString() + "-" + Title + "-" + TimeStamp;
    }

    public AssetType getType(){
        return AssetType.Invalid;
    }
}
