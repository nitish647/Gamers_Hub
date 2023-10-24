package com.nitish.gamershub.view.base;

import android.app.Application;

import io.paperdb.Paper;

public class AppClass extends Application {

    private static AppClass instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        instance = this;
    }

    public static AppClass getInstance() {
        return instance;
    }
}