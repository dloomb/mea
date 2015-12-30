package com.meamobile.photokit.user_interface;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.meamobile.photokit.R;

public class AuthenticatorActivity extends ActionBarActivity
{
    private static String TAG = "MEA.AuthenticatorActivity";

    public interface AuthenticatorActivityRedirectCallback
    {
        void didHitRedirectUrl(AuthenticatorActivity activity, String url);
    }

    private WebView mWebView;
    private FrameLayout mLoadingIndicator;
    private AuthenticatorActivityRedirectCallback mRedirectCallback;
    private String mRedirectString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(getWebViewClient());

        mWebView.loadUrl(getIntent().getStringExtra("AUTH_URL"));
    }

    public void setAuthenticationUrl(String url)
    {
        mWebView.loadUrl(url);
    }

    public void setRedirectUrl(String url, AuthenticatorActivityRedirectCallback callback)
    {
        mRedirectString = url;
        mRedirectCallback = callback;
    }

    protected WebViewClient getWebViewClient()
    {
        return new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d(TAG, "Should Load: " + url);

                if (mRedirectCallback != null && url.startsWith(mRedirectString))
                {
                    if (mRedirectCallback != null)
                    {
                        mRedirectCallback.didHitRedirectUrl(AuthenticatorActivity.this, url);
                    }
                    return false;
                }


                view.loadUrl(url);
                return true;
            }

//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                params.gravity = Gravity.CENTER;
//                mWebView.setLayoutParams(params);
//
//                mLoadingIndicator.setVisibility(View.INVISIBLE);
//            }
        };
    }
}
