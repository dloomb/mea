package com.meamobile.printicular_sdk.user_interface;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.Store;

public class StoreDetailsViewHolder extends RecyclerView.ViewHolder
{
    private ImageView mImageViewStoreLogo, mImageViewAccessory;
    private TextView mTextViewLineOne, mTextViewLineTwo, mTextViewPickupTime;

    public StoreDetailsViewHolder(View itemView)
    {
        super(itemView);

        mImageViewStoreLogo = (ImageView) itemView.findViewById(R.id.imageViewStoreLogo);
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

        }

    }
}
