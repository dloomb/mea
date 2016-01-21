package com.meamobile.printicular_sdk.core.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

public class ImageTest
{
    private static final String EXTERNAL_URL = "https://scontent-sea1-1.xx.fbcdn.net/hphotos-xat1/v/t1.0-9/11088406_1397148517272868_4043863592218515543_n.jpg?oh=3785879beb07bdc1b7faf0cc3f0121f5&oe=5649480D";
    private static final String KEY = "dev/users/00-dummy/TestToken/4c25ba9d45f7936772f61db6a1b6836d-15095.jpeg";

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

    protected Date dateFromString(String s) {
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    @Test
    public void it_hydrates_remote_images_correctly()
    {
        Image image = getNewRemoteImageInstance();
        Date d = dateFromString("2015-11-09 21:43:17");

        assertNull(image.getKey());
        assertNull(image.getChecksum());

        assertEquals("images", image.getType());
        assertEquals(3, image.getId());
        assertEquals(EXTERNAL_URL, image.getExternalUrl());
        assertEquals(d, image.getCreatedAt());
        assertEquals(d, image.getUpdatedAt());
        assertEquals("ImageTestToken", image.getDeviceToken());
        assertEquals(0, image.getBytesize());
        assertEquals(0, image.getWidth());
        assertEquals(0, image.getHeight());
    }

    @Test
    public void it_hydrates_local_images_correctly()
    {
        Image image = getNewLocalImageInstance();
        Date created = dateFromString("2015-11-16 02:05:41");
        Date updated = dateFromString("2015-11-16 02:05:51");

        assertNull(image.getExternalUrl());

        assertEquals("images", image.getType());
        assertEquals(9, image.getId());
        assertEquals(created, image.getCreatedAt());
        assertEquals(updated, image.getUpdatedAt());
        assertEquals("ImageTestToken", image.getDeviceToken());
        assertEquals(KEY, image.getKey());
        assertEquals("4c25ba9d45f7936772f61db6a1b6836d", image.getChecksum());
        assertEquals(15095, image.getBytesize());
        assertEquals(600, image.getWidth());
        assertEquals(600, image.getHeight());
    }


    @Test
    public void it_evaportates_remote_images_correctly()
    {
        Image image = getNewRemoteImageInstance();
        Map map = image.evaporate();
        Map data = (Map) map.get("data");
        Map attributes = (Map) data.get("attributes");

        assertNotNull(map);
        assertNotNull(data.get("attributes"));

        assertEquals(3l, data.get("id"));
        assertEquals("images", data.get("type"));
        assertEquals(EXTERNAL_URL, attributes.get("external_url"));
        assertNull(attributes.get("key"));
    }


    @Test
    public void it_evaportates_local_images_correctly()
    {
        Image image = getNewLocalImageInstance();
        Map map = image.evaporate();
        Map data = (Map) map.get("data");
        Map attributes = (Map) data.get("attributes");

        assertNotNull(map);
        assertNotNull(data.get("attributes"));

        assertEquals(9l, data.get("id"));
        assertEquals("images", data.get("type"));
        assertEquals(KEY, attributes.get("key"));
        assertEquals("4c25ba9d45f7936772f61db6a1b6836d", attributes.get("checksum"));
        assertEquals(15095l, attributes.get("bytesize"));
        assertEquals(600, attributes.get("pixel_width"));
        assertEquals(600, attributes.get("pixel_height"));

        assertNull(attributes.get("external_url"));
    }
}