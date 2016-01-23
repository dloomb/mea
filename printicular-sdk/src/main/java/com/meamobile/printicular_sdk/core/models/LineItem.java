package com.meamobile.printicular_sdk.core.models;

public class LineItem extends Model
{
    private Product mProduct;
    private Image mImage;

    private int mQuantity = 1;

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

    public int getQuantity()
    {
        return mQuantity;
    }

    public void setmQuantity(int quantity)
    {
        mQuantity = quantity;
    }
}
