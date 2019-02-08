package org.druplay.radioapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PageHome extends Fragment {
    NSCLib nsc;
    Context context;
    View viewR;
    SliderLayout sliderLayout;
    JcPlayerView jcAudio;
    LottieAnimationView animationView;
    boolean loaded_ = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.activity_page_home, container, false);
        nsc = new NSCLib(getActivity());
        context = getActivity();
        viewR = viewRoot;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Main();
            }
        }, 2000);
        return viewRoot;
    }

    @SuppressLint("ClickableViewAccessibility")
    void Main() {
        jcAudio = viewR.findViewById(R.id.jcplayer);
        sliderLayout = viewR.findViewById(R.id.imageSlider);
        animationView = viewR.findViewById(R.id.loader);

        jcAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setY(event.getRawY() - (v.getHeight()));
                    v.setX(event.getRawX() - (v.getWidth()) / 2);
                }
                return true;
            }
        });

        //Start network approach
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("act", "read-radio");
        requestParams.put("app_id", BuildConfig.APPLICATION_ID);
        asyncHttpClient.addHeader("nsckey", nsc.APP_API_HEADER_KEY);
        asyncHttpClient.setMaxRetriesAndTimeout(5,10000);
        asyncHttpClient.post(nsc.API_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                nsc.NSCtoasy("Radio server is down...");
            }

            @Override
            public void onStart() {
                //Die in silence
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        //Load radio data
                        loadRadio(response.getJSONObject("data1"));
                        //load slider
                        setSliderViews(response);
                        loaded_ = true;
                    } else {
                        nsc.NSCtoasy(response.getString("message"));
                    }
                } catch (JSONException ex) {
                    Log.e("Read Radio Error", ex.getMessage());
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }


    //Load radio
    private void loadRadio(JSONObject jsonObject) {
        try{
            if(!jsonObject.getString("set_asl").isEmpty()){
                //Off loader
                animationView.cancelAnimation();
                animationView.setVisibility(View.GONE);
                jcAudio.setVisibility(View.VISIBLE);
                ArrayList<JcAudio> jcAudios = new ArrayList<>();
                jcAudios.add(JcAudio.createFromURL(jsonObject.getString("app_name"), jsonObject.getString("set_asl")));
                jcAudios.add(JcAudio.createFromURL(jsonObject.getString("app_name")+" Temporal", jsonObject.getString("set_aasl")));
                jcAudio.initPlaylist(jcAudios, null);
                jcAudio.continueAudio();
                return;
            }
            nsc.NSCtoasy("Radio has not set ASL");
        }catch (JSONException ex){
            //die in silence
        }
    }

    private void setSliderViews(JSONObject jsonObject) {
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        sliderLayout.setScrollTimeInSec(5);
        sliderLayout.setVisibility(View.VISIBLE);

        try{
            if(jsonObject.length()>0){
                JSONArray jsonArray = jsonObject.getJSONArray("data2");
                for(int i = 0; i<jsonArray.length(); i++){

                    final JSONObject jo = jsonArray.getJSONObject(i);

                    SliderView sliderView = new SliderView(getActivity());
                    sliderView.setImageUrl(nsc.API_SLIDER_URL+"/"+jo.getString("slider_finder"));
                    sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                    sliderView.setDescription(jo.getString("slider_desc"));
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            try {
                                if(!jo.getString("slider_link").isEmpty()&& URLUtil.isValidUrl(jo.getString("slider_link"))){
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(jo.getString("slider_link"))));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //at last add this view in your layout :
                    sliderLayout.addSliderView(sliderView);
                }
            }
        }catch (JSONException ex){
            //die in silence
        }

    }


    //Engine stater
    //Future idea
}
