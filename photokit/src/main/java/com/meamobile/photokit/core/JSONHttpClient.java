package com.meamobile.photokit.core;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class JSONHttpClient
{
    private static final String TAG = "MEA.JSONHttpClient";

    public Observable<Map<String, Object>> post(String urlString, Map<String, String> parameters)
    {
        return post(urlString, parameters, false);
    }


    public Observable<Map<String, Object>> post(String urlString, Map<String, String> parameters, boolean formUrlEncoded)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");

            byte[] content = formUrlEncoded
                    ? contentForFormURLEncoding(connection, parameters)
                    : contentForJSONEncoding(connection, parameters);

            return request(connection, content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Observable.error(e);
        }
    }



    public Observable<Map<String, Object>> get(String urlString, Map<String, String> parameters)
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
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");

            return request(connection, null)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    public Observable<File> getFile(String urlString, File output)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");

            return Observable.create(

                    new Observable.OnSubscribe<File>()
                    {
                        @Override
                        public void call(Subscriber<? super File> subscriber)
                        {
                            connection.setUseCaches(false);
                            connection.setDoInput(true);
                            try
                            {
                                connection.connect();
                                int responseCode = connection.getResponseCode();

                                InputStream inputStream = connection.getInputStream();
                                copyInputStreamToFile(inputStream, output);

                                subscriber.onNext(output);
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
        catch (Exception e)
        {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    protected Observable<Map<String, Object>> request(HttpURLConnection connection)
    {
        return request(connection, null);
    }

    protected Observable<Map<String, Object>> request(HttpURLConnection connection, byte[] content)
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


                            if (content != null)
                            {
                                connection.setDoOutput(true);
                                connection.getOutputStream().write(content);

//                                connection.setFixedLengthStreamingMode(content.length);
//                                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
//                                os.write(content);
//                                os.flush();
//                                os.close();
                            }

                            connection.connect();
                            int responseCode = connection.getResponseCode();

                            InputStream inputStream = null;
                            try
                            {
                                inputStream = connection.getInputStream();
                            }
                            catch (Exception e)
                            {
                                inputStream = connection.getErrorStream();
                            }

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

        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected byte[] contentForFormURLEncoding(HttpURLConnection connection, Map<String, String> parameters) throws UnsupportedEncodingException
    {
        Set set = parameters.entrySet();
        Iterator i = set.iterator();
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

        return postDataBytes;
    }

    protected byte[] contentForJSONEncoding(HttpURLConnection connection, Map<String, String> parameters)
    {
        byte[] content = new Gson().toJson(parameters).getBytes();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", "" + content.length);
        return content;
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


    protected void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
