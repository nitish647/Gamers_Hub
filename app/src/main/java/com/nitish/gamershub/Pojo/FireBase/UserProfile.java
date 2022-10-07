package com.nitish.gamershub.Pojo.FireBase;

import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.Utils.AppHelper;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.DeviceHelper;

import io.paperdb.Paper;

public class UserProfile {


    public ProfileData profileData;
    public TimerStatus timerStatus;
    public UserTransactions userTransactions;
    public AdViewedStats adViewedStats;






    public UserProfile() {


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












    public static class ProfileData {

        public String name =AppHelper.getGoogleSignInAccountUser().getDisplayName();;
        public String email= AppHelper.getGoogleSignInAccountUser().getEmail();
        public int gameCoins = 0;

        public String lastLogin=DateTimeHelper.getDatePojo().getGetCurrentDateString();
        public String lastOpened=DateTimeHelper.getDatePojo().getGetCurrentDateString();
        public String deviceInfo= DeviceHelper.getDeviceNameAndVersion();
        public String createdAt= DateTimeHelper.getDatePojo().getGetCurrentDateString();

        public  ProfileData() {

        }

        public static ProfileData  getProfileData()
        {
            return new ProfileData();
        }


        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDeviceInfo() {
            return deviceInfo;
        }

        public void setDeviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getGameCoins() {
            return gameCoins;
        }

        public void setGameCoins(int gameCoins) {
            this.gameCoins = gameCoins;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getLastOpened() {
            return lastOpened;
        }

        public void setLastOpened(String lastOpened) {
            this.lastOpened = lastOpened;
        }
    }
}