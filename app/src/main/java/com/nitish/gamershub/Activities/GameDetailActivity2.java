package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.gameDataObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Fragments.GamePlayFragment;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;

import java.time.LocalTime;

public class GameDetailActivity2 extends FragmentActivity {




    InterstitialAd interstitialAd;
    Button playButton,checkVisibilityButton;
    static  String DetailFrag = "GameDetailFragment";
    Fragment gameDetailsFragment;

    GamePlayFragment gamePlayFragment;
    boolean gamePlayVisibility= false;
    AllGamesItems allGamesItems;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        playButton = findViewById(R.id.playButton);
        checkVisibilityButton =findViewById(R.id.checkVisibilityButton);
         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;
         fragmentManager = getSupportFragmentManager();
        gamePlayFragment = (GamePlayFragment)fragmentManager.findFragmentById(R.id.gamePlayFrag);
        gameDetailsFragment = fragmentManager.findFragmentById(R.id.gameDescFrag);


        loadInterstitialAd();


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction      fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.show(gamePlayFragment).hide(gameDetailsFragment).addToBackStack(null).commit();

                    if (allGamesItems.getOrientation().toLowerCase().contains("hori")) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    }

                    gamePlayVisibility = true;
                    gamePlayFragment.startTimer();
                    // game play fragment is visible


            }
        });



    }
    public void exitGameDialog()
    {
        LayoutInflater factory = LayoutInflater.from(GameDetailActivity2.this);

        final View exitGameLayout = factory.inflate(R.layout.exit_game_dialog, null);

        final AlertDialog exitGameDialog = new AlertDialog.Builder(GameDetailActivity2.this).create();



        exitGameDialog.setView(exitGameLayout);

        exitGameDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        exitGameDialog.show();
        exitGameDialog.setCancelable(false);

        Button yesButton = exitGameLayout.findViewById(R.id.yesButton);
        Button noButton = exitGameLayout.findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGameDialog.dismiss();
                if (interstitialAd != null && ConstantsHelper.ShowAds) {
                      showInterstitial();
                } else {


                    showGameDetailsFrag();
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGameDialog.dismiss();
            }
        });

    }
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.

        if (interstitialAd != null && ConstantsHelper.ShowAds) {
            interstitialAd.show(this);
        }
    }
    @Override
    public void onBackPressed() {
        if (gamePlayVisibility) {

            exitGameDialog();
        }

        else
            super.onBackPressed();


    }
    public void showGameDetailsFrag()
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    show_ads_admob_first();
//                    adblockWebView.onPause();
//                    wv_paused = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragmentTransaction.show(gameDetailsFragment).hide(gamePlayFragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
        // game play fragment is not visible
        gamePlayFragment.resetTimer();// reset the timer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(LocalTime.parse( gamePlayFragment.timerMinuteSecond).compareTo(LocalTime.parse("00:30")) > 0 )
                Toast.makeText(this, "you are rewarded", Toast.LENGTH_SHORT).show();
        }
        gamePlayVisibility = false;
    }
    public void oldExitDialog()
    {
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
                FragmentTransaction      fragmentTransaction = fragmentManager.beginTransaction();

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    show_ads_admob_first();
//                    adblockWebView.onPause();
//                    wv_paused = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                fragmentTransaction.show(gameDetailsFragment).hide(gamePlayFragment);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.commit();
                // game play fragment is not visible
                gamePlayVisibility = false;



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
            positive.setBackground(Helper_class.setBackgroundWithGradient("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
            positive.setTextColor(Color.WHITE);
            positive.setGravity(Gravity.CENTER);
            positive.setLayoutParams(params);

        }
        if (negative != null) {
            negative.setTextColor(Color.WHITE);
            negative.setGravity(Gravity.CENTER);
            negative.setLayoutParams(params);
            negative.setBackground(Helper_class.setBackgroundWithGradient("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
        }

    }
    public void loadInterstitialAd() {


        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getString(R.string.admob_inter),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        GameDetailActivity2.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        GameDetailActivity2.this.interstitialAd = null;
                                        Log.d("gInterstitialAd", "The ad was dismissed.");

                                        loadInterstitialAd();

                                        showGameDetailsFrag();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        GameDetailActivity2.this.interstitialAd = null;
                                        Log.d("gInterstitialAd", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("gInterstitialAd", "The ad was shown.");
                                    }
                                });
                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("gInterstitialAd", "ad loading failed : "+loadAdError.getMessage());
                        interstitialAd = null;

                        String error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.d("gInterstitialAd","Ad loading failed : "+error);
                    }
                });
    }
    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

        // load the ad again
        if(interstitialAd==null)
            loadInterstitialAd();
    }
    public void updateUserWallet()
    {

    }
    public void showRewardDialog()
    {
        LayoutInflater factory = LayoutInflater.from(GameDetailActivity2.this);

        final View addBankAccLayout = factory.inflate(R.layout.activity_category, null);

        final AlertDialog addAccountDialog = new AlertDialog.Builder(GameDetailActivity2.this).create();


        addAccountDialog.setView(addBankAccLayout);

        addAccountDialog.show();

        EditText accNumberEditText = addBankAccLayout.findViewById(R.id.searchView);
        Spinner accountTypeSpinner = addBankAccLayout.findViewById(R.id.webView);

        TextView dismissDialogButton = addBankAccLayout.findViewById(R.id.playButton);


    }
}