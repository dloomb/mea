package com.meamobile.printicular_sdk.core.models;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product extends Model
{
    public static Product cheaper(Product p1, Product p2, String currency) {

        Price price1 = p1.getPriceForCurrency(currency);
        Price price2 = p2.getPriceForCurrency(currency);

        if (price1 == null) {
            return p2;
        }

        if (price2 == null) {
            return p1;
        }

        if (price1.getTotal() < price2.getTotal()) {
            return p1;
        }

        return p2;
    }


    private PrintService mPrintService;

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


    private boolean mIsMetric;

    @Override
    public String getType()
    {
        return "products";
    }


    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            mName = (String) attributes.get("name");
            mChecksum = (String) attributes.get("checksum");
            mProductCode = (String) attributes.get("product_code");
            mLongDescription = (String) attributes.get("long_description");
            mShortDescription = (String) attributes.get("short_description");
            mSecondaryProductCode = (String) attributes.get("secondary_product_code");

            mWidth = (int) safeParse(attributes.get("width"), ClassType.INTEGER);
            mHeight = (int) safeParse(attributes.get("height"), ClassType.INTEGER);
            mPixelWidth = (int) safeParse(attributes.get("pixel_width"), ClassType.INTEGER);
            mPixelHeight = (int) safeParse(attributes.get("pixel_height"), ClassType.INTEGER);
            mMinimumResolution = (int) safeParse(attributes.get("minimum_resolution"), ClassType.INTEGER);

            mIsMetric = (boolean) safeParse(attributes.get("metric"), ClassType.BOOLEAN);
        }
    }

    @Override
    public void associate(Map<String, Map> objects)
    {
        super.associate(objects);

        try {
            Map<Long, Price> prices = objects.get("prices");
            List<Map> relations = (List) ((Map)mRelationshipMap.get("prices")).get("data");

            mPrices = new ArrayList<>();
            for (Map relation : relations)
            {
                long id = (long) safeParse(relation.get("id"), ClassType.LONG);
                Price p = prices.get(id);

                if (p != null) {
                    mPrices.add(p);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            attributes.put("name", mName);
            attributes.put("checksum", mChecksum);
            attributes.put("product_code", mProductCode);
            attributes.put("long_description", mLongDescription);
            attributes.put("short_description", mShortDescription);
            attributes.put("secondary_product_code", mSecondaryProductCode);

            attributes.put("width", mWidth);
            attributes.put("height", mHeight);
            attributes.put("pixel_width", mPixelWidth);
            attributes.put("pixel_height", mPixelHeight);
            attributes.put("minimum_resolution", mMinimumResolution);

            attributes.put("metric", mIsMetric);
        }

        return data;
    }


    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------


    public PrintService getPrintService()
    {
        return mPrintService;
    }

    public List<Price> getPrices()
    {
        return mPrices;
    }

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



    public Price getPriceForCurrency(String currency) {
        if (mPrices != null) {

            for (Price p : mPrices)
            {
                if (p.getCurrency().equalsIgnoreCase(currency))
                {
                    return p;
                }
            }
        }
        return null;
    }
}
