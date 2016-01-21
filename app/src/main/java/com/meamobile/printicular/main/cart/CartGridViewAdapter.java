package com.meamobile.printicular.main.cart;

import android.app.Activity;
import android.content.Context;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        final String tag =  "" + (Integer.parseInt((String) imageView.getTag()) + 1);
        imageView.setTag(tag);

        Asset asset = mCart.assetAtIndex(position);

        mImageCache.requestThumbnailForAsset(asset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> {
                            if (tag == (String) imageView.getTag())
                            {
                                Picasso.with(mActivity)
                                        .load(file)
                                        .placeholder(com.meamobile.photokit.R.drawable.printicular_logo)
                                        .into(imageView);
                            }
                        },
                        error -> {
                            Log.d("Image Error", "BAD");
                        }
                );

        return itemView;
    }

}