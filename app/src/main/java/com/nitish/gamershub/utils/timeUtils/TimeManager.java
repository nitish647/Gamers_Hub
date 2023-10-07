package com.nitish.gamershub.utils.timeUtils;

import android.os.SystemClock;

public class TimeManager {
    private static TimeManager instance;
    private long timeDifference;        //previous server time - previous elapsedRealtime
    private boolean isServerTime;       //is it a server time

    private TimeManager() {
    }
    public static TimeManager getInstance() {
        if (instance == null) {
            synchronized (TimeManager.class) {
                if (instance == null) {
                    instance = new TimeManager();
                }
            }
        }
        return instance;
    }

    /**
     * Get current server time
     *
     * @return the time
     */
    public synchronized long getServerTime() {
        if (!isServerTime) {
            //todo trigger to get server time
            return System.currentTimeMillis();
        }

        // Calculate the current server time
        return timeDifference + SystemClock.elapsedRealtime();
    }

    /**
     * Time calibration
     *
     * @param lastServiceTime current server time
     * @return the long
     */
    public synchronized long initServerTime(long lastServiceTime) {
        // Record the time difference
        timeDifference = lastServiceTime - SystemClock.elapsedRealtime();
        isServerTime = true;
        return lastServiceTime;
    }
}