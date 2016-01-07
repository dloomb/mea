package com.meamobile.photokit.dropbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxUsers;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

import com.dropbox.core.android.Auth;

import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

import java.util.Locale;

@SuppressLint("ParcelCreator")
public class DropboxSource extends Source
{
    public static String APP_KEY;
    private static final String DROPBOX_ACCESS_TOKEN = "com.meamobile.photokit.dropbox.access_token";

    private String mEmail, mAccessToken;

    public DropboxSource()
    {
        mTitle = "Dropbox";
        mImageResourceId = R.drawable.dropbox_badge;

        mAccessToken = Auth.getOAuth2Token();
        int i = 0;

        loadAccessToken();
        if (isActive())
        {
            fetchCurrentAccountDetails();
        }
    }

    @Override
    public boolean isActive()
    {
        return mAccessToken != null;
    }

    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        super.activateSource(activity, callback);

        Auth.startOAuth2Authentication(activity, APP_KEY);

        AuthenticatorCallbackManager.getInstance().addListener(new AuthenticatorCallbackManager.OnResultListener()
        {
            //ignored
            @Override public boolean handleResult(int requestCode, int resultCode, Intent data) {return false;}

            @Override
            public void onResume()
            {
                AuthenticatorCallbackManager.getInstance().removeListener(this);

                mAccessToken = Auth.getOAuth2Token();
                if (mAccessToken != null)
                {
                    saveAccessToken();

                    handleSourceActivation(true, null);
                }
                else
                {
                    handleSourceActivation(false, "Dropbox OAuth2Token was null");
                }
            }
        });
    }

    @Override
    public int getBrandColor()
    {
        return 0xFF007ee5;
    }

    public DbxClientV2 getNewClient()
    {
        DbxRequestConfig config = new DbxRequestConfig("com.meamobile.photokit", Locale.getDefault().getLanguage());
        DbxClientV2 client = new DbxClientV2(config, mAccessToken);
        return client;
    }

    private void fetchCurrentAccountDetails()
    {
        new Thread(new Runnable() { @Override public void run()
        {
            DbxClientV2 client = getNewClient();
            try
            {
                DbxUsers.FullAccount account = client.users.getCurrentAccount();
                mEmail = account.email;
                mUsername = account.name.displayName;
            }
            catch (DbxException e)
            {
                e.printStackTrace();
            }
        }}).start();
    }

    private void loadAccessToken()
    {
        mAccessToken = UserDefaults.getInstance().stringForKey(DROPBOX_ACCESS_TOKEN);//"qD28WmbGyoIAAAAAAAAKODDbINBNKHJBFLTY_rRFbYeA-LTo4NhHqL0nV25UG1Gx";// ;
    }

    private void saveAccessToken()
    {
        UserDefaults.getInstance().setStringValueForKey(mAccessToken, DROPBOX_ACCESS_TOKEN);
    }
}
