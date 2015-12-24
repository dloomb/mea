package com.meamobile.photokit.core;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class RemoteAsset extends Asset
{
    public String FullResolutionUrlString;
    public String ThumbnailUrlString;

    public RemoteAsset(){}

    @Override
    public AssetType getType()
    {
        return AssetType.Remote;
    }
}
