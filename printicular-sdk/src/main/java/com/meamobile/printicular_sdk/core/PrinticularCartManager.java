package com.meamobile.printicular_sdk.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.LineItem;
import com.meamobile.printicular_sdk.core.models.Order;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Product;
import com.meamobile.printicular_sdk.core.models.Store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class PrinticularCartManager
{
    private static final String TAG = "MEA.PRTCartManager";

    private static final String SHARED_PREF_KEY = "com.meamobile.printicular_sdk.cart.shared_preferences";
    private static final String CART_SAVED_STORE_KEY = "com.meamobile.printicular_sdk.cart.saved_store";


    protected static PrinticularCartManager sInstance;

    protected List<Image> mImages;
    protected List<LineItem> mLineItems;
    protected Map<Image, ArrayList> mData;
    protected Context mContext;

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

    public void setContext(Context context)
    {
        mContext = context;
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
        if (!cartContainsImage(image))
        {
            LineItem lineItem = new LineItem();
            ArrayList<LineItem> items = new ArrayList<LineItem>();
            items.add(lineItem);
            mLineItems.add(lineItem);
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
            List<LineItem> items = mData.get(image);
            for (Object item : items)
            {
                mLineItems.remove(item);
            }
            mData.remove(image);
        }
    }

    public boolean cartContainsImage(Image image)
    {
        return mData.containsKey(image);
    }


    protected Product defaultProductForImage(Image image) {
        int w = image.getWidth();
        int h = image.getHeight();

        float ratio = Math.min(w, h) / Math.max(w, h);

        return mCurrentPrintService.getProducts().get(0);
    }


    ///-----------------------------------------------------------
    /// @name Store Handling
    ///-----------------------------------------------------------

    public void saveStore(Store store)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();

        if (store == null)
        {
            e.remove(CART_SAVED_STORE_KEY);
        }
        else {
            e.putString(CART_SAVED_STORE_KEY, store.toJsonString());
        }

        e.apply();
    }

    public Store loadSavedStore()
    {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_SAVED_STORE_KEY, null);

        if (json != null)
        {
            Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
            Store s = new Store();
            s.populate(map);
            return s;
        }

        return null;
    }




    ///-----------------------------------------------------------
    /// @name Order Handling
    ///-----------------------------------------------------------

    public Observable<Order> createNewOrderInstance()
    {
        Order order = new Order();

        if (mLineItems == null || mLineItems.size() == 0)
        {
            return Observable.error(new RuntimeException("No Line Items"));
        }

        order.setLineItems(mLineItems);
        order.setPrintService(mCurrentPrintService);
        order.setStore(mCurrentStore);
        order.setAddress(mCurrentAddress);
        order.setCurrency(mCurrentPrintService.getDefaultCurrency());



        return Observable.just(order);
    }



    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public void setCurrentPrintService(PrintService printService)
    {
        mCurrentPrintService = printService;
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
