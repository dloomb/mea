package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.meamobile.photokit.R;

public class AuthenticatorActivity extends ActionBarActivity
{
    public static String TITLE = "com.meamobile.photokit.authenticator.title";
    public static String AUTH_URL = "com.meamobile.photokit.authenticator.auth_url";
    public static String REDIRECT_URL = "com.meamobile.photokit.authenticator.redirect_url";

    private static String TAG = "MEA.AuthenticatorActivity";

    private WebView mWebView;
    private RelativeLayout mLoadingIndicator;
    private String mRedirectString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        mLoadingIndicator = (RelativeLayout) findViewById(R.id.loadingBlocker);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(getWebViewClient());

        clearCookies();
        setupWebView();

        Intent i = getIntent();
        String title = i.getStringExtra(TITLE);
        setTitle(title != null ? title : "Login");
        mRedirectString = i.getStringExtra(REDIRECT_URL);
        mWebView.loadUrl(i.getStringExtra(AUTH_URL));
    }

    public void setAuthenticationUrl(String url)
    {
        mWebView.loadUrl(url);
    }

    protected WebViewClient getWebViewClient()
    {
        return new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d(TAG, "Should Load: " + url);

                if (mRedirectString != null && url.startsWith(mRedirectString))
                {
                    onRedirectHit(url);

                    return false;
                }


                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        };
    }


    protected void clearCookies()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            android.webkit.CookieManager.getInstance().removeAllCookies(null);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            cookieSyncManager.startSync();

            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.startSync();
        }
    }

    protected void setupWebView()
    {
        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mWebView.setInitialScale(1);
    }

    protected void onRedirectHit(String url)
    {
        Intent i = new Intent();
        i.putExtra(REDIRECT_URL, url);

        if (getParent() == null)
        {
            setResult(Activity.RESULT_OK, i);
        }
        else
        {
            getParent().setResult(Activity.RESULT_OK, i);
        }
        finish();
    }

}
