package com.meamobile.printicular_sdk.core.models;


import java.util.Map;

public class Price extends Model
{
    private Product mProduct;

    private String mCurrency;

    private double mTotal;

    private boolean mTaxInclusive;

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            mCurrency = (String) attributes.get("currency");
            mTotal = (double) safeParse(attributes.get("total"), ClassType.DOUBLE);
            mTaxInclusive = (boolean) safeParse(attributes.get("total"), ClassType.BOOLEAN);
        }
    }
}
