package com.nitish.gamershub.model.firebase;

public class TimerStatus {

   public DailyBonus dailyBonus;

   public WatchViewReward watchViewReward;


    public TimerStatus() {


    }

    public WatchViewReward getWatchViewReward() {
        return watchViewReward;
    }

    public void setWatchViewReward(WatchViewReward watchViewReward) {
        this.watchViewReward = watchViewReward;
    }

    public DailyBonus getDailyBonus() {
        return dailyBonus;
    }

    public void setDailyBonus(DailyBonus dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public DailyBonus dailyBonus() {
        return dailyBonus;
    }





    public static class DailyBonus {
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

}
