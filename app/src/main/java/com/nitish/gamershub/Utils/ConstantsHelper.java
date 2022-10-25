package com.nitish.gamershub.Utils;

import com.nitish.gamershub.Pojo.FireBase.AdViewedStats;

public class ConstantsHelper {
    public static  String gameDataObject = "gameDataObject";
    public  static boolean ShowAds = true;
    public static String UserMail ="UserMail";
    public static String GamersHub_ParentCollection ="Gamers Hub";

    public static String GamersHub_DATA ="Gamers Hub Data";

    public static  String DateTimeGlobal = "DateTimeGlobal";
    public static  String FavouriteList = "FavouriteList";
    public static  String MainGamesList = "MainGamesList";
    public static  String NewGamesList = "NewGamesList";
    public static  String PopularGamesList = "PopularGamesList";
    public static  String UserInfo = "UserInfo";
    public static  String GoogleSignInAccountUser = "GoogleSignInAccountUser";
    public static  String GoogleSignInUserProfile = "GoogleSignInUserProfile";
    // general reward coins to reward after a game
    public static  String GeneralRewardCoins = "GeneralRewardCoins";

    public static  String DailyBonus = "DailyBonus";
    public static  String WatchViewReward = "WatchViewReward";
    public static  String UserProfileGlobal = "userProfileGlobal";
    public static  String GamersHubDataGlobal = "GamersHubDataGlobal";
    public static  String AdViewedStatsGlobal = "AdViewedStatsGlobal";
    public static  String FirebaseFCMToken = "FirebaseFCMToken";
    public static  String From = "From";


    public static  int AccountActive =0;
    public static  int AccountSuspended =1;
    public static  int AccountBanned =2;

    public static String timerHourMinuteSecond ="00:00:00";
    public static enum ConnectionSignalStatus{
        NO_CONNECTIVITY,GOOD_STRENGTH,FAIR_STRENGTH,POOR_STRENGTH

    }
}
