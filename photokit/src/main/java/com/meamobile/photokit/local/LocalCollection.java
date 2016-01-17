package com.meamobile.photokit.local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.Source;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;

public class LocalCollection extends Collection
{
    private static String LOCAL_CAMERA_BUCKET_NAME = Environment.getExternalStorageDirectory().toString();

    private long mBucketId = 0;

    public static LocalCollection RootCollection()
    {
        LocalSource source = new LocalSource();
        LocalCollection collection = new LocalCollection(source.getTitle(), 0, source, null);

        return collection;
    }

    private LocalCollection(){};

    private LocalCollection(String name, long id, LocalSource source, Asset cover)
    {
        mTitle = name;
        mBucketId = id;
        mSource = source;
        mCoverAsset = cover;
    }

    @Override
    public CollectionType getType()
    {
        return CollectionType.Local;
    }


    @Override
    public Observable<Double> loadContents(Activity activity)
    {
        super.loadContents(activity);

        return Observable.create(new Observable.OnSubscribe<Double>() {
            @Override
            public void call(Subscriber<? super Double> subscriber) {
                mLoadSubscriber = subscriber;

                if (mBucketId == 0)
                {
                    loadBaseCollections(activity);
                }
                else
                {
                    loadAssets(activity);
                }
            }
        });
    }

    private void loadBaseCollections(Activity activity)
    {
        ContentResolver contentResolver = activity.getContentResolver();
        String[] projection = new String[]{"distinct " + ImageColumns.BUCKET_ID, ImageColumns.BUCKET_DISPLAY_NAME};

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "", null, null);

        if (cursor != null)
        {
            int count = cursor.getCount();
            int bucketIdCol = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
            int nameCol = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);

            for (int i = 0; i < count; i++)
            {
                cursor.moveToPosition(i);

                long bucketId = cursor.getLong(bucketIdCol);
                String name = cursor.getString(nameCol);

                Asset cover = coverAssetForBucketId(bucketId, contentResolver);

                LocalCollection collection = new LocalCollection(name, bucketId, (LocalSource) mSource, cover);
                addCollection(collection);
            }

            cursor.close();
        }
    }

    private void loadAssets(Activity activity)
    {
        ContentResolver contentResolver = activity.getContentResolver();
        String[] projection = { ImageColumns._ID, ImageColumns.DISPLAY_NAME, ImageColumns.DATE_TAKEN, ImageColumns.DATA };
        String selection = ImageColumns.BUCKET_ID + " = ? ";
        String[] selectionArgs = { "" + mBucketId };

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null)
        {
            int count = cursor.getCount();

            for (int i = 0; i < count; i++)
            {
                cursor.moveToPosition(i);

                LocalAsset asset = new LocalAsset(cursor);
                addAsset(asset);
            }

            cursor.close();
        }
    }

    private Asset coverAssetForBucketId(long mBucketId, ContentResolver contentResolver)
    {
        String[] projection = { "max(" + ImageColumns.DATE_TAKEN + ")", ImageColumns._ID, ImageColumns.DISPLAY_NAME };
        String selection = ImageColumns.BUCKET_ID + " = ? ";
        String[] selectionArgs = { "" + mBucketId };

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
            long imageId = cursor.getLong(cursor.getColumnIndex(ImageColumns._ID));
            String imageName = cursor.getString(cursor.getColumnIndex(ImageColumns.DISPLAY_NAME));

            return new LocalAsset(imageId, imageName);
        }

        return null;
    }





    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------

    public static final Parcelable.Creator<LocalCollection> CREATOR = new Parcelable.Creator<LocalCollection>() {
        public LocalCollection createFromParcel(Parcel in) {
            return new LocalCollection(in);
        }

        public LocalCollection[] newArray(int size) {
            return new LocalCollection[size];
        }
    };

    protected LocalCollection(Parcel in)
    {
        super(in);
    }


}
