package com.meamobile.printicular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.CallbackManager;
import com.meamobile.photokit.user_interface.AuthenticatorCallbackManager;

public class AuthenticatableActivity extends ActionBarActivity
{
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean handled = mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (!handled)
        {
            handled = AuthenticatorCallbackManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AuthenticatorCallbackManager.getInstance().onResume();
    }
}
