package com.meamobile.photokit.instagram;

import android.util.Log;

import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.JSONHttpClient;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InstagramCollection extends Collection
{
    public static InstagramCollection RootCollection()
    {
        InstagramCollection collection = new InstagramCollection();

        collection.Source = new InstagramSource();

        return collection;
    }

    @Override
    public CollectionType type()
    {
        return CollectionType.Instagram;
    }

    @Override
    public void loadContents()
    {
        JSONHttpClient client = new JSONHttpClient();
        InstagramSource igSource = (InstagramSource) this.Source;

        client.get("https://api.instagram.com/v1/users/self/media/recent/?count=20&access_token=" + igSource.getAccessToken(), new JSONHttpClient.JSONHttpClientCallback()
        {
            @Override
            public void success(JSONObject response)
            {
                JSONObject t = response;
            }

            @Override
            public void error(String error)
            {
                Log.e("InstagramCollection", error);
            }
        });
    }
}
