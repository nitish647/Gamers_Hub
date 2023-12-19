package com.nitish.gamershub.utils.adsUtils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nitish.gamershub.R;
import com.nitish.gamershub.view.base.BaseActivity;

public class InterstitialUtilsAdmobAdUtil extends AdmobAdsUtilsBase {

    InterstitialAd interstitialAd;
    Activity activity;
    public void showInterstitialAdNew( AdmobInterstitialAdListener interstitialAdListener) {


        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.

                           interstitialAd = null;
                            interstitialAdListener.onAdDismissed();


                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
//                            BaseActivity.this.interstitialAd = null;

                            interstitialAdListener.onAdFailed();
                            Log.d("gInterstitialAd", "The ad failed to show.");

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.

                            Log.d("gInterstitialAd", "The ad was shown.");
                            ((BaseActivity) activity).incrementRewardAdCount();

                            interstitialAdListener.onAdShown();
                        }
                    });

            interstitialAd.show(activity);

        } else {
            interstitialAdListener.onAdLoading();
        }
    }

    public InterstitialUtilsAdmobAdUtil(Activity activity, InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
        this.activity = activity;

    }

    public InterstitialAd loadInterstitialAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                activity,
                activity.getString(R.string.admob_inter),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
//                        BaseActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");

                         InterstitialUtilsAdmobAdUtil.this.interstitialAd = interstitialAd;


                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("gInterstitialAd", "ad loading failed : " + loadAdError.getMessage());

                        interstitialAd = null;

                        String error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.d("gInterstitialAd", "Ad loading failed : " + error);
                    }
                });

        return interstitialAd;

    }
}
