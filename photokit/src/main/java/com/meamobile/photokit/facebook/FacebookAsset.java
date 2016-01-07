package com.meamobile.photokit.facebook;

import android.annotation.SuppressLint;

import com.meamobile.photokit.core.RemoteAsset;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class FacebookAsset extends RemoteAsset
{

    public FacebookAsset(Map<String, Object> json)
    {
        String created = (String) json.get("created_time");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        long timestamp = 0;
        try
        {
            timestamp = dateFormat.parse(created).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        ArrayList<Map<String, Object>> images = (ArrayList<Map<String, Object>>) json.get("images");
        Collections.sort(images, new Comparator<Map<String, Object>>()
        {
            @Override
            public int compare(Map<String, Object> lhs, Map<String, Object> rhs)
            {
                Double lhsWidth = (Double) lhs.get("width");
                Double rhsWidth = (Double) rhs.get("width");
                return lhsWidth.compareTo(rhsWidth);
            }
        });

        int count = images.size();
        Map<String, Object> source = images.get(count - 1);
        Map<String, Object> thumb = images.get((count >= 3) ? 2 : count - 1);

        Number width = (Number) source.get("width");
        Number height = (Number) source.get("height");

        mTitle = (String) json.get("id");
        mTimestamp = timestamp;
        FullResolutionUrlString = (String) source.get("source");
        ThumbnailUrlString = (String) thumb.get("source");
        mWidth = width.intValue();
        mHeight = height.intValue();
    }

}
