package com.nitish.gamershub.model.firebase;

public class GamersHubData {

    private GamesData gamesData;
    private Messages message;


    public GamersHubData() {
    }

    public Messages getMessage() {
        return message;
    }

    public void setMessage(Messages message) {
        this.message = message;
    }

    public GamesData getGamesData() {
        return gamesData;
    }

    public void setGamesData(GamesData gamesData) {
        this.gamesData = gamesData;
    }


    public static class GamesData {
        int dailyCheckReward = 0;
        int gamePlaySecs = 0;
        int gamePlayReward = 0;
        int watchVideoReward = 0;
        int dailyGamePlayLimit = 0;
        boolean forceUpdate = false;
        String latestVersionName;

        String firebaseFcmToken;

        public GamesData() {
        }

        public String getFirebaseFcmToken() {
            return firebaseFcmToken;
        }

        public void setFirebaseFcmToken(String firebaseFcmToken) {
            this.firebaseFcmToken = firebaseFcmToken;
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

        public int getDailyGamePlayLimit() {
            return dailyGamePlayLimit;
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

