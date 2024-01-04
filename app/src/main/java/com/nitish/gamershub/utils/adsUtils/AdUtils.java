package com.nitish.gamershub.utils.adsUtils;

import android.app.Activity;

import com.google.android.gms.ads.rewarded.RewardedAd;


public class AdUtils {

    Activity activity;
    RewardedAdAdmobUtilUtils rewardedAdAdmobUtilUtils;

    InterstitialUtilsAdmobAdUtil interstitialUtilsAdmobAdUtil;
    public AdUtils(Activity activity)
    {
        this.activity = activity;
    }


    public void loadRewardedAdmobAd()
    {
      rewardedAdAdmobUtilUtils = new RewardedAdAdmobUtilUtils(activity);

        rewardedAdAdmobUtilUtils.loadRewardedAdmobAd();
    }

    public void showRewardedAdmobAd(AdmobAdsListener.RewardedAdListener rewardedAdListener)
    {
        rewardedAdAdmobUtilUtils.showRewardedAd(rewardedAdListener);
    }


    public void loadAdMobInterstitialAd()
    {
        interstitialUtilsAdmobAdUtil = new InterstitialUtilsAdmobAdUtil(activity);

        interstitialUtilsAdmobAdUtil.loadInterstitialAd();
    }

    public void showAdMobInterstitialAd(AdmobInterstitialAdListener admobInterstitialAdListener)
    {
        interstitialUtilsAdmobAdUtil.showInterstitialAdNew(admobInterstitialAdListener);
    }

}
