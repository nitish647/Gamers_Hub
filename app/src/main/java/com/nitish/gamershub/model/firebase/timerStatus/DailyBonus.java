package com.nitish.gamershub.model.firebase.timerStatus;

public class DailyBonus {
    Boolean claimed = false;
    String claimedDate;
    String lastResetDateTime ;

    public DailyBonus() {
    }

    public Boolean getClaimed() {
        return claimed;
    }

    public void setClaimed(Boolean claimed) {
        this.claimed = claimed;
    }

    public String getClaimedDate() {
        return claimedDate;
    }

    public void setClaimedDate(String claimedDate) {
        this.claimedDate = claimedDate;
    }

    public String getLastResetDateTime() {
        return lastResetDateTime;
    }

    public void setLastResetDateTime(String lastResetDateTime) {
        this.lastResetDateTime = lastResetDateTime;
    }
}