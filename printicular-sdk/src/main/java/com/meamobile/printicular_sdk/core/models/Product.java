package com.meamobile.printicular_sdk.core.models;


import java.util.Map;

public class Product extends Model
{
    public Product(){}

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = (Map) data.get("attributes");
        if (attributes != null)
        {

        }
    }
}
