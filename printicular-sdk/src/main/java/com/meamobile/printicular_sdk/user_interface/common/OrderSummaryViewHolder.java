package com.meamobile.printicular_sdk.user_interface.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.LineItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSummaryViewHolder extends RecyclerView.ViewHolder
{
    private TextView
            mTextViewProductDescription,
            mTextViewProductPrice,
            mTextViewShipping,
            mTextViewTotal,
            mTextViewTax;

    public OrderSummaryViewHolder(View itemView)
    {
        super(itemView);

        mTextViewProductDescription = (TextView) itemView.findViewById(R.id.textViewProductDescription);
        mTextViewProductPrice = (TextView) itemView.findViewById(R.id.textViewProductPrice);
        mTextViewShipping = (TextView) itemView.findViewById(R.id.textViewShipping);
        mTextViewTotal = (TextView) itemView.findViewById(R.id.textViewTotal);
        mTextViewTax = (TextView) itemView.findViewById(R.id.textViewTax);
    }

    public void setupWithLineItems(List<LineItem> lineItems)
    {
        Map<Long, Map> map = new HashMap<>();

        for (LineItem i : lineItems)
        {
            Map<String, Object> description = map.get(i.getProduct().getId());
            if (description == null) {
                description = new HashMap<>();
            }

            int count = (description.get("quantity") != null) ? (int) description.get("quantity") : 0;
            count += i.getQuantity();

            description.put("text", i.getProduct().getName());
            description.put("quantity", count);
            description.put("price", i.getProduct().getPriceForCurrency("NZD").getTotal() * count);
        }


        String description = "";
        String price = "";

        for (Map details : map.values())
        {
            description += details.get("text") + "\n";
            price += "$" + details.get("price") + "\n";
        }

        mTextViewProductDescription.setText(description);
        mTextViewProductPrice.setText(price);
    }
}
