package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;
import com.meamobile.printicular_sdk.core.PrinticularCartManager;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderTest extends ModelTest
{
    @Test
    public void it_has_the_correct_type() {
        Order order = new Order();
        assertEquals("orders", order.getType());
    }


    @Test
    public void it_hydrates_correctly()
    {
        Order order = getNewOrderInstance();

        assertEquals( "TestToken", order.getDeviceToken());
        assertEquals("fake", order.getClientName());
        assertEquals("1.0", order.getClientVersion());
        assertEquals("NZD", order.getCurrency());
        assertEquals("new", order.getStatus());
    }

    @Test
    public void it_evaporates_correctly()
    {
        Order order = getNewOrderInstance();
        Address address = getNewAddressInstance();
        Store store = getNewStoreInstance();
        PrintService service = getNewPrintServiceInstance();
        Image remote = getNewRemoteImageInstance();
        Image local = getNewLocalImageInstance();

        PrinticularCartManager cart = PrinticularCartManager.getInstance();
        cart.reset();

        cart.addImageToCart(remote);
        cart.addImageToCart(local);

        cart.setCurrentPrintService(service);

        order.setLineItems(PrinticularCartManager.getInstance().getLineItems());
        order.setAddress(address);
        order.setStore(store);
        order.setPrintService(service);

        String json = order.toJsonString();

        Map map = order.evaporate();
        Map data = (Map) map.get("data");
        Map attributes = (Map) data.get("attributes");
        Map relationships = (Map) data.get("relationships");
        Map storeResource = (Map) relationships.get("store");
        Map addressResource = (Map) relationships.get("address");
        Map serviceResource = (Map) relationships.get("print_service");
        Map lineItemResource = (Map) relationships.get("line_items");

        assertNotNull(map.get("data"));
        assertNotNull(data.get("attributes"));
        assertNotNull(data.get("relationships"));

        assertEquals("TestToken", attributes.get("device_token"));
        assertEquals("fake", attributes.get("client_name"));
        assertEquals("1.0", attributes.get("client_version"));
        assertEquals("NZD", attributes.get("currency"));

        assertEquals(store.getId(), ((Map)storeResource.get("data")).get("id"));
        assertEquals(store.getType(), ((Map)storeResource.get("data")).get("type"));

        assertEquals(address.getId(), ((Map)addressResource.get("data")).get("id"));
        assertEquals(address.getType(), ((Map)addressResource.get("data")).get("type"));

        assertEquals(service.getId(), ((Map)serviceResource.get("data")).get("id"));
        assertEquals(service.getType(), ((Map) serviceResource.get("data")).get("type"));

        assertEquals(order.getLineItems().get(0).getType(), ((Map) ((List) lineItemResource.get("data")).get(0)).get("type"));
        assertEquals(order.getLineItems().get(0).getQuantity(), ((Map)((Map)((List) lineItemResource.get("data")).get(0)).get("attributes")).get("quantity"));
        assertEquals(order.getLineItems().get(0).getProduct().getId(), ((Map)((Map)((List) lineItemResource.get("data")).get(0)).get("attributes")).get("product_id"));
        assertEquals(order.getLineItems().get(0).getImage().getId(), ((Map)((Map)((List) lineItemResource.get("data")).get(0)).get("attributes")).get("image_id"));
    }

}