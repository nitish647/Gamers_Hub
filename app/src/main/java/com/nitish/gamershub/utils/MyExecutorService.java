package com.nitish.gamershub.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyExecutorService {

    private ExecutorService executor;
    private Handler handler;
    private ExecutorServiceListener executorServiceListener;
    private Activity activity;

    public MyExecutorService(Activity activity) {
        this.activity = activity;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public MyExecutorService() {

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void runExecutorService(ExecutorServiceListener executorServiceListener) {
//        executorServiceListener.backgroundTask();
//        executorServiceListener.uiThreadTask();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                executorServiceListener.backgroundTask();
                //Background work here

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    executorServiceListener.uiThreadTask();
                                }
                            });
                        }
                       

                        //UI Thread work here
                    }
                });
            }
        });
    }

    public interface ExecutorServiceListener {
        void backgroundTask();

        void uiThreadTask();
    }
}
