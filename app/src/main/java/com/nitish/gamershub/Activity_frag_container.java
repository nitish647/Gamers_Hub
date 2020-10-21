package com.nitish.gamershub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;
import org.json.JSONException;
import org.json.JSONObject;

public class Activity_frag_container extends AppCompatActivity {
    static String json_string;
    TextView instruction;
    String url;
    Game_play_frag game_play_frag;
    String ads = MainActivity.ads_state;
    AdView google_banner_ad;
    FragmentManager fragmentManager = getSupportFragmentManager();
    AdblockWebView adblockWebView;
    boolean game_play_visibily, wv_paused;
    Button game_play_btn;
    Bundle bundle = new Bundle();
    String name, instrution, img_file, json_obj, orientation;
    String non_stat = "snmsdnfnbsdf";
    private InterstitialAd fb_interstitialAd;
    private com.google.android.gms.ads.InterstitialAd admob_mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_container);

        adblockWebView = findViewById(R.id.frag_webview);
        instruction = findViewById(R.id.frag_instr_textview);
        game_play_btn = findViewById(R.id.frag_desc_start_btn);
        instruction.setTextColor(Color.WHITE);
        game_play_frag = new Game_play_frag();
        String img_file = "", name = "", orientation = "";

        final Intent intent = getIntent();

        json_string = intent.getStringExtra("json");

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_string);
            url = jsonObject.getString("url");
            orientation = jsonObject.getString("orientation");
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            adblockWebView.loadUrl(url);
            Game_play_frag game_play_frag = new Game_play_frag();
            game_play_frag.setArguments(bundle);
// set Fragmentclass Arguments


        } catch (JSONException e) {
            e.printStackTrace();
            Helper_class.show_toast(getApplicationContext(), "error " + e.toString());
        }


        //facebook ads
        fb_interstitialAd = new InterstitialAd(this, getResources().getString(R.string.fb_inter));
        setFb_interstitialAd_listner();
        Game_play_frag game_play_frag = new Game_play_frag();


        //google ads

        admob_mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        admob_mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_inter));
        admob_mInterstitialAd.loadAd(new AdRequest.Builder().build());
        setAdmob_mInterstitialAd();


        final String finalOrientation = orientation;
        final String finalOrientation1 = orientation;
        game_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game_play_visibily = true;
                if (wv_paused)
                    adblockWebView.onResume();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show_ads_admob_first();
                    }
                }, 2500);
                if (finalOrientation1.toLowerCase().contains("hori")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentById(R.id.act_desc_frag)).show(fragmentManager.findFragmentById(R.id.act_game_play_frag)).addToBackStack(null).commit();

            }
        });


    }


    public void setFb_interstitialAd_listner() {

        InterstitialAdListener fb_interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {


            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        fb_interstitialAd.loadAd(fb_interstitialAd.buildLoadAdConfig().withAdListener(fb_interstitialAdListener).build());
    }

    public void setAdmob_mInterstitialAd() {
        admob_mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                admob_mInterstitialAd.loadAd(new AdRequest.Builder().build());

                // finish();
                super.onAdClosed();
            }
        });
    }

    public void show_ads_fb_first() {
        if (ads.contains("true")) {
            if (fb_interstitialAd.isAdLoaded() && !fb_interstitialAd.isAdInvalidated())
                fb_interstitialAd.show();
            else if (admob_mInterstitialAd.isLoaded())
                admob_mInterstitialAd.show();

        }
    }

    public void show_ads_admob_first() {
        if (ads.contains("true")) {

            if (admob_mInterstitialAd.isLoaded())
                admob_mInterstitialAd.show();
            else if (fb_interstitialAd.isAdLoaded() && !fb_interstitialAd.isAdInvalidated())
                fb_interstitialAd.show();

        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adblockWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adblockWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (game_play_visibily) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("Do you want to Exit the game");

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    show_ads_admob_first();
                    adblockWebView.onPause();
                    wv_paused = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    game_play_visibily = false;
                    onBackPressed();

                }
            });

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();


            Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginStart(10);
            if (positive != null) {
                positive.setBackground(Helper_class.set_Colors("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
                positive.setTextColor(Color.WHITE);
                positive.setGravity(Gravity.CENTER);
                positive.setLayoutParams(params);

            }
            if (negative != null) {
                negative.setTextColor(Color.WHITE);
                negative.setGravity(Gravity.CENTER);
                negative.setLayoutParams(params);
                negative.setBackground(Helper_class.set_Colors("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
            }

        } else
            super.onBackPressed();
//if(game_play_visibily)
//{show_ads_fb_first();
//    adblockWebView.destroy();
//}

    }

    @Override
    protected void onDestroy() {
        if (fb_interstitialAd != null) {
            fb_interstitialAd.destroy();
        }
        super.onDestroy();
    }
}