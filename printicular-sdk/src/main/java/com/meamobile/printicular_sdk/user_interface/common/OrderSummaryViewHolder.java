package com.meamobile.printicular_sdk.user_interface.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.LineItem;
import com.meamobile.printicular_sdk.core.models.Price;
import com.meamobile.printicular_sdk.core.models.PrintService;

import java.text.DecimalFormat;
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

    public void setupWithLineItemsAndPrintService(List<LineItem> lineItems, PrintService printService)
    {
        String currency = printService.getDefaultCurrency();
        PrintService.FulfillmentType fulfillmentType = printService.getFulFillmentType();

        Map<Long, Map> map = new HashMap<>();
        double total = 0;
        boolean taxInclusive = true;

        for (LineItem i : lineItems)
        {
            Map<String, Object> description = map.get(i.getProduct().getId());
            if (description == null) {
                description = new HashMap<>();
            }

            Price price = i.getProduct().getPriceForCurrency(currency);
            int quantity = i.getQuantity();
            double itemprice = price.getTotal();
            total += itemprice * quantity;
            taxInclusive &= price.getTaxInclusive();

            int count = (description.get("quantity") != null) ? (int) description.get("quantity") : 0;
            count += i.getQuantity();

            description.put("text", i.getProduct().getName());
            description.put("quantity", count);
            description.put("price", itemprice);
            description.put("total", itemprice * count);

            map.put(i.getProduct().getId(), description);
        }


        String description = "";
        String price = "";
        DecimalFormat df = new DecimalFormat("0.00");

        for (Map details : map.values())
        {
            description += details.get("text") +
                    " ( $" + df.format(details.get("price")) + " ea. )" +
                    "\t x" + details.get("quantity") +
                    "\n";

            price += "$" + df.format(details.get("total")) + "\n";
        }

        //Product Summaries
        mTextViewProductDescription.setText(description);
        mTextViewProductPrice.setText(price);


        //Shipping
        switch (fulfillmentType) {
            case PICKUP:
                mTextViewShipping.getLayoutParams().height = 0;
                break;

            case DELIVERY:
                //Do Shipping
        }

        //Tax
        if (taxInclusive) {
            mTextViewTax.setText("all prices are GST inclusive");
        }else {
            //Do Tax
        }

        //Total
        String totalString = "TOTAL $" + df.format(total);
        mTextViewTotal.setText(totalString);
    }
}
