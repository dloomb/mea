package com.meamobile.printicular_sdk.user_interface.common;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.PrinticularServiceManager;
import com.meamobile.printicular_sdk.core.models.Store;

import java.text.DecimalFormat;

public class StoreDetailsViewHolder extends RecyclerView.ViewHolder
{
    private RelativeLayout
            mRelativeLayoutOuter,
            mRelativeLayoutInner,
            mRelativeLayoutSpecial;

    private View mViewBrandDetail;

    private ImageView
            mImageViewStoreLogo,
            mImageViewAccessory,
            mImageViewSpecial;

    private TextView
            mTextViewTitle,
            mTextViewLineOne,
            mTextViewLineTwo,
            mTextViewSpecial;

    public StoreDetailsViewHolder(View itemView)
    {
        super(itemView);

        mRelativeLayoutOuter = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutOuter);
        mRelativeLayoutInner = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutInner);
        mRelativeLayoutSpecial = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutSpecialText);

        mViewBrandDetail = itemView.findViewById(R.id.viewBrandDetail);

        mImageViewStoreLogo = (ImageView) itemView.findViewById(R.id.imageViewStoreLogo);
        mTextViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        mTextViewLineOne = (TextView) itemView.findViewById(R.id.textViewLine1);
        mTextViewLineTwo = (TextView) itemView.findViewById(R.id.textViewLine2);
        mTextViewSpecial = (TextView) itemView.findViewById(R.id.textViewSpecial);
        mImageViewSpecial = (ImageView) itemView.findViewById(R.id.imageViewSpecial);
        mImageViewAccessory = (ImageView) itemView.findViewById(R.id.imageViewAccessory);
    }

    public void setStore(Store store)
    {
        if (store == null)
        {
            mImageViewStoreLogo.setVisibility(View.GONE);
            mTextViewLineOne.setText("Pleases select a store");
            mTextViewLineTwo.setVisibility(View.GONE);
            mTextViewSpecial.setVisibility(View.GONE);
            mImageViewAccessory.setImageResource(R.drawable.vect_chevron_right);
        }
        else
        {
            mImageViewStoreLogo.setImageResource(store.getStoreLogoImageResourceId());
            mTextViewLineOne.setText(store.getAddress());
            mTextViewLineTwo.setText(store.getPostCode() + " " + store.getCity());

            int color = itemView.getContext().getResources().getColor(store.getStoreColorResourceId());
            mViewBrandDetail.setBackgroundColor(color);

            Object distance = store.getMeta("distance");
            if (distance != null)
            {
                DecimalFormat df = new DecimalFormat("#.00");
                mRelativeLayoutSpecial.setVisibility(View.VISIBLE);
                mTextViewSpecial.setText(df.format(distance) + " km away");
            }
            else
            {
                mRelativeLayoutSpecial.setVisibility(View.GONE);
            }

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
