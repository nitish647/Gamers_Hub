package com.nitish.gamershub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import java.io.File;

public class Game_play extends AppCompatActivity {
Intent intent;
AdblockWebView webView;
File myfile;
    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        webView = (AdblockWebView) findViewById(R.id.webview);

        //ask permission
        ask_permission();
        intent = getIntent();

        myfile =  new File(String.valueOf(getExternalFilesDir("mygames")));

        String link = intent.getStringExtra("url");
webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled( true );
        File mypath = new File(myfile, "profile2.jpg");
        webView.getSettings().setAppCachePath( mypath.toString() );

        show_toast("saved " + mypath.toString());
    //    webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
      webView.getSettings().getCacheMode();
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setDatabaseEnabled(true);
       webView.setWebChromeClient(new ChromeClient());

       //

    //    webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (view.getUrl().equals(failingUrl)){
                  show_toast("error "+description +"  "+failingUrl);
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
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
                show_toast(error +" dtata is not available2");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
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

        }
        else {
            Toast.makeText(this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public void ask_permission()
    {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,2);
    }
}