package com.nitish.gamershub.Fragments;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.nitish.gamershub.Activities.GameDetailActivity2;
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.databinding.FragmentGamePlayBinding;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Locale;


public class GamePlayFragment extends Fragment {

    FragmentGamePlayBinding fragmentGamePlayBinding;

    LinearLayout.LayoutParams layoutParams;

    AllGamesItems allGamesItems;

    StringBuilder blocklist;

    String loddnormallist= "0"; //if you want to use a filterlist without "::::" at the beginning. please change to 1


    public int seconds = 0;

    // Is the stopwatch running?
    private boolean running;
public  String timerMinuteSecond="00:00";

    private boolean wasRunning;
    GameDetailActivity2 parentActivity;


    public static GamePlayFragment newInstance() {

        return new GamePlayFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGamePlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false);

        parentActivity = (GameDetailActivity2) fragmentGamePlayBinding.getRoot().getContext();
        loadGame();
        allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;



        layoutParams = new LinearLayout.LayoutParams(0, 0);

        fragmentGamePlayBinding.webView.setWebViewClient(new Browser_home());




        fragmentGamePlayBinding.webView.loadUrl(allGamesItems.getGameUrl());



        fragmentGamePlayBinding.webView.getSettings().setJavaScriptEnabled(true);

        fragmentGamePlayBinding.webView.getSettings().setAllowFileAccess(true);
        fragmentGamePlayBinding.webView.getSettings().setDomStorageEnabled(true);
        fragmentGamePlayBinding.webView.getSettings().setDatabaseEnabled(true);
        fragmentGamePlayBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        fragmentGamePlayBinding.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.onBackPressed();
            }
        });
        if (!isNetworkAvailable()) { // loading offline
            fragmentGamePlayBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        return fragmentGamePlayBinding.getRoot();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
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
                view.setLayoutParams(layoutParams);
                fragmentGamePlayBinding.fragNoInternet.setVisibility(View.VISIBLE);
                Helper_class.show_toast(view.getContext(), "something went wrong ");
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
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



        }


    }
    private void loadGame(){//Blocklist loading
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

    @Override
    public void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }



    }



    public void startTimer()
    {

        running = true;
        timer();
    }

    public String resetTimer()
    {
        running = false;
        seconds = 0;
        String stopTimer =timerMinuteSecond;
        timerMinuteSecond ="00:00";
        return stopTimer;
    }

    public void timer()
    {



        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                 timerMinuteSecond
                            = DateTimeHelper.formatTimeToMMSS(minutes, secs);
                    fragmentGamePlayBinding.timerTextview.setText(timerMinuteSecond);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1100);
            }
        });
    }


}

