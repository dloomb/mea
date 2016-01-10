package com.meamobile.photokit.core;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;

import com.meamobile.photokit.local.LocalAsset;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
        final String _urlString = urlString;
        final File _output = output;
        final CachingImageManagerRequestCallback _callback = callback;

        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Log.d("ImageManager", "download begining");
                Log.d("ImageManager", "download url:" + _urlString);
                Log.d("ImageManager", "downloaded file name:" + _output.getAbsolutePath());

                URL url = null;
                try {
                    url = new URL(_urlString);
                    URLConnection ucon = url.openConnection();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                    }

                    FileOutputStream fos = new FileOutputStream(_output);
                    fos.write(baf.toByteArray());
                    fos.close();
                    Log.d("ImageManager", "download ready in"
                            + ((System.currentTimeMillis() - startTime) / 1000)
                            + " sec");
                    _callback.success(_output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
