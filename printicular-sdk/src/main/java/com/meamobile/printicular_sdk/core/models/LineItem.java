package com.meamobile.printicular_sdk.core.models;

public class LineItem extends Model
{
    private Product mProduct;
    private Image mImage;

    public LineItem(){}

    public Product getProduct()
    {
        return mProduct;
    }

    public void setProduct(Product product)
    {
        mProduct = product;
    }

    public void setImage(Image image) { mImage = image; }
}
