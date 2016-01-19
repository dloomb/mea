package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AddressTest
{

    private static String RAW_ADDRESS_JSON = "{\"data\":{\"id\":1,\"type\":\"addresses\",\"attributes\":{\"email\":\"daniel@meamobile.com\",\"name\":\"daniel\",\"phone\":\"535-123-1234\",\"line_1\":\"48 Ward St\",\"line_2\":\"Level 15, Tower Building\",\"city\":\"Hamilton\",\"state\":\"WKO\",\"postcode\":\"3216\",\"territory_id\":1,\"user_id\":0,\"device_token\":\"123\",\"updated_at\":\"2016-01-18 17:53:43\",\"created_at\":\"2016-01-18 17:53:43\"},\"relationships\":{\"territory\":{\"type\":\"territories\",\"id\":1}}},\"included\":[{\"type\":\"territories\",\"id\":1,\"attributes\":{\"country_name\":\"New Zealand\",\"contry_code\":\"NZL\"}}]}";


    protected Address getNewAddressInstance()
    {
        Map<String, Object> map = new Gson().fromJson(RAW_ADDRESS_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Address address = (Address) objects.get("addresses").get(1l);
        return address;
    }

    @Test
    public void it_hydrates_correctly()
    {
        Address address = getNewAddressInstance();

        assertEquals(address.getName(),     "daniel");
        assertEquals(address.getEmail(),    "daniel@meamobile.com");
        assertEquals(address.getPhone(),    "535-123-1234");
        assertEquals(address.getLine1(),    "48 Ward St");
        assertEquals(address.getLine2(),    "Level 15, Tower Building");
        assertEquals(address.getCity(),     "Hamilton");
        assertEquals(address.getState(),    "WKO");
        assertEquals(address.getPostcode(), "3216");
    }

    @Test
    public void it_evaporates_correctly()
    {
        Address address = getNewAddressInstance();

        Map map = address.evaporate();
        assertNotNull(map.get("data"));

        Map data = (Map) map.get("data");
        assertNotNull(data.get("attributes"));

        Map attributes = (Map) data.get("attributes");

        assertEquals(attributes.get("name"),     "daniel");
        assertEquals(attributes.get("email"),    "daniel@meamobile.com");
        assertEquals(attributes.get("phone"),    "535-123-1234");
        assertEquals(attributes.get("line_1"),   "48 Ward St");
        assertEquals(attributes.get("line_2"),   "Level 15, Tower Building");
        assertEquals(attributes.get("city"),     "Hamilton");
        assertEquals(attributes.get("state"),    "WKO");
        assertEquals(attributes.get("postcode"), "3216");
    }



}