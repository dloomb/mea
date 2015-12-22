package com.meamobile.photokit.core;


public class Asset
{
    public String Title;
    public long TimeStamp;
    public int Width;
    public int Height;

    public String assetIdentifer()
    {
        return getClass().toString() + "-" + Title + "-" + TimeStamp;
    }
}
