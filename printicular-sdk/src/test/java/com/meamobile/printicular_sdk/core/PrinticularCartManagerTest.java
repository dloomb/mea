package com.meamobile.printicular_sdk.core;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.models.Image;
import com.meamobile.printicular_sdk.core.models.LineItem;
import com.meamobile.printicular_sdk.core.models.Model;
import com.meamobile.printicular_sdk.core.models.Price;
import com.meamobile.printicular_sdk.core.models.Product;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PrinticularCartManagerTest extends PrinticularCartManager
{
    protected List<Product> getNewProductList() {
        Map<String, Object> map = new Gson().fromJson(JSONResource.RAW_WAREHOUSE_PRINT_SERVICE_JSON, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Map> objects = Model.hydrate(map);

        Map<Long, Product> products = objects.get("products");

        return new ArrayList(products.values());
    }

    protected Image getNewImageAtSize(int w, int h) {
        Image i = new Image();
        i.setWidth(w);
        i.setHeight(h);
        return i;
    }

    @Before
    public void setUp() {

    }

    @Test
    public void it_calculates_common_default_products_correctly()
    {
        getInstance();//create

        Image i720x480 = getNewImageAtSize(720, 480); //0.66
        Image i1024x768 = getNewImageAtSize(1024, 768); //0.75
        Image i1920x1080 = getNewImageAtSize(1920, 1080); //0.56
        Image i3264x2448 = getNewImageAtSize(3264, 2448); //0.75

        Image i320x320 = getNewImageAtSize(320, 320); //1.0
        Image i3264x3264= getNewImageAtSize(3264, 3264);

        String code4x4 = "4x4M"; //1.0
        String code6x4 = "6x4M"; //0.66
        String code6x8 = "6x8M"; //0.75

        addImageToCart(i720x480);
        addImageToCart(i1024x768);
        addImageToCart(i1920x1080);
        addImageToCart(i3264x2448);

        addImageToCart(i320x320);
        addImageToCart(i3264x3264);

        calculateAndSetProductsByRatio(getNewProductList(), "NZD");

        Product p1 = mLineItems.get(0).getProduct();
        assertTrue(p1.getWidth() != p1.getHeight());
        assertEquals(code6x4, p1.getSecondaryProductCode());

        Product p2 = mLineItems.get(1).getProduct();
        assertTrue(p2.getWidth() != p2.getHeight());
        assertEquals(code6x8, p2.getSecondaryProductCode());

        Product p3 = mLineItems.get(2).getProduct();
        assertTrue(p3.getWidth() != p3.getHeight());
        assertEquals(code6x4, p3.getSecondaryProductCode());

        Product p4 = mLineItems.get(3).getProduct();
        assertTrue(p4.getWidth() != p4.getHeight());
        assertEquals(code6x8, p4.getSecondaryProductCode());

        Product p5 = mLineItems.get(4).getProduct();
        assertTrue(p5.getWidth() == p5.getHeight());
        assertEquals(code4x4, p5.getSecondaryProductCode());

        Product p6 = mLineItems.get(5).getProduct();
        assertTrue(p6.getWidth() == p6.getHeight());
        assertEquals(code4x4, p6.getSecondaryProductCode());
    }

    @Test
    public void it_calculates_uncommon_default_products_correctly()
    {
        getInstance();//create

        Image i720x320 = getNewImageAtSize(720, 320); //0.44
        Image i352x288 = getNewImageAtSize(352, 288); //0.81
        Image i520x720 = getNewImageAtSize(520, 720); //0.75
        Image i720x480 = getNewImageAtSize(720, 480); //0.66

        Image i320x300 = getNewImageAtSize(320, 300); //1.0
        Image i3264x3200= getNewImageAtSize(3264, 3200);

        String code6x4 = "6x4M"; //0.66
        String code5x7 = "5x7M"; //0.71
        String code8x10 = "8x10M"; //0.8
        String code4x4 = "4x4M"; //1.0

        addImageToCart(i720x320);
        addImageToCart(i352x288);
        addImageToCart(i520x720);
        addImageToCart(i720x480);

        addImageToCart(i320x300);
        addImageToCart(i3264x3200);

        calculateAndSetProductsByRatio(getNewProductList(), "NZD");

        Product p1 = mLineItems.get(0).getProduct();
        assertTrue(p1.getWidth() != p1.getHeight());
        assertEquals(code6x4, p1.getSecondaryProductCode());

        Product p2 = mLineItems.get(1).getProduct();
        assertTrue(p2.getWidth() != p2.getHeight());
        assertEquals(code8x10, p2.getSecondaryProductCode());

        Product p3 = mLineItems.get(2).getProduct();
        assertTrue(p3.getWidth() != p3.getHeight());
        assertEquals(code5x7, p3.getSecondaryProductCode());

        Product p4 = mLineItems.get(3).getProduct();
        assertTrue(p4.getWidth() != p4.getHeight());
        assertEquals(code6x4, p4.getSecondaryProductCode());

        Product p5 = mLineItems.get(4).getProduct();
        assertTrue(p5.getWidth() == p5.getHeight());
        assertEquals(code4x4, p5.getSecondaryProductCode());

        Product p6 = mLineItems.get(5).getProduct();
        assertTrue(p6.getWidth() == p6.getHeight());
        assertEquals(code4x4, p6.getSecondaryProductCode());
    }


}