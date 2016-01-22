package com.meamobile.printicular_sdk.core.models;


import java.util.List;
import java.util.Map;

public class Product extends Model
{
    private List<Price> mPrices;

    private String
            mName,
            mChecksum,
            mProductCode,
            mLongDescription,
            mShortDescription,
            mSecondaryProductCode;

    private int
            mWidth,
            mHeight,
            mPixelWidth,
            mPixelHeight,
            mMinimumResolution;


    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            mName = (String) attributes.get("name");
            mChecksum = (String) attributes.get("name");
            mProductCode = (String) attributes.get("product_code");
            mLongDescription = (String) attributes.get("long_description");
            mShortDescription = (String) attributes.get("short_description");
            mSecondaryProductCode = (String) attributes.get("secondary_product_code");

            mWidth = (int) safeParse(attributes.get("width"), ClassType.INTEGER);
            mHeight = (int) safeParse(attributes.get("height"), ClassType.INTEGER);
            mPixelWidth = (int) safeParse(attributes.get("pixel_width"), ClassType.INTEGER);
            mPixelHeight = (int) safeParse(attributes.get("pixel_height"), ClassType.INTEGER);
            mMinimumResolution = (int) safeParse(attributes.get("minimum_resolution"), ClassType.INTEGER);
        }
    }


    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getChecksum()
    {
        return mChecksum;
    }

    public void setChecksum(String checksum)
    {
        mChecksum = checksum;
    }

    public String getProductCode()
    {
        return mProductCode;
    }

    public void setProductCode(String productCode)
    {
        mProductCode = productCode;
    }

    public String getLongDescription()
    {
        return mLongDescription;
    }

    public void setLongDescription(String longDescription)
    {
        mLongDescription = longDescription;
    }

    public String getShortDescription()
    {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        mShortDescription = shortDescription;
    }

    public String getSecondaryProductCode()
    {
        return mSecondaryProductCode;
    }

    public void setSecondaryProductCode(String secondaryProductCode)
    {
        mSecondaryProductCode = secondaryProductCode;
    }


    public int getWidth()
    {
        return mWidth;
    }

    public void setWidth(int width)
    {
        mWidth = width;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public void setHeight(int height)
    {
        mHeight = height;
    }

    public int getPixelWidth()
    {
        return mPixelWidth;
    }

    public void setPixelWidth(int pixelWidth)
    {
        mPixelWidth = pixelWidth;
    }

    public int getPixelHeight()
    {
        return mPixelHeight;
    }

    public void setPixelHeight(int pixelHeight)
    {
        mPixelHeight = pixelHeight;
    }

    public int getMinimumResolution()
    {
        return mMinimumResolution;
    }

    public void setMinimumResolution(int minimumResolution)
    {
        mMinimumResolution = minimumResolution;
    }
}
