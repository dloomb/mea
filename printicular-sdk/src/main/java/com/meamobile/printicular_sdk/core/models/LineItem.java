package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class LineItem extends Model
{
    private Product mProduct;
    private Image mImage;

    private int mQuantity = 1;

    public LineItem(){}


    @Override
    public String getType()
    {
        return "line_items";
    }

    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            attributes.put("quantity", mQuantity);
            attributes.put("product_id", mProduct.getId());
            attributes.put("image_id", mImage.getId());
        }

        return data;
    }

    public Product getProduct()
    {
        return mProduct;
    }

    public void setProduct(Product product)
    {
        mProduct = product;
    }

    public Image getImage()
    {
        return mImage;
    }

    public void setImage(Image image) { mImage = image; }

    public int getQuantity()
    {
        return mQuantity;
    }

    public void setQuantity(int quantity)
    {
        mQuantity = quantity;
    }
}
