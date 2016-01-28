package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class Image extends DeviceTokenableModel
{
    private User mUser;

    private String
            mKey,
            mChecksum,
            mFilename,
            mExternalUrl;

    private long mBytesize;

    private int
            mWidth,
            mHeight;

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null) {
            mExternalUrl =  (String) safeParse(attributes.get("external_url"), ClassType.STRING);
            mKey =          (String) safeParse(attributes.get("key"), ClassType.STRING);
            mFilename =     (String) safeParse(attributes.get("filename"), ClassType.STRING);
            mChecksum =     (String) safeParse(attributes.get("checksum"), ClassType.STRING);
            mBytesize =     (long)   safeParse(attributes.get("bytesize"), ClassType.LONG);
            mWidth =        (int)    safeParse(attributes.get("pixel_width"), ClassType.INTEGER);
            mHeight =       (int)    safeParse(attributes.get("pixel_height"), ClassType.INTEGER);
        }
    }

    @Override
    public Map<String, Map> evaporate() {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = (Map<String, Object>) data.get("data").get("attributes");
        attributes.put("external_url", mExternalUrl);
        attributes.put("key", mKey);
        attributes.put("filename", mFilename);
        attributes.put("checksum", mChecksum);
        attributes.put("bytesize", mBytesize);
        attributes.put("pixel_width", mWidth);
        attributes.put("pixel_height", mHeight);

        return data;
    }

    @Override
    public void update(Model model)
    {
        super.update(model);
        Image imageModel = (Image) model;

        mExternalUrl = (String) isSetOr(imageModel.getExternalUrl(), mExternalUrl);
        mKey = (String) isSetOr(imageModel.getKey(), mKey);
        mFilename = (String) isSetOr(imageModel.getFilename(), mFilename);
        mChecksum = (String) isSetOr(imageModel.getChecksum(), mChecksum);
        mBytesize = isSetOr(imageModel.getBytesize(), mBytesize);
        mWidth = isSetOr(imageModel.getWidth(), mWidth);
        mHeight = isSetOr(imageModel.getHeight(), mHeight);
    }

    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------


    @Override
    public String getType() {
        return "images";
    }

    public String getReferencableString() {
        return mKey != null ? mKey : mExternalUrl;
    }

    public String getExternalUrl()
    {
        return mExternalUrl;
    }

    public void setExternalUrl(String externalUrl)
    {
        mExternalUrl = externalUrl;
    }

    public String getKey()
    {
        return mKey;
    }

    public void setKey(String key)
    {
        mKey = key;
    }

    public String getFilename()
    {
        return mFilename;
    }

    public void setFilename(String filename)
    {
        mFilename = filename;
    }

    public String getChecksum()
    {
        return mChecksum;
    }

    public void setChecksum(String checksum)
    {
        mChecksum = checksum;
    }

    public long getBytesize()
    {
        return mBytesize;
    }

    public void setBytesize(long bytesize)
    {
        mBytesize = bytesize;
    }

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
