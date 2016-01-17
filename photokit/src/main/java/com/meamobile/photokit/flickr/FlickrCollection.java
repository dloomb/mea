package com.meamobile.photokit.flickr;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.JSONHttpClient;

import org.json.JSONObject;
import org.json.XML;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

@SuppressLint("ParcelCreator")
public class FlickrCollection extends Collection
{

    private FlickrCollection()
    {

    }

    public static FlickrCollection RootCollection()
    {
        FlickrCollection collection = new FlickrCollection();

        collection.mSource = new FlickrSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Flickr;
    }

    @Override
    public Observable<Double> loadContents(Activity activity) {
        super.loadContents(activity);

        return Observable.create(new Observable.OnSubscribe<Double>() {
            @Override
            public void call(Subscriber<? super Double> subscriber) {

                mLoadSubscriber = subscriber;
                loadImagesAtPage(0);

            }
        });
    }

    protected void loadImagesAtPage(int page)
    {
        FlickrSource source = (FlickrSource) mSource;

        OAuthRequest request = source.newRequestWithVerb(Verb.GET);
        request.addQuerystringParameter("method", "flickr.people.getPhotos");
        request.addQuerystringParameter("user_id", "me");
        request.addQuerystringParameter("extras", "url_o,url_m,date_upload");
        request.addQuerystringParameter("page", "" + page);
        source.signRequest(request);

        Response response = request.send();
        String body = response.getBody();

        try
        {
            JSONObject json = XML.toJSONObject(body);
            Map<String, Object> object = new Gson().fromJson(json.toString(), new TypeToken<Map<String, Object>>()
            {
            }.getType());

            Map rsp = (Map) object.get("rsp");
            Map photos = (Map) rsp.get("photos");
            List<Map> photo = (List) photos.get("photo");

            int pages = ((Number) photos.get("pages")).intValue();

            for (Map data : photo)
            {
                FlickrAsset asset = new FlickrAsset(data);
                addAsset(asset);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
