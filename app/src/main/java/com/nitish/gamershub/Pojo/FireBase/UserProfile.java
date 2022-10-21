package com.nitish.gamershub.Pojo.FireBase;

import com.nitish.gamershub.BuildConfig;
import com.nitish.gamershub.Utils.AppHelper;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.DeviceHelper;

public class UserProfile {


    public static ProfileData profileData;
    public static TimerStatus timerStatus;
    public static UserTransactions userTransactions;
    public static AdViewedStats adViewedStats;
    public static UserAccountStatus userAccountStatus;





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












    public static class ProfileData {

        public String name =AppHelper.getGoogleSignInAccountUser().getDisplayName();;
        public String email= AppHelper.getGoogleSignInAccountUser().getEmail();
        public String versionName = BuildConfig.VERSION_NAME+"";
        public int gameCoins = 0;

        public String firebaseFcmToken;
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

        public String getFirebaseFcmToken() {
            return firebaseFcmToken;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public void setFirebaseFcmToken(String firebaseFcmToken) {
            this.firebaseFcmToken = firebaseFcmToken;
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
