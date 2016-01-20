package com.meamobile.printicular_sdk.core.models;

public class Image extends Model
{
    private User mUser;
    private String
            mExternalUrl,
            mKey,
            mFilename,
            mChecksum,
            mDeviceToken;
    private long mBytesize;
    private int mWidth, mHeight;


    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }
}
