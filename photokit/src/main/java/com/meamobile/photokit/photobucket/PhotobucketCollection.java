package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.photokit.core.Collection;

import org.json.JSONObject;
import org.json.XML;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;


@SuppressLint("ParcelCreator")
public class PhotobucketCollection extends Collection
{
    private PhotobucketSource mPBSource;
    private String mPhotobucketAlbumId;

    public static PhotobucketCollection RootCollection()
    {
        PhotobucketCollection collection = new PhotobucketCollection();

        collection.mSource = new PhotobucketSource();
        collection.mTitle = collection.mSource.getTitle();
        collection.mPBSource = (PhotobucketSource) collection.mSource;

        return collection;
    }

    private PhotobucketCollection(){}

    private PhotobucketCollection(Map json, PhotobucketSource source) throws UnsupportedEncodingException
    {
        String parent = (String) json.get("parent");

        mTitle = (String) json.get("name");
        mSource = source;

        mPhotobucketAlbumId = URLEncoder.encode(parent + "/" + mTitle, "UTF-8");
        mPBSource = source;

    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Photobucket;
    }


    @Override
    public void call(Subscriber<? super Object> subscriber)
    {
        super.call(subscriber);

        OAuthService service = mPBSource.getOAuthService();

        mPhotobucketAlbumId = (mPhotobucketAlbumId == null) ? mPBSource.getUsername() : mPhotobucketAlbumId;

        String url = "http://api.photobucket.com/album/" + mPhotobucketAlbumId;

        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(mPBSource.getOAuthToken(), request);
        String complete = request.getCompleteUrl();
        try
        {
            Response response = request.send();
            String body = response.getBody();
            JSONObject json = XML.toJSONObject(body);
            Map<String, Object> object = new Gson().fromJson(json.toString(), new TypeToken<Map<String, Object>>()
            {
            }.getType());

            Map res = (Map) object.get("response");
            Map content = (Map) res.get("content");
            Map root = (Map) content.get("album");

            final Object albums = root.get("album");
            if (albums != null)
            {
                List<Map> iAlbums = (albums instanceof List) ? (List) albums : new ArrayList(){{add(albums);}};

                for (Map album : iAlbums)
                {
                    album.put("parent", mPhotobucketAlbumId);
                    PhotobucketCollection collection = new PhotobucketCollection(album, mPBSource);
                    addCollection(collection);
                }
            }

            final Object media = (List) root.get("media");
            if (media != null)
            {
                List<Map> iMedia = (media instanceof List) ? (List) media : new ArrayList(){{add(media);}};
                for (Map image : iMedia)
                {
                    PhotobucketAsset asset = new PhotobucketAsset(image);
                    addAsset(asset);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            mLoadSubscriber.onError(e);
        }
    }
}
