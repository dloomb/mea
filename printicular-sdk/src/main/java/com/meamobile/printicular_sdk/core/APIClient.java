package com.meamobile.printicular_sdk.core;


import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.models.AccessToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class APIClient
{
    public interface APIClientCallback
    {
        void success(Map<String, Object> response);
        void error(String reason);
    }

    private String mBaseUrl;

    public APIClient(String baseUrl)
    {
        mBaseUrl = baseUrl;
    }

    {

    }

    public interface JSONHttpClientCallback
    {
        public void success(Map<String, Object> response);
        public void error(String error);
    }

    protected void request(final HttpUriRequest request, APIClientCallback callback)
    {
        final HttpUriRequest _request = request;
        final APIClientCallback _callback = callback;

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

    public void post(String url, Bundle parameters, APIClientCallback callback, AccessToken accessToken)
    {
        HttpPost post = new HttpPost(mBaseUrl + url);

        if(parameters != null)
        {
            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for(String key : parameters.keySet()){
                    Object value = parameters.get(key);
                    params.add(new BasicNameValuePair(key, (String) value));
                }

                post.setEntity(new UrlEncodedFormEntity(params));
            }
            catch (UnsupportedEncodingException e)
            {
                callback.error("Error parsing json parameters");
                return;
            }
        }

        if (accessToken != null)
        {
            post.addHeader("Authorization", "Bearer " + accessToken.toString());
        }

        request(post, callback);
    }

    public void get(String url, Bundle parameters, APIClientCallback callback, AccessToken accessToken)
    {
        if (parameters != null)
        {
            Uri.Builder builder = new Uri.Builder();

            Set<String> keys = parameters.keySet();

            for (String key: keys)
            {
                builder.appendQueryParameter(key, parameters.getString(key));
            }
            String query = builder.build().getQuery();

            if (query != null && query.length() != 0)
            {
                url += "?" + query;
            }
        }

        HttpGet get = new HttpGet(mBaseUrl + url);

        if (accessToken != null)
        {
            get.addHeader("Authorization", "Bearer " + accessToken.toString());
        }


        request(get, callback);
    }


}
