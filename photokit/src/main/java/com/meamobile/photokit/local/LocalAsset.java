package com.meamobile.photokit.local;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.meamobile.photokit.core.Asset;

@SuppressLint("ParcelCreator")
public class LocalAsset extends Asset
{
    private long mId;
    private String mPath;

    public LocalAsset(long id, String title)
    {
        Title = title;
        mId = id;
    }

    public LocalAsset(Cursor cursor)
    {
        int idCol = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
        int nameCol = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        int timestampCol = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
        int pathCol = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        Title = cursor.getString(nameCol);
        TimeStamp = cursor.getLong(timestampCol);
        mId = cursor.getLong(idCol);
        mPath = cursor.getString(pathCol);
    }

    public long getContentResolverId()
    {
        return mId;
    }

    public Bitmap.CompressFormat getCompressionFormat()
    {
        return Bitmap.CompressFormat.JPEG;
    }

    @Override
    public AssetType getType()
    {
        return AssetType.Local;
    }

    @Override
    public String assetIdentifer()
    {
        return getClass().toString() + "-" + mId + "-" + Title + "-" + TimeStamp;
    }
}
