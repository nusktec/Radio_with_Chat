package org.druplay.radioapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    NSCLib nsc;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private BottomNavigationView bottomNavigationView;
    private TextView title, sub_title;
    private CircleImageView profileImg;
    PopupMenu dropDown;
    Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                //.setDefaultFontPath("fonts/ubuntu.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_main);
        nsc = new NSCLib(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.main_action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMix1)));
        View view = actionBar.getCustomView();
        this.title = view.findViewById(R.id.main_bar_title);
        this.sub_title = view.findViewById(R.id.main_bar_subtitle);
        this.profileImg = view.findViewById(R.id.main_bar_dp);

        context = this;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOffscreenPageLimit(4);

        bottomNavigationView = findViewById(R.id.btnMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_onair:
                        mViewPager.setCurrentItem(0);
                        title.setText(String.valueOf("On Air"));
                        break;
                    case R.id.action_connect:
                        title.setText(String.valueOf("Connect"));
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.action_account:
                        mViewPager.setCurrentItem(2);
                        title.setText(String.valueOf("Adverts"));
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.action_onair);
                        //bottomNavigationView.performClick();
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.action_connect);
                        //bottomNavigationView.performClick();
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.action_account);
                        //bottomNavigationView.performClick();
                        break;
                    default:

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Profile pix on click
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropDown != null)
                    dropDown.show();
            }
        });

        //setup popup menu
        dropDown = new PopupMenu(this, profileImg);
        dropDown.getMenuInflater().inflate(R.menu.menu_main, dropDown.getMenu());
        dropDown.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                //noinspection SimplifiableIfStatement
                if (id == R.id.action_out) {
                    nsc.NSC7TempStorage().edit().remove(nsc.ACCOUNT).apply();
                    startActivity(new Intent(MainActivity.this, ActivitySplash.class));
                    finish();
                    return true;
                }
                if (id == R.id.action_exit) {
                    finish();
                    return true;
                }
                if (id == R.id.action_report) {
                    startActivity(new Intent(MainActivity.this, WriteReport.class));
                    return true;
                }
                if(id==R.id.action_about){
                    startActivity(new Intent(MainActivity.this, ActivityAbout.class));
                    return true;
                }
                return true;
            }
        });

        //load personal info
        loadPersonalData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    PageHome pageHome = new PageHome();
                    Bundle bundle = new Bundle();
                    pageHome.setArguments(bundle);
                    return pageHome;
                case 1:
                    return new PageConnect();
                case 2:
                    return new PageAdvert();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    //Personal data loader
    boolean loaded_ = false;

    void loadPersonalData() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("act", "readuser");
        requestParams.put("phone", nsc.NSC7TempStorage().getString(nsc.ACCOUNT, "No User"));
        asyncHttpClient.addHeader("nsckey", nsc.APP_API_HEADER_KEY);

        asyncHttpClient.post(nsc.API_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                sub_title.setText(String.valueOf("Info not available"));
            }

            @Override
            public void onStart() {
                sub_title.setText(String.valueOf("Loading info..."));
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        sub_title.setText(String.valueOf(response.getJSONObject("meta-data").getString("name")));
                        Glide.with(context)
                                .load(nsc.API_IMG_URL + "/" + response.getJSONObject("meta-data").getString("phone"))
                                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(profileImg);
                        loaded_ = true;
                        //Update user
                        nsc.updateUser("lastseen", "", false);
                    } else {
                        nsc.NSCtoasy(response.getString("message"));
                        sub_title.setText(String.valueOf("Info not available"));
                    }
                } catch (JSONException ex) {
                    Log.e("ReadUser Error", ex.getMessage());
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    //Onback pressed


    @Override
    public void onBackPressed() {
        //die in silence
        Toasty.error(this, "Tab on the profile picture to exit...", Toast.LENGTH_LONG).show();
        return;
    }
}


