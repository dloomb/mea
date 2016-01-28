package com.meamobile.printicular.main.cart;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.RemoteAsset;
import com.meamobile.photokit.local.LocalAsset;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;
import com.meamobile.printicular_sdk.core.models.Image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhotoKitCartManager extends PrinticularCartManager
{
    Map<String, Image> mImageMap;
    Map<String, Asset> mAssetMap;

    protected PhotoKitCartManager()
    {
        mImageMap = new LinkedHashMap<String, Image>();
        mAssetMap = new LinkedHashMap<String, Asset>();
    }

    public static PhotoKitCartManager getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new PhotoKitCartManager();
        }

        return (PhotoKitCartManager) sInstance;
    }

    public void addAssetToCart(Asset asset)
    {
        Image image = new Image();
        if (asset instanceof RemoteAsset) {
            RemoteAsset remote = (RemoteAsset) asset;
            image.setExternalUrl(remote.getFullResolutionUrlString());
        } else if (asset instanceof LocalAsset) {
            LocalAsset local = (LocalAsset) asset;
            image.setBytesize(local.getBytesize());
            image.setChecksum(local.getChecksum());
            image.setFilename(local.getFilename());
        }

        image.setWidth(asset.getWidth());
        image.setHeight(asset.getHeight());

        mImageMap.put(asset.getAssetIdentifier(), image);
        mAssetMap.put(asset.getAssetIdentifier(), asset);
        sInstance.addImageToCart(image);
    }

    public void removeAssetFromCart(Asset asset)
    {
        String id = asset.getAssetIdentifier();
        Image image = mImageMap.get(id);
        mImageMap.remove(id);
        mAssetMap.remove(id);
        sInstance.removeImageFromCart(image);
    }

    public boolean isAssetSelected(Asset asset)
    {
        return mAssetMap.containsKey(asset.getAssetIdentifier());
    }

    public Asset assetAtIndex(int index)
    {
        List<Asset> list = new ArrayList(mAssetMap.values());
        return list.get(index);
    }

    public int indexOfAsset(Asset asset)
    {
        List keys = new ArrayList(mData.keySet());
        return keys.indexOf(asset);
    }
}
