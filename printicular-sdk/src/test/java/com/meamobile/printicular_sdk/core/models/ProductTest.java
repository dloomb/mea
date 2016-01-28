package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ProductTest
{
    protected Product getNewProductInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Product product = (Product) objects.get("products").get(4l);
        return product;
    }

    @Test
    public void it_hydrates_correctly()
    {
        Product product = getNewProductInstance();

        assertEquals(4l, product.getId());
        assertEquals("6x4 matte print", product.getName());
        assertEquals(null, product.getChecksum());
        assertEquals("APP06041", product.getProductCode());
        assertEquals("6x4 matte print", product.getLongDescription());
        assertEquals("6x4 matte", product.getShortDescription());
        assertEquals("6x4M", product.getSecondaryProductCode());
    }

    @Test
    public void it_evaporates_correctly()
    {
        Product product = getNewProductInstance();

        Map map = product.evaporate();
        assertNotNull(map.get("data"));

        Map data = (Map) map.get("data");
        assertNotNull(data.get("attributes"));

        Map attributes = (Map) data.get("attributes");

        assertEquals("6x4 matte print", attributes.get("name"));
        assertEquals(null, attributes.get("checksum"));
        assertEquals("APP06041", attributes.get("product_code"));
        assertEquals("6x4 matte print", attributes.get("long_description"));
        assertEquals("6x4 matte", attributes.get("short_description"));
        assertEquals("6x4M", attributes.get("secondary_product_code"));
    }


    @Test
    public void it_associates_correctly() {
        Product product = getNewProductInstance();
        Price price = product.getPriceForCurrency("NZD");

        assertNotNull(price);
        assertNotNull(product.getPrices());

        assertEquals(0.35d, price.getTotal(), 0);
    }
}