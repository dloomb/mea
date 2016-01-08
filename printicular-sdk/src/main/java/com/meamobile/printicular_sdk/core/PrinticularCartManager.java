package com.meamobile.printicular_sdk.core;

import android.location.Address;

import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.LineItem;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrinticularCartManager
{
    protected static PrinticularCartManager sInstance;

    protected List<Image> mImages;
    protected List<LineItem> mLineItems;
    protected Map<Image, ArrayList> mData;

    private PrintService mCurrentPrintService;
    private Store mCurrentStore;
    private Address mCurrentAddress;

    protected PrinticularCartManager()
    {
        mImages = new ArrayList<Image>();
        mLineItems = new ArrayList<LineItem>();
        mData = new LinkedHashMap<Image, ArrayList>();
    }

    public static PrinticularCartManager getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new PrinticularCartManager();
        }

        return sInstance;
    }

    protected void loadRecentlyUsedStore()
    {

    }


    ///-----------------------------------------------------------
    /// @name Image Handling
    ///-----------------------------------------------------------

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
            mData.remove(image);
        }
    }

    public boolean cartContainsImage(Image image)
    {
        return mData.containsKey(image);
    }





    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public void setCurrentPrintService(PrintService printService)
    {
        mCurrentPrintService = printService;
        loadRecentlyUsedStore();
    }

    public PrintService getCurrentPrintService()
    {
        return mCurrentPrintService;
    }

    public void setCurrentStore(Store store)
    {
        mCurrentStore = store;
    }

    public Store getCurrentStore()
    {
        return mCurrentStore;
    }

    public void setCurrentAddress(Address address)
    {
        mCurrentAddress = address;
    }

    public Address getCurrentAddress()
    {
        return mCurrentAddress;
    }



}
