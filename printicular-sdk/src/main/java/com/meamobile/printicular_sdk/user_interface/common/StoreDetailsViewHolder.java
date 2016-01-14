package com.meamobile.printicular_sdk.user_interface.common;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Store;

import org.w3c.dom.Text;

public class StoreDetailsViewHolder extends RecyclerView.ViewHolder
{
    private RelativeLayout
            mRelativeLayoutOuter,
            mRelativeLayoutInner;

    private ImageView
            mImageViewStoreLogo,
            mImageViewAccessory;

    private TextView
            mTextViewTitle,
            mTextViewLineOne,
            mTextViewLineTwo,
            mTextViewPickupTime;

    public StoreDetailsViewHolder(View itemView)
    {
        super(itemView);

        mRelativeLayoutOuter = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutOuter);
        mRelativeLayoutInner = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutInner);

        mImageViewStoreLogo = (ImageView) itemView.findViewById(R.id.imageViewStoreLogo);
        mTextViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        mTextViewLineOne = (TextView) itemView.findViewById(R.id.textViewAddressLine1);
        mTextViewLineTwo = (TextView) itemView.findViewById(R.id.textViewAddressLine2);
        mTextViewPickupTime = (TextView) itemView.findViewById(R.id.textViewPickupTime);
        mImageViewAccessory = (ImageView) itemView.findViewById(R.id.imageViewAccessory);
    }

    public void setStore(Store store)
    {
        if (store == null)
        {
            mImageViewStoreLogo.setVisibility(View.GONE);
            mTextViewLineOne.setText("Pleases select a store");
            mTextViewLineTwo.setVisibility(View.GONE);
            mTextViewPickupTime.setVisibility(View.GONE);
            mImageViewAccessory.setImageResource(R.drawable.vect_chevron_right);
        }
        else
        {
            mImageViewStoreLogo.setImageResource(store.getStoreLogoImageResourceId());
            mTextViewLineOne.setText(store.getAddress());
            mTextViewLineTwo.setText(store.getPostCode() + " " + store.getCity());
        }
    }

    public void setStoreDetailsHeadingVisibility(int visibility)
    {
        mTextViewTitle.setVisibility(visibility);
    }

    public void setAccessoryVisibility(int visibility)
    {
        mImageViewAccessory.setVisibility(visibility);
    }

    public void setMargins(int t, int l, int b, int r)
    {
        int d = (int)PrinticularServiceManager.getInstance().getContext().getResources().getDisplayMetrics().density;
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) mRelativeLayoutOuter.getLayoutParams();
        params.setMargins(l * d, t * d, r * d, b * d);
    }
}
