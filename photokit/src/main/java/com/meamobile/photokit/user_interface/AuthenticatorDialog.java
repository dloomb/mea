package com.meamobile.photokit.user_interface;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthenticatorDialog extends AlertDialog
{
    public interface AuthenticatorDialogRedirectCallback
    {
        public void didHitRedirectUrl(AuthenticatorDialog dialog, String url);
    }

    private WebView mWebView;
    private AuthenticatorDialogRedirectCallback mRedirectCallback;
    private String mRedirectString;

    public AuthenticatorDialog(Context context)
    {
        super(context);

        mWebView = new WebView(context);
        mWebView.setWebViewClient(getWebViewClient());

        this.setView(mWebView);
        this.setButton(BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setAuthenticationUrl(String url)
    {
        mWebView.loadUrl(url);
    }

    public void setRedirectUrl(String url, AuthenticatorDialogRedirectCallback callback)
    {
        mRedirectString = url;
        mRedirectCallback = callback;
    }

    protected WebViewClient getWebViewClient()
    {
        final AuthenticatorDialog dialog = this;
        return new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("AuthenticatorDialog", url);

                if (url.startsWith(mRedirectString))
                {
                    if (mRedirectCallback != null)
                    {
                        mRedirectCallback.didHitRedirectUrl(dialog, url);
                    }
                    return false;
                }


                view.loadUrl(url);
                return true;
            }
        };
    }

}
