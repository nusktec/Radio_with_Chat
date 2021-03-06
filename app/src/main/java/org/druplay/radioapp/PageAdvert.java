package org.druplay.radioapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.airbnb.lottie.LottieAnimationView;

import im.delight.android.webview.AdvancedWebView;

public class PageAdvert extends Fragment implements AdvancedWebView.Listener {
    NSCLib nsc;
    Context context;
    AdvancedWebView webView;
    View viewR;
    LottieAnimationView loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.activity_page_adverts, container, false);
        nsc = new NSCLib(getActivity());
        context = getActivity();
        viewR = viewRoot;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Main();
            }
        },2000);
        return viewRoot;
    }

    @SuppressLint("SetJavaScriptEnabled")
    void Main() {
        webView = viewR.findViewById(R.id.awv);
        webView.setListener(getActivity(), this);
        loader = viewR.findViewById(R.id.loader);

        webView.loadUrl(nsc.API_ADVERTS + "?app_id=" + BuildConfig.APPLICATION_ID, true);
        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by
        if (!nsc.NSC7isConnected()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        viewR.setFocusableInTouchMode(true);
        viewR.requestFocus();
        viewR.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        if(webView.canGoBack()){
                            webView.goBack();
                            return true;
                        }
                    if(!webView.canGoBack()){
                        webView.loadUrl(nsc.API_ADVERTS);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        loader.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(String url) {
        loader.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        //nsc.NSCtoasy("You can also swipe to refresh");
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        loader.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        nsc.NSCtoasy("Failed to load adverts");
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

}
