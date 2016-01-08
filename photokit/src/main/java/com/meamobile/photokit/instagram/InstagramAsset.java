package com.meamobile.photokit.instagram;

import com.meamobile.photokit.core.RemoteAsset;

import java.util.Map;


public class InstagramAsset extends RemoteAsset
{
    public InstagramAsset(Map<String, Object> json)
    {
        Long created = Long.parseLong((String)json.get("created_time"));

        Map<String, Object> images = (Map) json.get("images");
        Map<String, Object> standard = (Map) images.get("standard_resolution");
        Map<String, Object> thumb = (Map) images.get("thumbnail");

        Number width = (Number) standard.get("width");
        Number height = (Number) standard.get("height");

        mTitle = (String) json.get("id");
        mTimestamp = created.longValue();
        mFullResolutionUrlString = (String) standard.get("url");
        mThumbnailUrlString = (String) thumb.get("url");;
        mWidth = width.intValue();
        mHeight = height.intValue();
    }
}
