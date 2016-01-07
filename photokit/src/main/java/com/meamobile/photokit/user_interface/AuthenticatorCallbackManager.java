package com.meamobile.photokit.user_interface;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorCallbackManager
{
    public interface OnResultListener
    {
        boolean handleResult(final int requestCode, final int resultCode, final Intent data);
        void onResume();
    }

    private static AuthenticatorCallbackManager mInstance = null;
    private List<OnResultListener> mListeners;

    public static AuthenticatorCallbackManager getInstance(){
        if(mInstance == null)
        {
            mInstance = new AuthenticatorCallbackManager();
            mInstance.mListeners = new ArrayList<OnResultListener>();
        }
        return mInstance;
    }

    public boolean onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        for (OnResultListener listener : mListeners)
        {
            if (listener.handleResult(requestCode, resultCode, data))
            {
                return true;
            }
        }

        return false;
    }

    public void onResume()
    {
        for (OnResultListener listener : mListeners)
        {
            listener.onResume();
        }
    }

    public void addListener(OnResultListener listener)
    {
        mListeners.add(listener);
    }

    public void removeListener(OnResultListener listener)
    {
        mListeners.remove(listener);
    }
}
