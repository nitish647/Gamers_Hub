package com.nitish.gamershub.model.firebase.timerStatus;

import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;

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
