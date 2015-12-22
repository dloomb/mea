package com.meamobile.photokit.core;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDefaults
{
    private static UserDefaults mInstance = null;
    private Context mContext;

    public static UserDefaults getInstance(){
        if(mInstance == null)
        {
            mInstance = new UserDefaults();
        }
        return mInstance;
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public String stringForKey(String key)
    {
        SharedPreferences prefs = mContext.getSharedPreferences("USER_DEFAULTS", Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public void setStringValueForKey(String value, String key)
    {
        SharedPreferences prefs = mContext.getSharedPreferences("USER_DEFAULTS", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(key,value);
        e.commit();
    }

}
