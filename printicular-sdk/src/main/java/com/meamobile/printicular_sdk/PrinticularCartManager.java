package com.meamobile.printicular_sdk;

import com.meamobile.printicular_sdk.models.Image;
import com.meamobile.printicular_sdk.models.LineItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrinticularCartManager
{
    protected static PrinticularCartManager mInstance;

    protected List<Image> mImages;
    protected List<LineItem> mLineItems;
    protected Map<Image, ArrayList> mData;

    protected PrinticularCartManager()
    {
        mImages = new ArrayList<Image>();
        mLineItems = new ArrayList<LineItem>();
        mData = new LinkedHashMap<Image, ArrayList>();
    }

    public static PrinticularCartManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new PrinticularCartManager();
        }

        return mInstance;
    }

    public int getImageCount()
    {
        return mData.size();
    }

    public int indexOfImage(Image image)
    {
        List keys = new ArrayList(mData.keySet());
        return keys.indexOf(image);
    }

    public void addImageToCart(Image image)
    {
        if (cartContainsImage(image) == false)
        {
            ArrayList<LineItem> items = new ArrayList<LineItem>();
            items.add(new LineItem());
            mData.put(image, items);
        }
        else
        {
            //Update Quantity or Duplicate? ...
            List<LineItem> items = mData.get(image);
            for (Object item : items)
            {

            }
        }

    }

    public void removeImageFromCart(Image image)
    {
        if (cartContainsImage(image))
        {
//            List<LineItem> items = mData.get(image);
            mData.remove(image);
        }
    }

    public boolean cartContainsImage(Image image)
    {
        return mData.containsKey(image);
    }

}
