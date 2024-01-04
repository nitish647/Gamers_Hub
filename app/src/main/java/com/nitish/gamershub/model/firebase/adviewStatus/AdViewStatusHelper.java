package com.nitish.gamershub.model.firebase.adviewStatus;

import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.PreferenceHelper;

public class AdViewStatusHelper {

    public static void checkAndUpdateAdViewStatus(UserProfile userProfile){

        if (userProfile.getAdViewedStats() == null) {
            userProfile.setAdViewedStats(new AdViewedStats());
        }
    }



   static String Admob_InterstitialAd="Admob_InterstitialAd";
   static String Admob_RewardedAd="Admob_RewardedAd";


    private static void incrementAds(String adType)
    {
        AdViewedStats adViewedStats;

        PreferenceHelper getPreferencesMain = AppHelper.getPreferenceHelperInstance();
        UserProfile userProfile = getPreferencesMain.getUserProfile();
        adViewedStats = userProfile.getAdViewedStats();

        if(adType.equals(Admob_InterstitialAd))
        {
            int interstitialAdCount = adViewedStats.getAdMobInterstitialAdViewed();
            interstitialAdCount++;

            adViewedStats.setAdMobInterstitialAdViewed(interstitialAdCount);

        }
        if(adType.equals(Admob_RewardedAd))
        {
            int rewardedAdAdCount = adViewedStats.getAdMobRewardedAdViewed();
            rewardedAdAdCount++;

            adViewedStats.setAdMobRewardedAdViewed(rewardedAdAdCount);

        }
        userProfile.setAdViewedStats(adViewedStats);
        getPreferencesMain.saveUserProfile(userProfile);

    }

    public static void incrementInterstitialAdAdCount() {

       incrementAds(Admob_InterstitialAd);
    }


    public static void incrementRewardedAdAdCount() {

      incrementAds(Admob_RewardedAd);
    }


}
