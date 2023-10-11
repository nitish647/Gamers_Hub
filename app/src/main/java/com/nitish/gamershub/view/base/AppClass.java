package com.nitish.gamershub.view.base;

import android.app.Application;

import io.paperdb.Paper;

public class AppClass extends Application {

    private AppClass appClass = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);
        getInstance();
    }


    public AppClass getInstance() {

        synchronized (this) {
            if (appClass == null) {
                appClass = this;
            }
            return appClass;
        }


    }
}
