package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.gameDataObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GamePlayActivity extends AppCompatActivity {

    LinearLayout.LayoutParams layoutParams;
    LottieAnimationView loading_lottieAnimationView, no_interent_lottie;
    WebView webView;
    AllGamesItems allGamesItems;

    StringBuilder blocklist;
    WebView view;
    String loddnormallist= "0"; //if you want to use a filterlist without "::::" at the beginning. please change to 1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play2);
        webView = findViewById(R.id.webView);



        load();
        allGamesItems = (AllGamesItems) getIntent().getSerializableExtra(gameDataObject);


        loading_lottieAnimationView = findViewById(R.id.frag_lottie_loading);
        no_interent_lottie = findViewById(R.id.frag_no_internet);


        layoutParams = new LinearLayout.LayoutParams(0, 0);
        loading_lottieAnimationView.setVisibility(View.VISIBLE);

        new AdBlockerWebView.init(this).initializeWebView(webView);
        webView.setWebViewClient(new Browser_home());




         webView.loadUrl(allGamesItems.getGameUrl());


       webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
     webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }



    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // webclient to block the ads
    public class Browser_home extends WebViewClient {

        Browser_home() {}

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {


            if(url.contains("securepubads")||url.contains("headerlift")||url.contains("criteo")){
                InputStream textStream = new ByteArrayInputStream("".getBytes());
                return getTextWebResource(textStream);
            }
            return super.shouldInterceptRequest(view, url);

        }



        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            ByteArrayInputStream EMPTY3 = new ByteArrayInputStream("".getBytes());
            String kk53 = String.valueOf(blocklist);//Load blocklist
            if (kk53.contains(":::::" + request.getUrl().getHost())) {// If blocklist equals url = Block
                return new WebResourceResponse("text/plain", "utf-8", EMPTY3);//Block
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (view.getUrl().equals(failingUrl)) {
                view.setLayoutParams(GamePlayActivity.this.layoutParams);
                loading_lottieAnimationView.setVisibility(View.INVISIBLE);
                no_interent_lottie.setVisibility(View.VISIBLE);
                Helper_class.show_toast(view.getContext(), "something went wrong ");
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
            loading_lottieAnimationView.setVisibility(View.GONE);

        }


    }
    private void load(){//Blocklist loading
        String strLine2="";
        blocklist = new StringBuilder();

        InputStream fis2 = this.getResources().openRawResource(R.raw.adblockserverlist);//Storage location
        BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
        if(fis2 != null) {
            try {
                while ((strLine2 = br2.readLine()) != null) {
                    if(loddnormallist.equals("0")){
                        blocklist.append(strLine2);//if ":::::" exists in blocklist | Line for Line
                        blocklist.append("\n");
                    }
                    if(loddnormallist.equals("1")){
                        blocklist.append(":::::"+strLine2);//if ":::::" not exists in blocklist | Line for Line
                        blocklist.append("\n");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private WebResourceResponse getTextWebResource(InputStream data) {
        return new WebResourceResponse("text/plain", "UTF-8", data);
    }
}