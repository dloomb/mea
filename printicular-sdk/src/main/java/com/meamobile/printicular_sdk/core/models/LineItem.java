package com.meamobile.printicular_sdk.core.models;

public class LineItem extends Model
{
    private Product mProduct;

    public LineItem(Product product) {
        mProduct = product;
    }

    public Product getProduct()
    {
        return mProduct;
    }

    public void setProduct(Product product)
    {
        mProduct = product;
    }
}
