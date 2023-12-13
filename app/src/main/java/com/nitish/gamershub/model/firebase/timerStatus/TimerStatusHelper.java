package com.nitish.gamershub.model.firebase.timerStatus;

import android.util.Log;

import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;

import java.util.Date;

public class TimerStatusHelper {


    public static void checkAndUpdateTimerStatus(UserProfile userProfile)
    {
        if (userProfile.getTimerStatus() != null) {

            setTimerStatus(userProfile);

        } else {
            userProfile.setTimerStatus(TimerStatusHelper.createTimerStatus());
        }


    }
    public static void setTimerStatus(UserProfile userProfile) {

        DailyBonus dailyBonus = userProfile.getTimerStatus().getDailyBonus();

        if (dailyBonus != null) {

            // when the daily bonus is claimed then check for the time
            if (dailyBonus.getClaimed()) {

                checkDailyBonus(userProfile.getTimerStatus().getDailyBonus(), userProfile);

            }


        } else {
            dailyBonus = new DailyBonus();
            userProfile.getTimerStatus().setDailyBonus(dailyBonus);
        }
        // initialize video reward
        if (userProfile.getTimerStatus().getWatchViewReward() == null) {
            WatchViewReward watchViewReward = new WatchViewReward();
            watchViewReward.setClaimed(false);
            watchViewReward.setClaimedTime(DateTimeHelper.getDatePojo().getGetCurrentDateString());

            userProfile.getTimerStatus().setWatchViewReward(watchViewReward);
        } else {

            WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();

            if (!watchViewReward.isClaimed()) {


            }

        }
    }

    public static void checkDailyBonus(DailyBonus dailyBonus,UserProfile userProfile ) {


        if (checkToUpdateDailyBonus(dailyBonus)) {

             updateTimerStatusDailyBonus(userProfile, dailyBonus);

//            callUpdateUser(userProfile, USAGE_UPDATE_TIMER_STATUS);


        }


    }

    public static TimerStatus createTimerStatus() {
        TimerStatus timerStatus = new TimerStatus();


        // DAILY BONUS
        DailyBonus dailyBonus = new DailyBonus();

        dailyBonus.setClaimed(false);


        String currentDateTime = DateTimeHelper.getDatePojo().getGetCurrentDateString();

        dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDateTime, DateTimeHelper.time_7_am));
        dailyBonus.setClaimedDate(currentDateTime);

        timerStatus.setDailyBonus(dailyBonus);

        // watchViewReward
        WatchViewReward watchViewReward = new WatchViewReward();
        watchViewReward.setClaimed(false);
        watchViewReward.setClaimedTime(currentDateTime);
        timerStatus.setWatchViewReward(watchViewReward);

        return timerStatus;
    }

    public static void updateTimerStatusDailyBonus(UserProfile userProfile,DailyBonus updatedDailyBonus)
    {
        TimerStatus timerStatus = userProfile.getTimerStatus();
        userProfile.getTimerStatus().setDailyBonus(updatedDailyBonus);
        userProfile.setTimerStatus(timerStatus);

    }

    public static boolean checkToUpdateDailyBonus(DailyBonus dailyBonus) {

        boolean toUpdate = false;
        String lastModifiedTime = dailyBonus.getLastResetDateTime();

        Date date1CurrentDate1 = null;

//            date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
        date1CurrentDate1 = DateTimeHelper.getDatePojo().getGetCurrentDate();

        Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
        long mills = date1CurrentDate1.getTime() - date2lastModifiedTime.getTime();


        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;

        String diff = hours + ":" + mins; // u
        Log.d("pTimer", "time difference " + diff);


        // reset the time to 8 pm when the time difference is 24 hours
        // enable the daily bonus
        if (hours >= 24) {


            toUpdate = true;

//            flag_DailyBonusWatched = false;
            dailyBonus.setClaimed(false);
            dailyBonus.setClaimedDate(DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1) + "");
            dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(date1CurrentDate1, DateTimeHelper.time_7_am));


//            UserProfile userProfile = getPreferencesMain().getUserProfile();
//            TimerStatusHelper.updateTimerStatusDailyBonus(userProfile,dailyBonus);
//
//            callUpdateUser(userProfile, USAGE_UPDATE_TIMER_STATUS);


        }
        return toUpdate;


    }

}
