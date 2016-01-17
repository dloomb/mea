package com.meamobile.photokit.instagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.JSONHttpClient;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

@SuppressLint("ParcelCreator")
public class InstagramCollection extends Collection
{
    static private String TAG = "Collection.Instagram";

    public InstagramCollection(){}

    public static InstagramCollection RootCollection()
    {
        InstagramCollection collection = new InstagramCollection();

        collection.mSource = new InstagramSource();
        collection.mTitle = collection.mSource.getTitle();

        return collection;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Instagram;
    }

    @Override
    public Observable<Double> loadContents(Activity activity)
    {
        super.loadContents(activity);

        return Observable.create(new Observable.OnSubscribe<Double>() {
            @Override
            public void call(Subscriber<? super Double> subscriber) {

                mLoadSubscriber = subscriber;

                InstagramSource igSource = (InstagramSource) mSource;
                String url = "https://api.instagram.com/v1/users/self/media/recent/?count=20&access_token=" + igSource.getAccessToken();

                loadAssetsWithUrl(url);

            }
        });
    }

    protected void loadAssetsWithUrl(String url)
    {
        JSONHttpClient client = new JSONHttpClient();

        client.get(url, null)
                .subscribe(
                        x -> {
                            List data = (List) x.get("data");
                            if (data != null)
                            {
                                int size = data.size();
                                for (int i = 0; i < size; i++)
                                {
                                    Map json = (Map) data.get(i);
                                    InstagramAsset asset = new InstagramAsset(json);
                                    addAsset(asset);
                                }

                                Log.d(TAG, "Instagram Assets Added, Count = " + numberOfAssets());
                            }

                            Map pagination = (Map) x.get("pagination");
                            if (pagination != null)
                            {
                                String next = (String) pagination.get("next_url");
                                if (next != null && next.contains("user"))
                                {
                                    loadAssetsWithUrl(next);
                                }
                            }
                        },
                        error -> {
                            Log.e(TAG, error.getLocalizedMessage());
                        }
                );

    }



    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------

    public static final Parcelable.Creator<InstagramCollection> CREATOR = new Parcelable.Creator<InstagramCollection>() {
        public InstagramCollection createFromParcel(Parcel in) {
            return new InstagramCollection(in);
        }
        public InstagramCollection[] newArray(int size) {
            return new InstagramCollection[size];
        }
    };

    protected InstagramCollection(Parcel in)
    {
        super(in);
    }

}
