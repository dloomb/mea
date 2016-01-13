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
    private float mDensity;

    public ExplorerRecyclerViewAdapter(Activity activity, Collection collection, ExplorerFragmentDelegate delegate) {
        mActivity = activity;
        mCollection = collection;
        mDelegate = delegate;

        mImageCache = new CachingImageManager(activity);

        mDensity = activity.getResources().getDisplayMetrics().density;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_explorer_view_holder, parent, false);

        ExplorerViewHolder cell = new ExplorerViewHolder(v);

        if (viewType == VIEWTYPE_ASSEST)
        {
            cell.setupForAsset();
        }

        return cell;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ExplorerViewHolder explorerHolder = (ExplorerViewHolder)holder;

        if (position < mCollection.numberOfCollections())
        {
            onBindCollectionViewHolder(explorerHolder, position);
        }
        else if (position < mCollection.numberOfAll())
        {
            int index = position - mCollection.numberOfCollections();
            onBindAssetViewHolder(explorerHolder, index);
        }
        else
        {
            onBindBlankViewHolder(explorerHolder);
        }
    }

    @Override
    public int getItemCount()
    {
        int collections = mCollection.numberOfCollections();
        int assets = mCollection.numberOfAssets();
        int span = getSpanCount();

        if (assets % span != 0)
        {
            assets = (int) (Math.ceil((double)assets / span) * span);
        }

        return collections + assets;
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

    protected void onBindCollectionViewHolder(ExplorerViewHolder holder, int index)
    {
        Collection collection = mCollection.collectionAtIndex(index);
        if (collection.getCoverAsset() != null)
        {
            requestAssetForCell(collection.getCoverAsset(), holder);
        }
        else
        {
            holder.getImageView().setImageResource(collection.getSource().getImageResource());
        }
        holder.setSelected(false);
        holder.setMainText(collection.getTitle());

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        layoutParams.setFullSpan(true);
    }

    protected void onBindAssetViewHolder(ExplorerViewHolder holder, int index)
    {
        Asset asset = mCollection.assetAtIndex(index);
        holder.setSelected(mDelegate.isAssetSelected(asset, index));

        requestAssetForCell(asset, holder);

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        layoutParams.setFullSpan(false);

//        int span = getSpanCount();
//        int whole = (int)Math.ceil(mDensity * 6);
//        int threeThird = (int)Math.ceil(mDensity * 4);
//        int third = (int)Math.ceil(mDensity * 4);
//        int half = (int)Math.ceil(mDensity * 3);
//
//        if (index % span == 0) //First Column
//        {
//            holder.itemView.setPadding(threeThird, whole, third, 0);
//        }
//        else if ((index + 1) % span == 0) //Last Column
//        {
//            holder.itemView.setPadding(third, whole, threeThird, 0);
//        }
//        else
//        {
//            holder.itemView.setPadding(half, whole, half, 0);
//        }
    }

    protected void onBindBlankViewHolder(ExplorerViewHolder holder)
    {
        holder.setSelected(false);
        holder.getImageView().setImageDrawable(null);
    }


    private void requestAssetForCell(Asset asset, ExplorerViewHolder cell)
    {
        final ExplorerViewHolder _cell = cell;
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
    public void collectionDidAddAssetAtIndex(Collection collection, Asset added, int index) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void collectionDidAddCollectionAtIndex(Collection collection, Collection added, int index) {
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
