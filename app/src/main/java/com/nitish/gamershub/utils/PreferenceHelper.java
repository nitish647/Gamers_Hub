package com.nitish.gamershub.utils;

import static com.nitish.gamershub.utils.AppHelper.removeDuplicatesFromGamesList;

import android.content.Context;
import android.content.SharedPreferences;


import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

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

    public void destroyAllPreferences() {
        editor.clear().apply();
    }

    public void saveString(String name, String value) {

        editor.putString(name, value);
        editor.apply();

    }

    public String getString(String name, String defaultValue) {

        return sharedPreferences.getString(name, defaultValue);
    }

    public void saveBoolean(String name, Boolean value) {

        editor.putBoolean(name, value);
        editor.apply();

    }

    public Boolean getBoolean(String name, Boolean defaultValue) {

        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public void saveInt(String name, Integer value) {

        editor.putInt(name, value);
        editor.apply();

    }

    public Integer getInt(String name, Integer defaultValue) {

        return sharedPreferences.getInt(name, defaultValue);
    }

    public void saveFloat(String name, Float value) {

        editor.putFloat(name, value);
        editor.apply();


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

        return new Gson().fromJson(userProfileString, UserProfile.class);

    }

    public void saveUserMail(@NonNull String userMail) {

        saveString(AppConstants.PrefUserMail, userMail);
    }

    public String getUserMail() {
        return getString(AppConstants.PrefUserMail, null);
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

    public void saveAllGamesResponseMaterData(@NonNull AllGamesResponseItem allGamesResponseItem) {
        String allGamesResponseItemString = new Gson().toJson(allGamesResponseItem);
        saveString(AppConstants.PrefGamersHubMasterData, allGamesResponseItemString);
    }

    public AllGamesResponseItem getAllGamesResponseMaterData() {

        String allGamesResponseItemString = getString(AppConstants.PrefGamersHubMasterData, "");

        AllGamesResponseItem allGamesResponseItem = new Gson().fromJson(allGamesResponseItemString, AllGamesResponseItem.class);
        allGamesResponseItem.getMainGamesList().sort(new Comparator<GamesItems>() {
            @Override
            public int compare(GamesItems game1, GamesItems game2) {
                return game1.getName().compareTo(game2.getName());
            }
        });

        return allGamesResponseItem;

    }


    public void saveFavouriteItemInList(@NonNull GamesItems favouriteItem) {
        ArrayList<GamesItems> favouriteList = getSavedFavouriteList();
        favouriteList.add(favouriteItem);

        saveFavouriteList(favouriteList);

    }

    public void saveFavouriteList(@NonNull ArrayList<GamesItems> favouriteList) {

        String favouriteListString = new Gson().toJson(favouriteList);
        saveString(AppConstants.PrefFavList, favouriteListString);
    }

    public ArrayList<GamesItems> getSavedFavouriteList() {

        String favouriteListString = getString(AppConstants.PrefFavList, "");

        Type type = new TypeToken<ArrayList<GamesItems>>() {
        }.getType();
//        Type type = AppHelper.getTypedTokenOfList(new ArrayList<GamesItems>());

        ArrayList<GamesItems> favList = (ArrayList<GamesItems>) new Gson().fromJson(favouriteListString, type);

        return favList == null ? new ArrayList<>() : favList;

    }


    public void saveRecentlyPlayedItemInList(@NonNull GamesItems favouriteItem) {


        ArrayList<GamesItems> recentlyPlayedList = getRecentlyPlayedList();
        recentlyPlayedList.add(favouriteItem);

        saveRecentlyPlayedList(removeDuplicatesFromGamesList(recentlyPlayedList));

    }


    public void saveRecentlyPlayedList(@NonNull ArrayList<GamesItems> recentlyPlayedList) {

        String recentlyPlayedListString = new Gson().toJson(recentlyPlayedList);
        saveString(AppConstants.PrefRecentlyPlayedList, recentlyPlayedListString);
    }

    public ArrayList<GamesItems> getRecentlyPlayedList() {

        String recentlyPlayedListString = getString(AppConstants.PrefRecentlyPlayedList, "");
        Type type = new TypeToken<ArrayList<GamesItems>>() {
        }.getType();

//        Type type = AppHelper.getTypedTokenOfList(new ArrayList<GamesItems>());

        ArrayList<GamesItems> recentlyPlayedList = (ArrayList<GamesItems>) new Gson().fromJson(recentlyPlayedListString, type);

        return recentlyPlayedList == null ? new ArrayList<>() : recentlyPlayedList;

    }


    public void saveUserLoginStatus(String loginStatus) {

        saveString(AppConstants.PrefLoginStatus, loginStatus);
    }

    public String getUserLoginStatus() {
        return getString(AppConstants.PrefLoginStatus, AppConstants.PrefLoginStatus_LoggedOUT);
    }

    public boolean isUserLoggedIn() {
        return getUserLoginStatus().equals(AppConstants.PrefLoginStatus_LoggedIn);
    }

    public void saveUserLoggedIn() {
        saveUserLoginStatus(AppConstants.PrefLoginStatus_LoggedIn);
    }

    public void saveUserLoggedOut() {
        saveUserLoginStatus(AppConstants.PrefLoginStatus_LoggedOUT);
    }


    public void saveGoogleSignInAccountUser(GoogleSignInAccount googleSignInAccountUser) {

        String googleSignInAccountUserString = new Gson().toJson(googleSignInAccountUser, GoogleSignInAccount.class);
        saveString(AppConstants.PrefGoogleSignInAccount, googleSignInAccountUserString);
    }

    public GoogleSignInAccount getGoogleSignInAccountUser() {
        String googleSignInAccountUserString = getString(AppConstants.PrefGoogleSignInAccount, "");

        return new Gson().fromJson(googleSignInAccountUserString, GoogleSignInAccount.class);
    }


}
