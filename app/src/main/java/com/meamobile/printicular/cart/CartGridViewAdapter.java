package com.meamobile.printicular.cart;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.CachingImageManager;
import com.meamobile.printicular.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CartGridViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private PhotoKitCartManager mCart;
    private CachingImageManager mImageCache;

    public CartGridViewAdapter(Activity activity) {
        mActivity = activity;
        mCart = PhotoKitCartManager.getInstance();
        mImageCache = new CachingImageManager(activity);
    }

    public int getCount() {
        return mCart.getImageCount();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.template_cart_item, null);
        } else {
            itemView = (View) convertView;
        }

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        final int tag = (int) imageView.getTag() + 1;
        imageView.setTag(tag);

        Asset asset = mCart.assetAtIndex(position);

        mImageCache.requestThumbnailForAsset(asset, new CachingImageManager.CachingImageManagerRequestCallback() {
            @Override
            public void success(File path) {
                Log.d("Image Success", "GOOD");
                final File _path = path;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (tag == imageView.getTag())
                        {
                            Picasso.with(mActivity)
                                    .load(_path)
                                    .placeholder(com.meamobile.photokit.R.mipmap.printicular_logo)
                                    .into(imageView);
                        }
                    }
                });

            }

            @Override
            public void error(String error) {
                Log.d("Image Error", "BAD");
            }
        });

        return itemView;
    }

}