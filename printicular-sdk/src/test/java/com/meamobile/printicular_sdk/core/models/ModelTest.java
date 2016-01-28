package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.JSONResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelTest
{
    protected Order getNewOrderInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_ORDER_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Order order = (Order) objects.get("orders").get(114l);
        return order;
    }

    protected Address getNewAddressInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_ADDRESS_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Address address = (Address) objects.get("addresses").get(1l);
        return address;
    }

    protected Store getNewStoreInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_STORE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Store store = (Store) objects.get("stores").get(11l);
        return store;
    }

    protected Price getNewPriceInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Price price = (Price) objects.get("prices").get(4l);
        return price;
    }

    protected PrintService getNewPrintServiceInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        PrintService printService = (PrintService) objects.get("print_services").get(3l);
        return printService;
    }

    protected Image getNewRemoteImageInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_IMAGE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Image image = (Image) objects.get("images").get(3l);
        return image;
    }

    protected Image getNewLocalImageInstance()
    {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_IMAGE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Image image = (Image) objects.get("images").get(9l);
        return image;
    }

    protected List<Product> getNewProductList() {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Map<Long, Product> products = objects.get("products");

        return new ArrayList(products.values());
    }


}
