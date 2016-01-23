package com.meamobile.printicular_sdk.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.LineItem;
import com.meamobile.printicular_sdk.core.models.Model;
import com.meamobile.printicular_sdk.core.models.Order;
import com.meamobile.printicular_sdk.core.models.PrintService;
import com.meamobile.printicular_sdk.core.models.Product;
import com.meamobile.printicular_sdk.core.models.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import rx.Observable;

public class PrinticularCartManager
{
    private static final String TAG = "MEA.PRTCartManager";

    private static final String SHARED_PREF_KEY = "com.meamobile.printicular_sdk.cart.shared_preferences";
    private static final String CART_SAVED_STORE_KEY = "com.meamobile.printicular_sdk.cart.saved_store";
    private static final String CART_SAVED_ADDRESS_KEY = "com.meamobile.printicular_sdk.cart.saved_address";


    protected static PrinticularCartManager sInstance;

    protected List<LineItem> mLineItems;
    protected Map<Image, ArrayList> mData;
    protected Context mContext;
    protected Map<Float, Product> mProductsByRatio;

    private PrintService mCurrentPrintService;
    private Store mCurrentStore;
    private Address mCurrentAddress;

    protected PrinticularCartManager()
    {
        mLineItems = new ArrayList<>();
        mData = new LinkedHashMap<>();
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

    public void reset() {
        mLineItems = new ArrayList<>();
        mData = new LinkedHashMap<>();
        mCurrentAddress = null;
        mCurrentPrintService = null;
        mCurrentStore = null;
        mProductsByRatio = null;
    }

    ///-----------------------------------------------------------
    /// @name Image Handling
    ///-----------------------------------------------------------

    public List<Image> getImages() {
        return new ArrayList<>(mData.keySet());
    }

    public List<LineItem> getLineItems() {
        return mLineItems;
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
        if (!cartContainsImage(image))
        {
            LineItem lineItem = newLineItemForImage(image);
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

    protected LineItem newLineItemForImage(Image image) {
        LineItem li = new LineItem();
        li.setProduct(defaultProductForImage(image));
        li.setImage(image);
        return li;
    }


    ///-----------------------------------------------------------
    /// @name Product Handling
    ///-----------------------------------------------------------

    protected void calculateAndSetProductsByRatio(List<Product> products, String currency)
    {
        mProductsByRatio = new HashMap<>();

        for (Product p : products)
        {
            float w = p.getWidth();
            float h = p.getHeight();
            if (w == 0 || h == 0) {
                Log.d(TAG, "Product width or height was Zero.");
                continue;
            }

            float ratio = Math.min(w, h) / Math.max(w, h);
            Product existing = mProductsByRatio.get(ratio);
            if (existing != null) {
                mProductsByRatio.put(ratio, Product.cheaper(p, existing, currency));
            } else {
                mProductsByRatio.put(ratio, p);
            }
        }

        List<Image> images = getImages();
        for (Image i : images)
        {
            Product p = defaultProductForImage(i);
            List<LineItem> items = mData.get(i);

            for (LineItem li : items)
            {
                li.setProduct(p);
            }
        }
    }

    protected Product defaultProductForImage(Image image) {
        if (mProductsByRatio != null) {
            int w = image.getWidth();
            int h = image.getHeight();
            if (w == 0 || h == 0) {
                return mProductsByRatio.values().iterator().next();
            }

            float ratio = (float)Math.min(w, h) / Math.max(w, h);

            List<Float> keys = new ArrayList<>(mProductsByRatio.keySet());
            Collections.sort(keys);
            int p = Collections.binarySearch(keys, ratio);
            p = (p < 0) ? -p - 1 : p;

            if (p == 0 || p == keys.size()) {
                return mProductsByRatio.get(keys.get(p));
            }

            float upper = keys.get(p);
            float lower = keys.get(p - 1);

            float diffLower = ratio - lower;
            float diffUpper= upper - ratio;

            if (diffLower < diffUpper) {
                return mProductsByRatio.get(lower);
            }

            return mProductsByRatio.get(upper);
        }
        return null;
    }



    ///-----------------------------------------------------------
    /// @name Store Handling
    ///-----------------------------------------------------------

    public void saveStore(Store store)
    {
        saveModelToDisk(CART_SAVED_STORE_KEY, store);
    }

    public Store loadSavedStore()
    {
        return (Store) loadModelFromDisk(CART_SAVED_STORE_KEY, new Store());
    }



    ///-----------------------------------------------------------
    /// @name Address Handling
    ///-----------------------------------------------------------

    public void saveAddress(Address address) {
        saveModelToDisk(CART_SAVED_ADDRESS_KEY, address);
    }

    public Address loadSavedAddress() {
        return (Address) loadModelFromDisk(CART_SAVED_ADDRESS_KEY, new Address());
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
        if (printService != null && printService != mCurrentPrintService) {
            calculateAndSetProductsByRatio(printService.getProducts(), printService.getDefaultCurrency());
        }

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





    ///-----------------------------------------------------------
    /// @name Helpers
    ///-----------------------------------------------------------

    protected void saveModelToDisk(String key, Model value) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();

        if (value == null)
        {
            e.remove(key);
        }
        else {
            e.putString(key, value.toJsonString());
        }

        e.commit();
    }

    protected Model loadModelFromDisk(String key, Model model) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);

        if (json != null)
        {
            Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
            model.populate(map);
            return model;
        }

        return null;
    }

}
