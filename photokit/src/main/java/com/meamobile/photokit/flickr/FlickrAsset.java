package com.meamobile.photokit.flickr;

import android.annotation.SuppressLint;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.RemoteAsset;

import java.util.Map;


@SuppressLint("ParcelCreator")
public class FlickrAsset extends RemoteAsset
{
    public FlickrAsset(Map data)
    {
        mTitle = data.get("title").toString();
        mTimestamp = ((Number) data.get("dateupload")).longValue();
        mWidth = ((Number) data.get("width_o")).intValue();
        mHeight = ((Number) data.get("height_o")).intValue();

        mThumbnailUrlString = (String) data.get("url_m");
        mFullResolutionUrlString = (String) data.get("url_o");
    }
}
