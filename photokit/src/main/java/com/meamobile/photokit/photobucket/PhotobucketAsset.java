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

        mTitle = (String) json.get("name");
        mTimestamp = uploaded;
        mFullResolutionUrlString = (String) json.get("url");
        mThumbnailUrlString = (String) json.get("thumb");
        mWidth = 0;
        mHeight = 0;
    }

}
