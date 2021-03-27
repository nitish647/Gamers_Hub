package com.nitish.gamershub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaDescription;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.RewardData;
import com.facebook.ads.RewardedAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;


import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;

public class Game_play extends AppCompatActivity {
    Intent intent;
    AdblockWebView webView;
    File myfile;
    String ads = MainActivity.ads_state;
    AlertDialog.Builder builder;
    LinearLayout.LayoutParams layoutParams;
    LottieAnimationView lottieAnimationView, lottie_loading;
    private InterstitialAd fb_interstitialAd;
    private com.google.android.gms.ads.InterstitialAd admob_mInterstitialAd;

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        webView = findViewById(R.id.webview);
        lottie_loading = findViewById(R.id.lottie_loading);
        lottieAnimationView = findViewById(R.id.no_internet);

        layoutParams = new LinearLayout.LayoutParams(0, 0);
        lottie_loading.setVisibility(View.VISIBLE);
        intent = getIntent();
        String link = intent.getStringExtra("url");
        String orientation = intent.getStringExtra("orientation");
        if (orientation.toLowerCase().contains("hori")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //facebook ads
        fb_interstitialAd = new InterstitialAd(this, getResources().getString(R.string.fb_inter_nitish));
        setFb_interstitialAd_listner();


        //google ads

        admob_mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        admob_mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_inter));
        admob_mInterstitialAd.loadAd(new AdRequest.Builder().build());
        setAdmob_mInterstitialAd();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);

//        webView.getSettings().setAppCachePath( mypath.toString() );


        //    webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().getCacheMode();
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new ChromeClient());

        //

        //    webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (view.getUrl().equals(failingUrl)) {
                    webView.setLayoutParams(layoutParams);
                    lottie_loading.setVisibility(View.INVISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    show_toast("something went wrong ");
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(request.getUrl().toString());
                view.stopLoading();

                return false;
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                lottie_loading.setVisibility(View.GONE);

            }
        });

        webView.loadUrl(link);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
    public void show_toast(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[] { permission },
                    requestCode);

        } else {
            Toast.makeText(this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void ask_permission() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 2);
    }

    @Override
    public void onBackPressed() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Do you want to Exit the game");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                webView.destroy();
                if (ads.contains("true")) {
                    if (fb_interstitialAd.isAdLoaded() || !fb_interstitialAd.isAdInvalidated())
                        fb_interstitialAd.show();
                    else if (admob_mInterstitialAd.isLoaded())
                        admob_mInterstitialAd.show();
                    else {
                        finish();
                    }
                }//check ad serving condition
                else
                    finish();
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

    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        if (fb_interstitialAd != null) {
            fb_interstitialAd.destroy();
        }
        super.onDestroy();

    }

    public void setFb_interstitialAd_listner() {

        InterstitialAdListener fb_interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                finish();
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

                finish();
                super.onAdClosed();
            }
        });
    }

    public void set_lottie_loading() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getResources().openRawResource(R.raw.loading_3));
        arrayList.add(getResources().openRawResource(R.raw.loading_3));
    }

}