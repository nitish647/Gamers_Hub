package com.nitish.gamershub.model.firebase.timerStatus;

public class TimerStatus {

   private DailyBonus dailyBonus;

   private WatchViewReward watchViewReward;


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







}
