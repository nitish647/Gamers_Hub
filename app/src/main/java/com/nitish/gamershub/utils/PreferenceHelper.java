package com.nitish.gamershub.utils;

import android.content.Context;
import android.content.SharedPreferences;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.UserProfile;

public class PreferenceHelper {


    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;


    public PreferenceHelper(Context context) {

        sharedPreferences = initializeSharePreferences(context);

    }

    private SharedPreferences initializeSharePreferences(Context context) {


        synchronized (this) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(AppConstants.PrefGamersHubMain, Context.MODE_PRIVATE);

            }
            editor = sharedPreferences.edit();
        }


        return sharedPreferences;
    }

    public void saveString(String name, String value) {

        editor.putString(name, value);

    }

    public String getString(String name, String defaultValue) {

        return sharedPreferences.getString(name, defaultValue);
    }

    public void saveBoolean(String name, Boolean value) {

        editor.putBoolean(name, value);

    }

    public Boolean getBoolean(String name, Boolean defaultValue) {

        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public void saveInt(String name, Integer value) {

        editor.putInt(name, value);

    }

    public Integer getInt(String name, Integer defaultValue) {

        return sharedPreferences.getInt(name, defaultValue);
    }

    public void saveFloat(String name, Float value) {

        editor.putFloat(name, value);

    }


    public Float getFloat(String name, Float defaultValue) {

        return sharedPreferences.getFloat(name, defaultValue);
    }


    public void saveUserProfile(@NonNull UserProfile userProfile) {
        String userProfileString = new Gson().toJson(userProfile);
        saveString(AppConstants.PrefUserProfile, userProfileString);
    }

    public UserProfile getUserProfile() {
        String userProfileString = getString(AppConstants.PrefUserProfile, "");

        UserProfile userProfile = new Gson().fromJson(userProfileString, UserProfile.class);
        return userProfile;

    }

    public void saveGamersHubData(@NonNull GamersHubData gamersHubData) {
        String gamersHubDataString = new Gson().toJson(gamersHubData);
        saveString(AppConstants.PrefGamersHubData, gamersHubDataString);
    }

    public GamersHubData getGamersHubData() {
        String gamersHubDataString = getString(AppConstants.PrefGamersHubData, "");

        GamersHubData gamersHubData = new Gson().fromJson(gamersHubDataString, GamersHubData.class);
        return gamersHubData;

    }

}
