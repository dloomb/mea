package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AddressTest extends ModelTest
{
    @Test
    public void it_has_the_correct_type() {
        Address a = new Address();
        assertEquals("addresses", a.getType());
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
        assertEquals(address.getDeviceToken(), "AddressTestToken");
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
        assertEquals(attributes.get("device_token"), "AddressTestToken");

    }



}