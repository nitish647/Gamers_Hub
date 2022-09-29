package com.nitish.gamershub.Pojo.FireBase;

public class WatchViewReward {

    String claimedTime ="";
    boolean claimed = false;


    {
        claimed=false;
        claimedTime ="";
    }
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
