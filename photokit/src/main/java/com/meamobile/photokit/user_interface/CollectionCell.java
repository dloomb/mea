package com.meamobile.photokit.user_interface;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.meamobile.photokit.R;

public class CollectionCell extends RecyclerView.ViewHolder
{
    private ImageView mImageView;
    private ImageView mSelectionView;
    private boolean mSelected;
    private int mTag = 0;

    public CollectionCell(View v) {
        super(v);

        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mSelectionView = (ImageView) v.findViewById(R.id.selectionIndicatorImageView);
    }


    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

    public ImageView getImageView()
    {
        return mImageView;
    }

    public ImageView getSelectionView()
    {
        return mSelectionView;
    }

    public void setSelected(boolean selected)
    {
        mSelected = selected;
        mSelectionView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean getSelected()
    {
        return mSelected;
    }

    public void setReuseTag(int tag) { mTag = tag; }
    public int getReuseTag() { return mTag; }
}
