package com.nitish.gamershub.view.base;

import android.app.Application;

public class AppClass extends Application {

    private AppClass appClass = null;

    @Override
    public void onCreate() {
        super.onCreate();

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
