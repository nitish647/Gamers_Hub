package com.nitish.gamershub.utils.adsUtils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.view.base.BaseActivity;

public class RewardedAdAdmobUtilUtils extends AdmobAdsUtilsBase {

    private RewardedAd rewardedAd;
   private Activity activity;

    public RewardedAdAdmobUtilUtils(RewardedAd rewardedAd, Activity activity) {
        this.rewardedAd = rewardedAd;
        this.activity = activity;
    }

    public RewardedAd loadRewardedAdmobAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();


        RewardedAd.load(
                activity,
                activity.getString(R.string.admob_reward),
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("rewardedAd", "onAdFailedToLoad " + loadAdError.getMessage());
                        rewardedAd = null;

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                        RewardedAdAdmobUtilUtils.this.rewardedAd = rewardedAd;

                        Log.d("rewardedAd", "onAdLoaded");

                    }
                });

        return rewardedAd;

    }

    public void showRewardedAd(AdmobAdsListener.RewardedAdListener rewardedAdListener)
    {
        if (rewardedAd == null) {
            Toast.makeText(activity, "The ad is not loaded yet , try again", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.

                        Log.d("rewardedAd", "onAdShowedFullScreenContent");


                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("rewardedAd", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Toast.makeText(activity, "Ad loading failed please try again", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAdListener.onDismissListener();
                        rewardedAd = null;


                    }
                });



        rewardedAd.show(
                activity,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        ((BaseActivity) activity).incrementRewardAdCount();

                        rewardedAdListener.onRewardGrantedListener();


                        AppConstants.ShowAds = false;
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }

}
