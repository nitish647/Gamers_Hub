package com.nitish.gamershub.model.firebase.userProfile;

import com.nitish.gamershub.model.firebase.adviewStatus.AdViewedStats;
import com.nitish.gamershub.model.firebase.gamePlayed.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.profileData.ProfileData;
import com.nitish.gamershub.model.firebase.timerStatus.TimerStatus;
import com.nitish.gamershub.model.firebase.userTransaction.UserTransactions;

public class UserProfile {


    private ProfileData profileData;
    private TimerStatus timerStatus;
    private UserTransactions userTransactions;
    private AdViewedStats adViewedStats;
    private UserAccountStatus userAccountStatus;
    private GamePlayedStatus gamePlayedStatus;





    public UserProfile() {


    }

    public UserAccountStatus getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setUserAccountStatus(UserAccountStatus userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    public AdViewedStats getAdViewedStats() {
        return adViewedStats;
    }

    public void setAdViewedStats(AdViewedStats adViewedStats) {
        this.adViewedStats = adViewedStats;
    }

    public UserTransactions getUserTransactions() {
        return userTransactions;
    }

    public void setUserTransactions(UserTransactions userTransactions) {
        this.userTransactions = userTransactions;
    }

    public UserProfile(ProfileData profileData) {
        this.profileData = profileData;
    }

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }


    public TimerStatus getTimerStatus() {
        return timerStatus;
    }

    public void setTimerStatus(TimerStatus timerStatus) {
        this.timerStatus = timerStatus;
    }

    public GamePlayedStatus getGamePlayedStatus() {
        return gamePlayedStatus;
    }

    public void setGamePlayedStatus(GamePlayedStatus gamePlayedStatus) {
        this.gamePlayedStatus = gamePlayedStatus;
    }




















}
