package com.meamobile.printicular.cart;

import com.meamobile.photokit.core.Asset;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.models.Image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhotoKitCartManager extends PrinticularCartManager
{
    Map<Asset, Image> mAssetToImageMap;

    protected PhotoKitCartManager()
    {
        mAssetToImageMap = new LinkedHashMap<Asset, Image>();
    }

    public static PhotoKitCartManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new PhotoKitCartManager();
        }

        return (PhotoKitCartManager) mInstance;
    }

    public void addAssetToCart(Asset asset)
    {
        Image image = new Image();
        mAssetToImageMap.put(asset, image);
        mInstance.addImageToCart(image);
    }

    public void removeAssetFromCart(Asset asset)
    {
        Image image = mAssetToImageMap.get(asset);
        mAssetToImageMap.remove(asset);
        mInstance.removeImageFromCart(image);
    }

    public boolean isAssetSelected(Asset asset)
    {
        return mAssetToImageMap.containsKey(asset);
    }

    public Asset assetAtIndex(int index)
    {
        List<Asset> list = new ArrayList(mAssetToImageMap.keySet());
        return list.get(index);
    }

    public int indexOfAsset(Asset asset)
    {
        List keys = new ArrayList(mData.keySet());
        return keys.indexOf(asset);
    }
}
