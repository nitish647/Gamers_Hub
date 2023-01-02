package com.nitish.gamershub.Pojo.FireBase;

public class GamePlayedStatus {

    Integer gamePlayedToday=0;
    String lastGamePlayedDate="";
    Integer totalGamePlayed=0;

    public GamePlayedStatus() {
    }

    public Integer getGamePlayedToday() {
        return gamePlayedToday;
    }

    public void setGamePlayedToday(Integer gamePlayedToday) {
        this.gamePlayedToday = gamePlayedToday;
    }

    public Integer getTotalGamePlayed() {
        return totalGamePlayed;
    }

    public void setTotalGamePlayed(Integer totalGamePlayed) {
        this.totalGamePlayed = totalGamePlayed;
    }

    public String getLastGamePlayedDate() {
        return lastGamePlayedDate;
    }

    public void setLastGamePlayedDate(String lastGamePlayedDate) {
        this.lastGamePlayedDate = lastGamePlayedDate;
    }
}
