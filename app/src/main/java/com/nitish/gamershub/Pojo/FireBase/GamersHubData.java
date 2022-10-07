package com.nitish.gamershub.Pojo.FireBase;

public class GamersHubData {

    public GamesData gamesData;

    public GamersHubData() {
    }

    public GamesData getGamesData() {
        return gamesData;
    }

    public void setGamesData(GamesData gamesData) {
        this.gamesData = gamesData;
    }

    public static class GamesData {
        int dailyCheckReward=0;
        int gamePlaySecs =0;
        int gamePlayReward=0;
        int watchVideoReward =0;



        public GamesData() {
        }

        public int getDailyCheckReward() {
            return dailyCheckReward;
        }

        public void setDailyCheckReward(int dailyCheckReward) {
            this.dailyCheckReward = dailyCheckReward;
        }

        public int getGamePlaySecs() {
            return gamePlaySecs;
        }

        public void setGamePlaySecs(int gamePlaySecs) {
            this.gamePlaySecs = gamePlaySecs;
        }

        public int getGamePlayReward() {
            return gamePlayReward;
        }

        public void setGamePlayReward(int gamePlayReward) {
            this.gamePlayReward = gamePlayReward;
        }

        public int getWatchVideoReward() {
            return watchVideoReward;
        }

        public void setWatchVideoReward(int watchVideoReward) {
            this.watchVideoReward = watchVideoReward;
        }
    }
}

