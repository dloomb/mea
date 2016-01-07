package com.meamobile.photokit.user_interface;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.meamobile.photokit.R;

public class CollectionCell extends RecyclerView.ViewHolder
{
    private ImageView mImageView;
    private ImageView mSelectionView;
    private TextView mMainTextView;
    private TextView mAccessoryTextView;
    private View mDividerView;
    private boolean mSelected;
    private int mTag = 0;

    public CollectionCell(View v) {
        super(v);

        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mSelectionView = (ImageView) v.findViewById(R.id.selectionIndicatorImageView);
        mMainTextView = (TextView) v.findViewById(R.id.textViewMain);
        mAccessoryTextView = (TextView) v.findViewById(R.id.textViewAccessory);
        mDividerView = v.findViewById(R.id.divider);
    }

    public void setupForAsset()
    {
        ViewGroup.LayoutParams layout = mImageView.getLayoutParams();
        layout.width = ViewGroup.LayoutParams.MATCH_PARENT;;
        layout.height = ViewGroup.LayoutParams.MATCH_PARENT;

        itemView.post(new Runnable() {@Override public void run()
        {
            itemView.setMinimumHeight(itemView.getWidth());
        }});
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

    public void setMainText(String text)
    {
        mMainTextView.setText(text);
    }

    public void setAccessoryText(String text)
    {
        mAccessoryTextView.setText(text);
    }

    public void setDividerVisible(boolean visible)
    {
        mDividerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
