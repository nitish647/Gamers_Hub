package com.nitish.gamershub.model.firebase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.BuildConfig;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.DeviceHelper;

public class UserProfile {


    private   ProfileData profileData;
    private   TimerStatus timerStatus;
    private   UserTransactions userTransactions;
    private   AdViewedStats adViewedStats;
    private   UserAccountStatus userAccountStatus;
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

    public static class ProfileData {


        GoogleSignInAccount googleSignInAccount = AppHelper.getPreferenceHelperInstance().getGoogleSignInAccountUser();

        private String name =googleSignInAccount!=null? googleSignInAccount.getDisplayName():"";
        private String email=googleSignInAccount!=null? googleSignInAccount.getEmail():"";
        private String versionName = BuildConfig.VERSION_NAME+"";
        private int gameCoins = 0;

        private String firebaseFcmToken;
        private String lastLogin=DateTimeHelper.getDatePojo().getGetCurrentDateString();
        private String lastOpened=DateTimeHelper.getDatePojo().getGetCurrentDateString();
        private String deviceInfo= DeviceHelper.getDeviceNameAndVersion();
        private String createdAt= DateTimeHelper.getDatePojo().getGetCurrentDateString();

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
