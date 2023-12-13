package com.nitish.gamershub.model.firebase.gamePlayed;

import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;

public class GamePlayedHelper {

    public static void setGamePlayedStatus(UserProfile userProfile) {
        if (userProfile.getGamePlayedStatus() == null) {
            GamePlayedStatus gamePlayedStatus = new GamePlayedStatus();
            gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            userProfile.setGamePlayedStatus(gamePlayedStatus);
        } else {
            GamePlayedStatus gamePlayedStatus = userProfile.getGamePlayedStatus();
            if (gamePlayedStatus.getLastGamePlayedDate() == null
                    || gamePlayedStatus.getLastGamePlayedDate().trim().isEmpty()) {
                gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            }
            // reset the game play limit if current date is greater to the last played date
            else if (DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(), gamePlayedStatus.getLastGamePlayedDate()) > 0) {
                gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                gamePlayedStatus.setGamePlayedToday(0);

            }
            userProfile.setGamePlayedStatus(gamePlayedStatus);
        }
    }


}
