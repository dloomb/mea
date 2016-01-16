package com.meamobile.photokit.core;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.meamobile.photokit.local.LocalAsset;

import java.io.File;
import java.io.FileOutputStream;

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

    public void requestThumbnailForAsset(Asset asset, CachingImageManagerRequestCallback callback)
    {
        File file = new File(mContext.getCacheDir(), asset.getAssetIdentifier() + "-thumbnail");
        if (file.exists())
        {
            callback.success(file);
            return;
        }

        switch (asset.getType())
        {
            case Local:
                loadLocalAssetThumbnailWithContentResolver((LocalAsset) asset, file, callback);
                break;
            case Remote:
                downloadImageFromUrl(((RemoteAsset)asset).getThumbnailUrlString(), file, callback);
                break;
        }


    }

    public void requestFullResolutionForAsset(Asset asset, CachingImageManagerRequestCallback callback)
    {
        File file = new File(mContext.getCacheDir(), asset.getAssetIdentifier() + "-fullresolution");
        if (file.exists())
        {
            callback.success(file);
            return;
        }

        switch (asset.getType())
        {
            case Remote:
                downloadImageFromUrl(((RemoteAsset)asset).getFullResolutionUrlString(), file, callback);
                break;
        }
    }



    protected void downloadImageFromUrl(String urlString, File output, CachingImageManagerRequestCallback callback)
    {
        JSONHttpClient client = new JSONHttpClient();
        client.getFile(urlString, output)
                .subscribe(callback::success,
                error -> {
                    callback.error(error.getLocalizedMessage());
                });
    }


    protected void loadLocalAssetThumbnailWithContentResolver(LocalAsset asset, File file, CachingImageManagerRequestCallback callback)
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
        }
        catch(Exception e)
        {
            e.printStackTrace();
            callback.error(e.getLocalizedMessage());

            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }
    }

}
