package com.meamobile.printicular_sdk.core;


import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meamobile.printicular_sdk.core.models.AccessToken;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class APIClient
{
    private static final String TAG = "MEA.APIClient";

    private String mBaseUrl;

    public APIClient(String baseUrl)
    {
        mBaseUrl = baseUrl;
    }

    protected String readStream(InputStream in) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String nextLine = "";
        while ((nextLine = reader.readLine()) != null)
        {
            sb.append(nextLine);
        }

        return sb.toString();
    }

    protected Observable<Map<String, Object>> request(HttpURLConnection connection, AccessToken accessToken)
    {
        return request(connection, accessToken, null);
    }

    protected Observable<Map<String, Object>> request(HttpURLConnection connection, AccessToken accessToken, String content)
    {
        return Observable.create(

                new Observable.OnSubscribe<Map<String, Object>>()
                {
                    @Override
                    public void call(Subscriber<? super Map<String, Object>> subscriber)
                    {
                        try
                        {
                            connection.setUseCaches(false);
                            connection.setDoInput(true);


                            if (accessToken != null)
                            {
                                String bearer = "Bearer " + accessToken.toString();
                                connection.setRequestProperty("Authorization", bearer);
                            }


                            if (content != null)
                            {
                                connection.setDoOutput(true);

                                byte[] bytes = content.getBytes();
                                connection.setFixedLengthStreamingMode(bytes.length);
                                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                                os.write(content.getBytes());
                                os.flush();
                                os.close();
                            }

                            connection.connect();
                            int responseCode = connection.getResponseCode();

                            InputStream inputStream = connection.getInputStream();
                            String json = readStream(inputStream);
                            inputStream.close();
                            Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>()
                            {
                            }.getType());

                            subscriber.onNext(map);
                            subscriber.onCompleted();

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }

        );
    }




    public Observable<Map<String, Object>> post(String urlString, Map parameters)
    {
        return post(urlString, parameters, null);
    }

    public Observable<Map<String, Object>> post(String urlString, Map parameters, AccessToken accessToken)
    {
        String content = new Gson().toJson(parameters);

        try
        {
            URL url = new URL(mBaseUrl + urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + content.length());
            connection.setRequestProperty("Content-Language", "en-US");

            return request(connection, accessToken, content)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Observable.error(e);
        }
    }




    public Observable<Map<String, Object>> get(String urlString, Map<String, String> parameters)
    {
        return get(urlString, parameters);
    }

    public Observable<Map<String, Object>> get(String urlString, Map<String, String> parameters, AccessToken accessToken)
    {
        if (parameters != null)
        {
            Uri.Builder builder = new Uri.Builder();
            Set<String> keys = parameters.keySet();
            for (String key: keys)
            {
                builder.appendQueryParameter(key, parameters.get(key));
            }
            String query = builder.build().getQuery();
            if (query != null && query.length() != 0)
            {
                urlString += "?" + query;
            }
        }

        try
        {
            URL url = new URL(mBaseUrl + urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");

            return request(connection, accessToken)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Observable.error(e);
        }
    }


}
