package org.druplay.radioapp;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.delight.android.webview.AdvancedWebView;

public class WriteReport extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView webview;
    NSCLib nsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_report);

        //setup for actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.flat_action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMix1)));
        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.action_bar_text)).setText(String.valueOf("Manage Reports"));
        view.findViewById(R.id.action_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nsc  = new NSCLib(this);
        webview = (AdvancedWebView) findViewById(R.id.report_wb);
        webview.setListener(this,this);
        webview.loadUrl(nsc.API_REPORT+nsc.NSC7TempStorage().getString(nsc.ACCOUNT,""));
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        webview.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(String url) {
        webview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        nsc.NSCtoasy("Please try again !");
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
