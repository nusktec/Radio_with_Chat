package org.druplay.radioapp;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import im.delight.android.location.SimpleLocation;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NSCLib {

    final public String DOMAIN_URL = "http://druplay.com/";
    final public String API_URL = DOMAIN_URL + "/app/api";
    final public String API_POST = DOMAIN_URL + "/app/posts";
    final public String API_ADVERTS = DOMAIN_URL + "/app/adverts";
    final public String API_REPORT = DOMAIN_URL + "/app/u-report?app_id="+BuildConfig.APPLICATION_ID+"&phone=";
    final public String API_POLICY = DOMAIN_URL + "/app/policy.html";
    final public String API_IMG_URL = DOMAIN_URL + "/app/PROFILE_IMG";
    final public String API_SLIDER_URL = DOMAIN_URL + "/app/APPS/" + BuildConfig.APPLICATION_ID+"/sliders";
    final public String APP_API_HEADER_KEY = "hdfkjalfADJKHEJWHWJH2353131mjnxczcjlcsjddjisdj==";
    final public String APP_KEY_HEADER = "nsc7-header-key";

    /**
     * Default share storage
     **/
    final static String IS_LOGGIN = "ACCOUNT_LOGGED";
    final public String ACCOUNT = "ACCOUNT";

    private Context context;

    private SimpleLocation simpleLocation;

    //Initialize context for this class
    public NSCLib(Context context) {
        this.context = context;

        //Disobey strict mode
        StrictMode.VmPolicy.Builder vmPolicy = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(vmPolicy.build());

        simpleLocation = new SimpleLocation(this.context, false, false, 5000);
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this.context);
            NSC7ShowToast("Please turn on location service");
        }
    }


    //Animation Loader
    public void nsc7AnimLoader(int i, LottieAnimationView lottieAnimationView) {
        lottieAnimationView.setAnimation(i);
        lottieAnimationView.playAnimation();
    }

    public void nsc7AnimStop(LottieAnimationView lottieAnimationView) {
        lottieAnimationView.cancelAnimation();
    }

    //Return public location for usage

    public SimpleLocation getSimpleLocation() {
        return simpleLocation;
    }

    public NSCLib(Context context, String tempStorageName) {
        this.context = context;
    }

    //Notification Service
    public int NSC7Notification(String title, String small_title, String message, int id, Class aClass, String meta_data) {
        Bitmap largeIcon = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            largeIcon = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.ic_launcher);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Logic for platforms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String noti_id = String.valueOf(id);
            final CharSequence noti_name = "Connect Alert";
            final int noti_importance = NotificationManager.IMPORTANCE_HIGH;

            //Apply it to a channel
            NotificationChannel notificationChannel = new NotificationChannel(noti_id, noti_name, noti_importance);
            notificationChannel.setDescription("Tell the world about it");
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        PendingIntent pendingIntent = null;
        if (aClass != null) {
            Intent intent = new Intent(this.context, aClass).putExtra("meta-data",meta_data);
            // Creating a pending intent and wrapping our intent
            pendingIntent = PendingIntent.getActivity(this.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intent = new Intent(this.context, ActivitySplash.class);
            // Creating a pending intent and wrapping our intent
            pendingIntent = PendingIntent.getActivity(this.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        //Now General Notification setup
        Notification.Builder notification = new Notification.Builder(this.context);
        notification.setSmallIcon(R.drawable.ic_stat_logo_white)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setSubText(small_title)
                .setContentText(message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(String.valueOf(id));
        }
        notification.setContentIntent(pendingIntent);
        assert notificationManager != null;
        notificationManager.notify(id, notification.build());

        return id;
    }

    //Toast Service
    public void NSC7ShowToast(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
    }

    //SharedPreference services
    public SharedPreferences NSC7TempStorage() {
        return this.context.getSharedPreferences("", Context.MODE_PRIVATE);
    }

    //Current Location LngLat
//    public String NSC7LocationLatLng() {
//        try {
//            LocationManager locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                return "";
//            }
//            assert locationManager != null;
//            locationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            double longitude = location.getLongitude();
//            double latitude = location.getLatitude();
//            if (longitude != 0 && latitude != 0) {
//                return String.valueOf(latitude) + "," + String.valueOf(longitude);
//            }
//        } catch (NullPointerException e) {
//            return "";
//        }
//        return "";
//    }


    //GETTING SINGLE SHARED STORAGE

    /**
     * System checking models
     * More cases and functions may fall under
     */
    //NSC7 Check for connectivity
    public boolean NSC7isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    //Permission prompt
    public String NSC7permissionLib() {
        //Read Locations
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return "Location";
        }
        //Read Storage
       if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            return "Storage";
        }
//        //Read Storage
//        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            //ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//            return "Record audio";
//        }
        //Read Storage
//        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
//            //ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//            return "Wake device";
//        }
        return "OK";
    }

    //Click to allow permission
    public void NSC7permissionClickAllow() {
        //Read Locations
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //Read Storage
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
        //Record
//        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.RECORD_AUDIO}, 3);
//        }
        //Wake
//        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(((Activity) this.context), new String[]{Manifest.permission.WAKE_LOCK}, 4);
//        }
    }


    //ShowTimed error on viewR
    public void NSC7showViewError(final TextView view, String message) {
        view.setText(message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setText("");
            }
        }, 10000);
    }

    //Nigeria phone no. filter
    public String NSC7FilerPhone(String enterRawPhone) {
        if (enterRawPhone.isEmpty()) {
            return "";
        }
        String phonebigining = enterRawPhone.substring(0, 1);
        String phoneConverted = enterRawPhone;
        if (phonebigining.equals("0") && enterRawPhone.length() < 12) {
            phoneConverted = "234" + enterRawPhone.substring(1, enterRawPhone.length());
        }
        if (!phonebigining.equals("0") && enterRawPhone.length() < 12 && !phonebigining.equals("2")) {
            phoneConverted = "234" + enterRawPhone.substring(0, enterRawPhone.length());
        }
        return phoneConverted;
    }

    //get formatted goot url
    public String getMapUrl(String latlng) {
        return "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlng + "&sensor=true&key=AIzaSyCCoa1aw4uenToO8qty2K8wiYNfIKeUK2k";
    }

    //Play notifcations
    public void NSC7PlayNoti() {
        //MediaPlayer mediaPlayer = MediaPlayer.create(this.context, R.raw.smsnoti);
        //mediaPlayer.start();
    }

    //Toasy
    public void NSCtoasy(String msg) {
        Toasty.normal(context, msg, Toast.LENGTH_LONG).show();
    }

    //DialogPresenter
    public EditText InputdialogPop(String hint, String title, String msg, SweetAlertDialog.OnSweetClickListener onSweetClickListener) {
        TextView textView = new TextView(context);
        textView.setText(String.valueOf(msg));
        textView.setPadding(10, 0, 10, 10);
        textView.setSingleLine(true);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setFocusableInTouchMode(true);
        textView.setFreezesText(true);
        textView.setSingleLine(true);
        textView.setMarqueeRepeatLimit(-1);
        textView.setFocusable(true);
        textView.setSelected(true);
        textView.setHorizontallyScrolling(true);

        EditText editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.curved_edittext);
        editText.setHint(String.valueOf(hint));
        editText.setPadding(25, 20, 25, 20);
        editText.setWidth(450);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textView);
        linearLayout.addView(editText);

        final SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        alertDialog.setTitleText(title);
        alertDialog.setContentText(msg);
        alertDialog.setCustomView(linearLayout);
        alertDialog.setCancelable(false);

        alertDialog.setConfirmButton("Confirm", onSweetClickListener);
        alertDialog.setCancelButton("Exit App", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                alertDialog.dismiss();
                ((Activity) context).finish();
            }
        });
        alertDialog.show();
        return editText;
    }

    //View visibilty activator
    public void viewEnabler(final View v, int duration, final String btnText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(View.VISIBLE);
                v.setEnabled(true);
                if (v instanceof Button) {
                    ((Button) v).setText(String.valueOf(btnText));
                }
            }
        }, duration);
    }


    private Address address;

    //GeoCoder
    public String getFullAddress(double latitude, double longitude) {
        String s = "";
        try {
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address_ = addresses.get(0);
                String street = address_.getThoroughfare();
                if (address_.getThoroughfare() == null) {
                    street = "Somewhere";
                }
                s = street + " in " + address_.getLocality() + " " + address_.getAdminArea() + ", " + address_.getCountryName();
                this.address = address_;
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return s;
    }

    public Address getAddress() {
        return address;
    }

    public String getOnlyLatLong() {
        return String.valueOf(getSimpleLocation().getLatitude()) + "," + String.valueOf(getSimpleLocation().getLongitude());
    }

    Handler handler = new Handler();
    public void autoStartLocation(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getOnlyLatLong();
                handler.postDelayed(this, 5000);
            }
        });
    }

    //ShowCasing
    public void showCase(List<SHOW_CASE_ITEM> case_itemArray){
        ShowcaseConfig config =  new ShowcaseConfig();
        config.setDelay(500);
        config.setFadeDuration(100);
        config.setDismissTextColor(Color.YELLOW);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(((Activity) context));
        sequence.setConfig(config);
        for (int i=0; i<=case_itemArray.size()-1; i++){
            sequence.addSequenceItem(case_itemArray.get(i).getViewItem(),case_itemArray.get(i).getMsg(),case_itemArray.get(i).getBtnTxt());
        }
        sequence.start();
    }

    public String bitMap64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //User updater
    public void updateUser(String grp, Object value, final boolean debugMode){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("act", "update-data");
        requestParams.put("phone", NSC7TempStorage().getString(ACCOUNT, "No User"));
        requestParams.put("grp", grp);
        requestParams.put("data", value);
        asyncHttpClient.addHeader("nsckey", APP_API_HEADER_KEY);
        asyncHttpClient.post(API_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if(debugMode){
                    NSC7ShowToast("Failed to update");
                }
            }

            @Override
            public void onStart() {
                if(debugMode){
                    NSC7ShowToast("Updating...");
                }
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        if(debugMode){
                            NSC7ShowToast("Updated");
                        }
                    } else {
                        if(debugMode){
                            NSC7ShowToast("Server error | On update");
                        }
                    }
                } catch (JSONException ex) {
                    Log.e("ReadUser Error", ex.getMessage());
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }
}

class SHOW_CASE_ITEM{
    private View viewItem;
    private String msg;
    private String btnTxt;

    public SHOW_CASE_ITEM(){
    }

    public SHOW_CASE_ITEM(View itView, String msg, String btntext){
        setViewItem(itView);
        setMsg(msg);
        setBtnTxt(btntext);
    }

    public View getViewItem() {
        return viewItem;
    }

    public void setViewItem(View viewItem) {
        this.viewItem = viewItem;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBtnTxt() {
        return btnTxt;
    }

    public void setBtnTxt(String btnTxt) {
        this.btnTxt = btnTxt;
    }
}
