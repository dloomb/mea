package com.meamobile.photokit.core;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class RemoteAsset extends Asset
{
    protected String mFullResolutionUrlString;
    protected String mThumbnailUrlString;

    public RemoteAsset(){}

    @Override
    public AssetType getType()
    {
        return AssetType.Remote;
    }

    public String getThumbnailUrlString()
    {
        return mThumbnailUrlString;
    }

    public String getFullResolutionUrlString()
    {
        return mFullResolutionUrlString;
    }
}
