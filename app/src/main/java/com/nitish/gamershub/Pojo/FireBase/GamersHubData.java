package com.nitish.gamershub.Pojo.FireBase;

import com.nitish.gamershub.Pojo.FaqPojo;

import java.util.ArrayList;

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
        boolean forceUpdate = false;
        String latestVersionName;


        public GamesData() {
        }

        public int getDailyCheckReward() {
            return dailyCheckReward;
        }


        public String getLatestVersionName() {
            return latestVersionName;
        }

        public int getGamePlaySecs() {
            return gamePlaySecs;
        }


        public int getGamePlayReward() {
            return gamePlayReward;
        }

        public int getWatchVideoReward() {
            return watchVideoReward;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }
    }

}

