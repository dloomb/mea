package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StoreTest extends ModelTest
{


    @Test
    public void it_hydrates_correctly()
    {
        Store store = getNewStoreInstance();

        assertEquals(store.getName(),               "Warehouse Stationery Warkworth");
        assertEquals(store.getAddress(),            "4 Queen Street");
        assertEquals(store.getCity(),               "Warkworth");
        assertEquals(store.getState(),              "");
        assertEquals(store.getPostCode(),           "0910");
        assertEquals(store.getPhone(),              "(09) 422 3051");
        assertEquals(store.getStoreCode(),          "WSL_1_73");
        assertEquals(store.getRetailerId(),         "WSL");
        assertEquals(store.getRetailerStoreId(),    "73");
        assertEquals(store.getType(),               "stores");
        assertEquals(store.getId(),                 11);
    }

    @Test
    public void it_evaporates_correctly()
    {
        Store address = getNewStoreInstance();
        Map map = address.evaporate();
        Map data = (Map) map.get("data");
        Map attributes = (Map) data.get("attributes");

        assertNotNull(map.get("data"));
        assertNotNull(data.get("attributes"));

        assertEquals(11l, data.get("id"));
        assertEquals(attributes.get("name"), "Warehouse Stationery Warkworth");
        assertEquals(attributes.get("address"),     "4 Queen Street");
        assertEquals(attributes.get("city"),        "Warkworth");
        assertEquals(attributes.get("state"),       "");
        assertEquals(attributes.get("postcode"),    "0910");
        assertEquals(attributes.get("phone"),       "(09) 422 3051");
        assertEquals(attributes.get("store_code"),  "WSL_1_73");
        assertEquals(attributes.get("retailer_id"), "WSL");
    }
}