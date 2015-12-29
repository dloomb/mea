package com.meamobile.printicular_sdk.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintService extends Model
{
    public enum FulfillmentType
    {
        DELIVERY("delivery"),
        PICKUP("pickup"),
        INVALID("invalid");

        private String text;

        FulfillmentType(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static FulfillmentType fromString(String text) {
            if (text != null) {
                for (FulfillmentType f : FulfillmentType.values()) {
                    if (text.equalsIgnoreCase(f.text)) {
                        return f;
                    }
                }
            }
            return INVALID;
        }
    }

    private String mName, mDisplayName, mDefaultCurrency;
    private FulfillmentType mFulFillmentType;
    private boolean mEnforceDefaultCurrency, mAutoConfirmable;
    private List mProducts;

    public PrintService(){}



    ///-----------------------------------------------------------
    /// @name Super Overrides
    ///-----------------------------------------------------------

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = (Map) data.get("attributes");

        if (attributes != null)
        {
            mName = (String) attributes.get("name");
            mDisplayName = (String) attributes.get("display_name");
            mDefaultCurrency = (String) attributes.get("default_currency");
            mEnforceDefaultCurrency = ((Number) attributes.get("enforce_currency")).intValue() == 1;
            mAutoConfirmable = ((Number) attributes.get("auto_confirmable")).intValue() == 1;
            mFulFillmentType = FulfillmentType.fromString((String) attributes.get("fulfillment_type"));
        }
    }


    @Override
    public void associate(Map<String, Map> objects)
    {
        super.associate(objects);

        Map productsRealtion = (Map) mRelationshipMap.get("products");
        if (productsRealtion != null && objects.get("prices") != null)
        {
            List<Map> data = (List) productsRealtion.get("data");
            if (data != null)
            {
                mProducts = new ArrayList<Price>();
                for (Map map : data)
                {
                    long priceId = ((Number) map.get("id")).longValue();
                    Product product = (Product) objects.get("products").get(priceId);
                    mProducts.add(product);
                }
            }

        }
    }
}
