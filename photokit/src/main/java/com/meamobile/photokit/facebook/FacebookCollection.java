package com.meamobile.photokit.facebook;


import android.annotation.SuppressLint;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.photokit.core.Collection;

import java.util.List;
import java.util.Map;

@SuppressLint("ParcelCreator")
public class FacebookCollection extends Collection
{
    private static String FACEBOOK_TAGGED_PHOTOS_ALBUM = "com.meamobile.photokit.facebook.tagged_photos_album";


    private String mFacebookAlbumId;

    public static FacebookCollection RootCollection()
    {
        FacebookCollection collection = new FacebookCollection();

        collection.Source = new FacebookSource();
        collection.Title = collection.Source.Title;

        return collection;
    }

    public FacebookCollection(){}

    public FacebookCollection(Map<String, Object> json)
    {
        mFacebookAlbumId = (String) json.get("id");
        Title = (String) json.get("name");
    }


    @Override
    public void loadContents()
    {

        if (mFacebookAlbumId == null)
        {
            loadGraphPath("me/albums", "id,name,count,picture");
        }
        else if (mFacebookAlbumId == FACEBOOK_TAGGED_PHOTOS_ALBUM)
        {

        }
        else
        {
            loadGraphPath(mFacebookAlbumId + "/photos", "id,source,picture,width,height,created_time,images");
        }

    }



    protected void loadGraphPath(String path, String fields)
    {
        Bundle params = new Bundle();
        params.putString("fields", fields);

        AccessToken token = AccessToken.getCurrentAccessToken();

        new GraphRequest(
                token,
                path,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {
                        FacebookRequestError error = response.getError();
                        if (error != null)
                        {
                            // TODO: 24/12/15 Handle error
                            return;
                        }

                        Map<String, Object> object = new Gson().fromJson(response.getRawResponse(), new TypeToken<Map<String, Object>>(){}.getType());

                        List data = (List) object.get("data");
                        if (data != null)
                        {
                            int size = data.size();
                            for (int i = 0; i < size; i++)
                            {
                                Map json = (Map) data.get(i);

                                if (mFacebookAlbumId == null)
                                {
                                    FacebookCollection collection = new FacebookCollection(json);
                                    collection.Source = Source;
                                    addCollection(collection);
                                }
                                else
                                {
                                    FacebookAsset asset = new FacebookAsset(json);
                                    addAsset(asset);
                                }
                            }

                        }

                        Map pagination = (Map) object.get("pagination");
                        if (pagination != null)
                        {

                        }


                    }
                }
        ).executeAsync();
    }

    protected void loadImagesWithGraphPath(String path)
    {

    }


}
