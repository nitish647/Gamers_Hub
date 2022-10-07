package com.nitish.gamershub.Pojo.FireBase;

import com.nitish.gamershub.Utils.DateTimeHelper;

import java.util.Timer;

public class WatchViewReward {

    String claimedTime = DateTimeHelper.getDatePojo().getGetCurrentDateString();
    boolean claimed = false;


    public WatchViewReward() {
    }

    public String getClaimedTime() {
        return claimedTime;
    }

    public void setClaimedTime(String claimedTime) {
        this.claimedTime = claimedTime;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
