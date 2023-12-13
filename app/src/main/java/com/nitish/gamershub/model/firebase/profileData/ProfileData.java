package com.nitish.gamershub.model.firebase.profileData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.BuildConfig;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.DeviceHelper;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;

public  class ProfileData {


    GoogleSignInAccount googleSignInAccount = AppHelper.getPreferenceHelperInstance().getGoogleSignInAccountUser();

    private String name =googleSignInAccount!=null? googleSignInAccount.getDisplayName():"";
    private String email=googleSignInAccount!=null? googleSignInAccount.getEmail():"";
    private String versionName = BuildConfig.VERSION_NAME+"";
    private int gameCoins = 0;

    private String firebaseFcmToken;
    private String lastLogin= DateTimeHelper.getDatePojo().getGetCurrentDateString();
    private String lastOpened=DateTimeHelper.getDatePojo().getGetCurrentDateString();
    private String deviceInfo= DeviceHelper.getDeviceNameAndVersion();
    private String createdAt= DateTimeHelper.getDatePojo().getGetCurrentDateString();

    public  ProfileData() {

    }
    public  ProfileData( GoogleSignInAccount googleSignInAccount) {
        this.name =googleSignInAccount.getDisplayName();
        this.email=googleSignInAccount.getEmail();

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