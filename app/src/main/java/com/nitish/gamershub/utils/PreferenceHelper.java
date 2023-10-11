package com.nitish.gamershub.utils;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.prefs.Preferences;

public class PreferenceHelper {


    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;


    PreferenceHelper(Context context) {

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

    public void getString(String name, String defaultValue) {

        sharedPreferences.getString(name, defaultValue);
    }

    public void saveBoolean(String name, Boolean value) {

        editor.putBoolean(name, value);

    }

    public void getBoolean(String name, Boolean defaultValue) {

        sharedPreferences.getBoolean(name, defaultValue);
    }

    public void saveInt(String name, Integer value) {

        editor.putInt(name, value);

    }

    public void getInt(String name, Integer defaultValue) {

        sharedPreferences.getInt(name, defaultValue);
    }

    public void saveFloat(String name, Float value) {

        editor.putFloat(name, value);

    }


    public void getFloat(String name, Float defaultValue) {

        sharedPreferences.getFloat(name, defaultValue);
    }






}
