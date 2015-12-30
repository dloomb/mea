package com.meamobile.photokit.photobucket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.user_interface.AuthenticatorActivity;
import com.meamobile.photokit.user_interface.AuthenticatorDialog;

@SuppressLint("ParcelCreator")
public class PhotobucketSource extends Source
{
    private static String TAG = "MEA.PhotobucketSource";

    private String mConsumerKey, mConsumerSecret;

    public PhotobucketSource()
    {
        this.Title = "Photobucket";
        this.ImageResourceId = R.drawable.photobucket_badge;

        mConsumerKey = "149833797";
        mConsumerSecret = "4a90d33bbac6183f8f3a389b4fbab300";
    }


    @Override
    public void activateSource(Activity activity, SourceActivationCallback callback)
    {
        final Activity _activity = activity;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                OAuthService service = new ServiceBuilder()
                        .provider(PhotobucketScribeAPI.class)
                        .apiKey(mConsumerKey)
                        .apiSecret(mConsumerSecret)
                        .build();

                Token token = service.getRequestToken();
                final String authUrl = service.getAuthorizationUrl(token);

                _activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showAuthenticatorDialogWithUrl(_activity, authUrl);
                    }
                });
            }
        }).start();
    }

    private void showAuthenticatorDialogWithUrl(Context context, String url)
    {
        Intent i = new Intent(context, AuthenticatorActivity.class);
        i.putExtra("AUTH_URL", url);
//        context.startActivity(i);
        AuthenticatorDialog dialog = new AuthenticatorDialog(context);
        dialog.setAuthenticationUrl(url);
//                dialog.setRedirectUrl(redirectUrl, getAuthenticatorDialogCallback(redirectUrl));
        dialog.show();
    }

}
