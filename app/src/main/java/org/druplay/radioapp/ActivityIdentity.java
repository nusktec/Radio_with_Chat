package org.druplay.radioapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.egistli.droidimagepicker.ImagePicker;
import com.egistli.droidimagepicker.ImagePickerDelegate;
import com.egistli.droidimagepicker.ImagePickerError;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;

import net.karthikraj.shapesimage.ShapesImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityIdentity extends AppCompatActivity implements ImagePickerDelegate {

    EditText txtfname, txtphone;
    ShapesImage circleImageView;
    Bitmap imgBitmap;
    Button btnNext;
    NSCLib nsc;
    Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ubuntu.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_identity);
        //setup for actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.flat_action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMix1)));
        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.action_bar_text)).setText(String.valueOf(getResources().getString(R.string.setup_text)));
        view.findViewById(R.id.action_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nsc = new NSCLib(this);
        context = this;
        nsc.autoStartLocation();

        btnNext = findViewById(R.id.btnNext);
        txtfname = findViewById(R.id.txtfname);
        txtphone = findViewById(R.id.txtphone);
        circleImageView = findViewById(R.id.imgProfile);
        //Policy links
        findViewById(R.id.linkPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityPolicy.class));
            }
        });

        //OnClick Image
        circleImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imgUploader();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtfname.getText().toString().isEmpty() || txtphone.getText().toString().length() < 11) {
                    nsc.NSCtoasy("Complete the form before you continue...");
                }else{
                    subDetails();
                }

            }
        });

        //showcasing
        List<SHOW_CASE_ITEM> showCaseItems = new ArrayList<>();
        showCaseItems.add(new SHOW_CASE_ITEM(circleImageView,"You can tap on the image avatar to upload your profile picture","Next"));
        showCaseItems.add(new SHOW_CASE_ITEM(txtfname,"When typing your names, please ensure that it's your full names. eg Revelation K.F","Next"));
        showCaseItems.add(new SHOW_CASE_ITEM(txtphone,"Your phone number could be one of this format. 0801xxx or 23480xxxx, avoid using +234","Next"));
        showCaseItems.add(new SHOW_CASE_ITEM(btnNext,"Finally, here you click to begin... Let join the rights connect family","Got It"));
        nsc.showCase(showCaseItems);

        verifyCode();
    }

    void subDetails(){
        if(imgBitmap ==null){
            imgUploader();
            return;
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("act", "adduser");
        requestParams.put("name", txtfname.getText().toString());
        requestParams.put("phone", txtphone.getText().toString());
        requestParams.put("latlng", nsc.getOnlyLatLong());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        requestParams.put("pimg", nsc.bitMap64(imgBitmap));
        requestParams.put("app_id", BuildConfig.APPLICATION_ID);
        asyncHttpClient.addHeader("nsckey", nsc.APP_API_HEADER_KEY);
        asyncHttpClient.post(nsc.API_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                txtfname.setEnabled(false);
                txtphone.setEnabled(false);
                nsc.NSCtoasy("Joining, please wait !");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        //do success here
                        nsc.NSC7TempStorage().edit().putString(nsc.ACCOUNT, response.getJSONObject("meta-data").getString("phone")).apply();
                        SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                        alertDialog.setTitleText("Welcome");
                        alertDialog.setContentText(response.getString("message"));
                        alertDialog.setConfirmButton("Thanks", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(context, MainActivity.class));
                                finish();
                            }
                        });
                        alertDialog.show();
                    } else {
                        //reject issues here
                        txtfname.setEnabled(true);
                        txtphone.setEnabled(true);
                    }
                } catch (JSONException ex) {
                    //silence kill you here
                    nsc.NSCtoasy(ex.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                nsc.NSCtoasy("Unable to join at the moment, try again " + responseString);
                txtfname.setEnabled(true);
                txtphone.setEnabled(true);
            }
        });
    }

    void verifyCode(){

    }

    ImagePicker imagePicker;
    //Uploader
    void imgUploader() {
        imagePicker = new ImagePicker(this, this, 1024);
        imagePicker.prompt();
    }

    //Backward capability
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void imagePickerDidCancel(ImagePicker imagePicker) {
        imgBitmap = null;
    }

    @Override
    public void imagePickerDidSelectImage(ImagePicker imagePicker, Bitmap bitmap) {
        if(bitmap!=null){
            circleImageView.setImageBitmap(bitmap);
            imgBitmap = bitmap;
        }
    }

    @Override
    public void imagePickerDidFailToSelectImage(ImagePicker imagePicker, ImagePickerError error) {
        imgBitmap = null;
    }

    @Override
    public boolean imagePickerShouldCrop(ImagePicker imagePicker) {
        return true;
    }

    @Override
    public void imagePickerSetUpCropDetail(ImagePicker imagePicker, Crop crop) {
        crop.asSquare();
        crop.withMaxSize(512,512);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(imagePicker!=null && imagePicker.handleActivityResult(requestCode,resultCode,data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
