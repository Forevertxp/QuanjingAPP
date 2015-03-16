package com.quanjing.weitu.app.ui.user;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.quanjing.weitu.app.ui.common.MWTBaseActivity;

public class MWTLicenseActivity extends MWTBaseActivity
{
    private WebView _webView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _webView = new WebView(this);
        _webView.loadUrl("file:///android_asset/license.html");
        _webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });

        this.setContentView(_webView);
    }
}
