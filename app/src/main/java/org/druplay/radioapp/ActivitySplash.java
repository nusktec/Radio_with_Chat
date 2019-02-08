package org.druplay.radioapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySplash extends Activity {
    private Context context;
    private LinearLayout container;
    private Button btnNextScreen;
    private TextView txtInfo;
    private NSCLib nscLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ubuntu.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        context = this;
        setContentView(R.layout.activity_splash);
        //set welcome app text name
        ((HtmlTextView) findViewById(R.id.welcome_app_name)).setHtml(String.valueOf("<b><font color='#000'>Rights</font></b><font color='#ff2a52'> Connect</font>"));
        btnNextScreen = findViewById(R.id.btnNext);
        nscLib = new NSCLib(this);
        Main();
    }

    public void Main(){
        checkSystemConfig();
        btnNextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(systemChecked){
                    //perform good activity of sign up here
                    if (nscLib.NSC7TempStorage().getString(nscLib.ACCOUNT, null) != null) {
                        //open to add new account
                        startActivity(new Intent(context, MainActivity.class));
                        //finish();
                        return;
                    }
                    startActivity(new Intent(context, ActivityIdentity.class));
                    return;
                }
                checkSystemConfig();
                nscLib.NSC7permissionClickAllow();
            }
        });
    }

    //fonts
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    boolean systemChecked = false;
    String error_info = "";
    //Start checking systems
    private void checkSystemConfig() {

        //check for network
        if (!nscLib.NSC7isConnected()) {
            error_info = String.valueOf("Your Mobile data / Wifi is off !");
            nscLib.NSCtoasy(error_info);
            return;
        }
        //Check for location
        String locationStatus = nscLib.NSC7permissionLib();
        if (!locationStatus.equals("OK")) {
            error_info = String.valueOf("Allow your device to access " + locationStatus);
            nscLib.NSCtoasy(error_info);
            return;
        }

        //All things are well done....
        error_info = String.valueOf("Now Ready On Mobile");

        //Setup ready, can sign-up
        systemChecked = true;
    }

    //Check permissions status


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        nscLib.NSCtoasy(error_info);
        btnNextScreen.setText(String.valueOf("Press Again"));
        Main();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
