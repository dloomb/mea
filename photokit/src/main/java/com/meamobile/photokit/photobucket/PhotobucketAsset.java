package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;

import com.meamobile.photokit.core.RemoteAsset;

import java.util.Map;

@SuppressLint("ParcelCreator")
public class PhotobucketAsset extends RemoteAsset
{

    public PhotobucketAsset(Map json)
    {
        int uploaded = ((Number) json.get("uploaddate")).intValue();

        Title = (String) json.get("name");
        TimeStamp = uploaded;
        FullResolutionUrlString = (String) json.get("url");
        ThumbnailUrlString = (String) json.get("thumb");
        Width = 0;
        Height = 0;
    }

}
