package com.meamobile.photokit.core;


public class RemoteAsset extends Asset
{
    public String FullResolutionUrlString;
    public String ThumbnailUrlString;

    @Override
    public AssetType getType()
    {
        return AssetType.Remote;
    }
}
