package com.meamobile.printicular_sdk.user_interface.address;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.Address;
import com.meamobile.printicular_sdk.core.models.PrintService;

public class AddressDetailsViewHolder extends RecyclerView.ViewHolder
{
    private TextView
            mTextViewTitle,
            mTextViewName,
            mTextViewLine1,
            mTextViewLine2;
    private RelativeLayout
            mRelativeLayoutLoadingContainer,
            mRelativeLayoutTextContainer;

    private boolean mLoading;

    public AddressDetailsViewHolder(View itemView)
    {
        super(itemView);

        mRelativeLayoutTextContainer = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutTextContainer);
        mRelativeLayoutLoadingContainer = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutLoadingContainer);

        mTextViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        mTextViewName = (TextView) itemView.findViewById(R.id.textViewName);
        mTextViewLine1 = (TextView) itemView.findViewById(R.id.textViewLine1);
        mTextViewLine2 = (TextView) itemView.findViewById(R.id.textViewLine2);
    }

    public void setAddress(Address address, PrintService printService)
    {
        mTextViewName.setVisibility(View.VISIBLE);
        mTextViewLine1.setVisibility(View.VISIBLE);
        mTextViewLine2.setVisibility(View.VISIBLE);

        mTextViewTitle.setText("Your Details");
        String blankMessage = "Tap to enter your details";

        boolean isDelivery = printService.getFulFillmentType() == PrintService.FulfillmentType.DELIVERY;

        if (isDelivery)
        {
            mTextViewTitle.setText("Postal Details");
            blankMessage = "Tap to enter your address";
        }

        if (address == null) {
            mTextViewName.setText(blankMessage);
            mTextViewLine1.setVisibility(View.GONE);
            mTextViewLine2.setVisibility(View.GONE);
        }
        else
        {
            String line1 = address.getPhone();
            String line2 = address.getEmail();
            if (isDelivery){
                line1 = address.getLine1();
                line2 = address.getLine2();
            }

            mTextViewName.setText(address.getName());
            mTextViewLine1.setText(line1);
            mTextViewLine2.setText(line2);
        }
    }

    public void setLoading(boolean loading)
    {
        mLoading = loading;

        if (loading) {
            mRelativeLayoutTextContainer.setVisibility(View.GONE);
            mRelativeLayoutLoadingContainer.setVisibility(View.VISIBLE);
        }
        else {
            mRelativeLayoutTextContainer.setVisibility(View.VISIBLE);
            mRelativeLayoutLoadingContainer.setVisibility(View.GONE);
        }
    }

    public boolean isLoading()
    {
        return mLoading;
    }
}
