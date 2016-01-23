package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PriceTest
{

    protected Price getNewPriceInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Price price = (Price) objects.get("prices").get(4l);

        return price;
    }

    @Test
    public void it_hydrates_correctly() throws Exception
    {
        Price p = getNewPriceInstance();

        assertEquals(0.6d, p.getTotal(), 0);
        assertEquals(true, p.getTaxInclusive());
        assertEquals("NZD", p.getCurrency());
    }

    @Test
    public void it_evaporates_correctly()
    {
        Price p = getNewPriceInstance();
        Map map = p.evaporate();
        Map data = (Map) map.get("data");
        Map attributes = (Map) data.get("attributes");

        assertNotNull(map.get("data"));
        assertNotNull(data.get("attributes"));

        assertEquals(0.6, attributes.get("total"));
        assertEquals(true, attributes.get("tax_inclusive"));
        assertEquals("NZD", attributes.get("currency"));

    }

}