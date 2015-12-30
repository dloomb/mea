package com.meamobile.photokit.user_interface;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.CachingImageManager;
import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.user_interface.ExplorerFragment.ExplorerFragmentDelegate;
import com.squareup.picasso.Picasso;

import java.io.File;


public class ExplorerGridViewAdapter extends BaseAdapter implements Collection.CollectionObserver {
    private Activity mActivity;
    private Collection mCollection;
    private CachingImageManager mImageCache;
    private ExplorerFragmentDelegate mDelegate;

    public ExplorerGridViewAdapter(Activity activity, Collection collection, ExplorerFragmentDelegate delegate) {
        mActivity = activity;
        mCollection = collection;
        mImageCache = new CachingImageManager(activity);
        mDelegate = delegate;
    }

    public int getCount() {
        return mCollection.numberOfAll();
    }

    public Object getItem(int position) {
        if (position < mCollection.numberOfCollections()) {
            return mCollection.collectionAtIndex(position);
        }
        else {
            return mCollection.assetAtIndex(position - mCollection.numberOfCollections());
        }
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.template_explorer_asset_item, null);
        } else {
            itemView = (View) convertView;
        }

        final ImageView mainImageView = (ImageView)itemView.findViewById(R.id.imageView);
        final String tag =  "" + (Integer.parseInt((String) mainImageView.getTag()) + 1);
        mainImageView.setTag(tag);

        ImageView selectionImageView = (ImageView)itemView.findViewById(R.id.selectionIndicatorImageView);

        if (position < mCollection.numberOfCollections())
        {
            Collection collection = mCollection.collectionAtIndex(position);

            mainImageView.setImageResource(collection.Source.ImageResourceId);
            selectionImageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            int index = position - mCollection.numberOfCollections();
            Asset asset = mCollection.assetAtIndex(index);

            boolean selected = mDelegate.isAssetSelected(asset, index);
            selectionImageView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);

            mImageCache.requestThumbnailForAsset(asset, new CachingImageManager.CachingImageManagerRequestCallback() {
                @Override
                public void success(File path) {
                    Log.d("Image Success", "GOOD");
                    final File _path = path;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tag == (String) mainImageView.getTag())
                            {
                                Picasso.with(mActivity)
                                        .load(_path)
                                        .placeholder(R.mipmap.printicular_logo)
                                        .into(mainImageView);
                            }
                        }
                    });

                }

                @Override
                public void error(String error) {
                    Log.d("Image Error", "BAD");
                }
            });

        }


        return itemView;
    }



    //--------------------------------------
    //          Collection Observer
    //--------------------------------------

    @Override
    public void collectionDidAddAsset(Collection collection, Asset added) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void collectionDidAddCollection(Collection collection, Collection added) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void collectionRefresh(Collection collection) {

    }
}