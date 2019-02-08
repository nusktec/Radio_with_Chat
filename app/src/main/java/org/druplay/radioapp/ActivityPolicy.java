package org.druplay.radioapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import cz.msebera.android.httpclient.Header;

public class ActivityPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setElevation(2);
        actionBar.setTitle(String.valueOf("Privacy Policy"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(new NSCLib(this).API_POLICY, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(ActivityPolicy.this, "Please try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ((HtmlTextView)findViewById(R.id.policy_txt)).setHtml(s);
            }
        });
    }
}
