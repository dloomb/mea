package com.meamobile.printicular_sdk.core.models;


import java.util.Map;

public class Price extends Model
{
    private Product mProduct;

    private String mCurrency;

    private double mTotal;

    private boolean mTaxInclusive;

    @Override
    public String getType()
    {
        return "prices";
    }

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            mCurrency = (String) attributes.get("currency");
            mTotal = (double) safeParse(attributes.get("total"), ClassType.DOUBLE);
            mTaxInclusive = (boolean) safeParse(attributes.get("tax_inclusive"), ClassType.BOOLEAN);
        }
    }

    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            attributes.put("currency", mCurrency);
            attributes.put("total", mTotal);
            attributes.put("tax_inclusive", mTaxInclusive);
        }

        return data;
    }

    public String getCurrency()
    {
        return mCurrency;
    }

    public void setCurrency(String currency)
    {
        mCurrency = currency;
    }

    public double getTotal()
    {
        return mTotal;
    }

    public void setTotal(double total)
    {
        mTotal = total;
    }

    public boolean getTaxInclusive() {
        return mTaxInclusive;
    }

    public void setTaxInclusive(boolean taxInclusive)
    {
        mTaxInclusive = taxInclusive;
    }
}
