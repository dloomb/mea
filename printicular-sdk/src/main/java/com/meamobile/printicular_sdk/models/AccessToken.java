package com.meamobile.printicular_sdk.models;


import android.app.Activity;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccessToken
{
    private static String ACCESS_TOKEN_FILENAME = "com.meamobile.printicular_sdk.access_token";

    private Date mExpiryDate;
    private String mToken;
    private String mRefreshToken;

    public AccessToken(String token, String refresh, Date expiry)
    {
        mToken = token;
        mRefreshToken = refresh;
        mExpiryDate = expiry;
    }

    private AccessToken() {}

    @Override
    public String toString()
    {
        return mToken;
    }

    public static AccessToken loadToken(Context context) throws Exception
    {
        FileInputStream fis = context.openFileInput(ACCESS_TOKEN_FILENAME);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String receiveString = "";
        StringBuilder sb = new StringBuilder();

        while ((receiveString = br.readLine()) != null)
        {
            sb.append(receiveString);
        }
        fis.close();

        if (sb.length() != 0)
        {
            JSONObject object = new JSONObject(sb.toString());

            AccessToken at = new AccessToken();
            at.mToken = object.getString("access_token");
            at.mRefreshToken = object.has("refresh_token") ? object.getString("refresh_token") : null;
            at.mExpiryDate = new Date(object.getLong("expiry"));

            return at;
        }
        return null;
    }

    public void storeToken(Context context) throws Exception
    {
        JSONObject object = new JSONObject();
        object.put("access_token", mToken);
        object.put("refresh_token", mRefreshToken);
        object.put("expiry", mExpiryDate.getTime());

        FileOutputStream fos = context.openFileOutput(ACCESS_TOKEN_FILENAME, Context.MODE_PRIVATE);
        fos.write(object.toString().getBytes());
        fos.close();
    }

    public void deleteToken(Context context)
    {
        context.deleteFile(ACCESS_TOKEN_FILENAME);
    }

    public boolean hasExpired()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -86400);
        Date dayago = calendar.getTime();
        return mExpiryDate.before(dayago);
    }



}
