package com.meamobile.photokit.core;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.meamobile.photokit.local.LocalAsset;

import java.io.File;
import java.io.FileOutputStream;

import rx.Observable;

public class CachingImageManager
{
    public interface CachingImageManagerRequestCallback
    {
        void success(File path);
        void error(String error);
    }

    private Context mContext;

    public CachingImageManager(Context context)
    {
        mContext = context;
    }

    public Observable<File> requestThumbnailForAsset(Asset asset)
    {
        File file = new File(mContext.getCacheDir(), asset.getAssetIdentifier() + "-thumbnail");
        if (file.exists())
        {
            return Observable.just(file);
        }

        switch (asset.getType())
        {
            case Local:
                return loadLocalAssetThumbnailWithContentResolver((LocalAsset) asset, file);
            case Remote:
                return downloadImageFromUrl(((RemoteAsset)asset).getThumbnailUrlString(), file);
        }

        return Observable.error(new RuntimeException("Invalid Asset Type given"));
    }

    public Observable<File> requestFullResolutionForAsset(Asset asset)
    {
        File file = new File(mContext.getCacheDir(), asset.getAssetIdentifier() + "-fullresolution");
        if (file.exists())
        {
            return Observable.just(file);
        }

        switch (asset.getType())
        {
            case Remote:
                return downloadImageFromUrl(((RemoteAsset)asset).getFullResolutionUrlString(), file);
        }

        return Observable.error(new RuntimeException("Invalid Asset Type given"));
    }



    protected Observable<File> downloadImageFromUrl(String urlString, File output)
    {
        JSONHttpClient client = new JSONHttpClient();
        return client.getFile(urlString, output);
    }


    protected Observable<File> loadLocalAssetThumbnailWithContentResolver(LocalAsset asset, File file)
    {
        ContentResolver resolver = mContext.getContentResolver();

        Bitmap thumb = null;
        FileOutputStream out = null;

        try
        {
            thumb = MediaStore.Images.Thumbnails
                    .getThumbnail(
                            resolver,
                            asset.getContentResolverId(),
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null);

            out = new FileOutputStream(file);
            thumb.compress(asset.getCompressionFormat(), 100, out);
            out.close();

            return Observable.just(file);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }

            return Observable.error(e);

        }
    }

    protected Observable<File> loadLocalAssetFullResolutionWithContentResolver(LocalAsset asset, File file) {

        ContentResolver resolver = mContext.getContentResolver();

        return Observable.empty();
    }

}
