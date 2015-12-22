package com.meamobile.photokit.core;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class JSONHttpClient
{
    public interface JSONHttpClientCallback
    {
        public void success(Map<String, Object> response);
        public void error(String error);
    }

    public JSONHttpClient()
    {

    }

    protected void request(final HttpUriRequest request, JSONHttpClientCallback callback)
    {
        final HttpUriRequest _request = request;
        final JSONHttpClientCallback _callback = callback;

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                DefaultHttpClient client = new DefaultHttpClient();

                try
                {
                    HttpResponse response = client.execute(_request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    Map<String, Object> object = new Gson().fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
                    _callback.success(object);
                }
                catch (Exception e)
                {
                    _callback.error(e.getLocalizedMessage());
                }
            }

        }).start();
    }

    public void post(String url, List<NameValuePair> parameters, JSONHttpClientCallback callback)
    {
        HttpPost post = new HttpPost(url);
        if(parameters != null)
        {
            try
            {
                post.setEntity(new UrlEncodedFormEntity(parameters));
            }
            catch (UnsupportedEncodingException e)
            {
                callback.error("Error parsing json parameters");
                return;
            }
        }

        request(post, callback);



    }

    public void get(String url, JSONHttpClientCallback callback)
    {
        HttpGet get = new HttpGet(url);
        request(get, callback);
    }

}
