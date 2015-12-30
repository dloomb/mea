package com.meamobile.photokit.user_interface;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.meamobile.photokit.R;


public class AuthenticatorDialog extends Dialog
{
    private static String TAG = "MEA.AuthenticatorDialog";

    public interface AuthenticatorDialogRedirectCallback
    {
        public void didHitRedirectUrl(AuthenticatorDialog dialog, String url);
    }

    private WebView mWebView;
    private FrameLayout mLoadingIndicator;
    private AuthenticatorDialogRedirectCallback mRedirectCallback;
    private String mRedirectString;

    public AuthenticatorDialog(Context context)
    {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.template_authenticator_dialog);

        mWebView = (WebView) this.findViewById(R.id.webView);
        mWebView.setWebViewClient(getWebViewClient());

        mWebView.setInitialScale(1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);

        mLoadingIndicator = (FrameLayout) this.findViewById(R.id.loadingFrameLayout);

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

                Log.d(TAG, "Should Load: " + url);

                if (mRedirectCallback != null && url.startsWith(mRedirectString))
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

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                mWebView.setLayoutParams(params);

                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        };
    }

}
