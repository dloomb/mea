package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.CachingImageManager;
import com.meamobile.photokit.user_interface.ExplorerFragment.ExplorerFragmentDelegate;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Collection;

import java.io.File;

import com.squareup.picasso.Picasso;


public class ExplorerRecyclerViewAdapter extends RecyclerView.Adapter implements Collection.CollectionObserver
{
    private static final int VIEWTYPE_COLLECTION = 0;
    private static final int VIEWTYPE_ASSEST = 1;

    private Activity mActivity;
    private Collection mCollection;
    private ExplorerFragmentDelegate mDelegate;
    private CachingImageManager mImageCache;
    private RecyclerView mRecyclerView;


    public ExplorerRecyclerViewAdapter(Activity activity, Collection collection, ExplorerFragmentDelegate delegate) {
        mActivity = activity;
        mCollection = collection;
        mDelegate = delegate;

        mImageCache = new CachingImageManager(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_explorer_asset_item, parent, false);

        CollectionCell cell = new CollectionCell(v);

        if (viewType == VIEWTYPE_ASSEST)
        {
            cell.setupForAsset();
        }

        return cell;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        CollectionCell cell = (CollectionCell)holder;

        if (position < mCollection.numberOfCollections())
        {
            layoutCellForCollectionAtIndex(cell, position);
            return;
        }

        int index = position - mCollection.numberOfCollections();
        layoutCellForAssetAtIndex(cell, index);
    }

    @Override
    public int getItemCount() {
        return mCollection.numberOfAll();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position < mCollection.numberOfCollections())
        {
            return VIEWTYPE_COLLECTION;
        }

        return VIEWTYPE_ASSEST;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    protected int getSpanCount()
    {
        StaggeredGridLayoutManager layout = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
        return layout.getSpanCount();
    }

    ///-----------------------------------------------------------
    /// @name View Recycling
    ///-----------------------------------------------------------

    protected void layoutCellForCollectionAtIndex(CollectionCell cell, int index)
    {
        Collection collection = mCollection.collectionAtIndex(index);
        if (collection.getCoverAsset() != null)
        {
            requestAssetForCell(collection.getCoverAsset(), cell);
        }
        else
        {
            cell.getImageView().setImageResource(collection.getSource().getImageResource());
        }
        cell.setSelected(false);
        cell.setMainText(collection.getTitle());

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) cell.itemView.getLayoutParams();
        layoutParams.setFullSpan(true);
    }

    protected void layoutCellForAssetAtIndex(CollectionCell cell, int index)
    {
        Asset asset = mCollection.assetAtIndex(index);
        cell.setSelected(mDelegate.isAssetSelected(asset, index));

        requestAssetForCell(asset, cell);

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) cell.itemView.getLayoutParams();
        layoutParams.setFullSpan(false);
    }


    private void requestAssetForCell(Asset asset, CollectionCell cell)
    {
        final CollectionCell _cell = cell;
        final int tag = _cell.getReuseTag();

        _cell.setReuseTag(tag);
        _cell.getImageView().setImageResource(R.drawable.printicular_logo);

        mImageCache.requestThumbnailForAsset(asset, new CachingImageManager.CachingImageManagerRequestCallback()
        {
            @Override
            public void success(File path)
            {
                Log.d("Image Success", "GOOD");
                final File _path = path;
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (tag == _cell.getReuseTag())
                        {
                            Picasso.with(mActivity)
                                    .load(_path)
                                    .placeholder(R.drawable.printicular_logo)
                                    .into(_cell.getImageView());
                        }
                    }
                });
            }
            @Override
            public void error(String error)
            {
                Log.d("Image Error", "BAD");
            }
        });


    }


    ///-----------------------------------------------------------
    /// @name Collection Observing
    ///-----------------------------------------------------------


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
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void collectionRefresh(Collection collection) {

    }

}