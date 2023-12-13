package com.nitish.gamershub.model.firebase.adviewStatus;

import com.nitish.gamershub.model.firebase.userProfile.UserProfile;

public class AdViewStatusHelper {

    public static void checkAndUpdateAdViewStatus(UserProfile userProfile){

        if (userProfile.getAdViewedStats() == null) {
            userProfile.setAdViewedStats(new AdViewedStats());
        }
    }
}
