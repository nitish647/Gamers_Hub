package com.nitish.gamershub.utils;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AppConstants {
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
    public static  String IntentData = "IntentData";


    public static String WithdrawalDone ="WithdrawalSuccess";

    public static  int AccountActive =0;
    public static  int AccountSuspended =1;
    public static  int AccountBanned =2;

    // Category Names
    public static List<String> CategoryList = new ArrayList<>();
    public static final String CategoryFavourites = "Favourites";
    public static final String CategoryNew = "New";
    public static final String CategoryBest = "Best";
    public static final String CategoryAction = "Action";
    public static final String CategoryArcade = "Arcade";
    public static final String CategoryShooting = "Shooting";
    public static final String CategoryPuzzle = "Puzzle";
    public static final String CategoryBoard = "Board";
    public static final String CategoryRacing = "Racing";
    public static final String CategoryStrategy = "Strategy";

    public static final String TransactionStatusPending = "PENDING";
    public static final String TransactionStatusFailed = "FAILED";
    public static final String TransactionStatusCompleted = "COMPLETED";
    public static final String TransactionMessage = "Unfortunately, the transaction could not be completed";


    public static String timerHourMinuteSecond ="00:00:00";
    public static enum ConnectionSignalStatus{
        NO_CONNECTIVITY,GOOD_STRENGTH,FAIR_STRENGTH,POOR_STRENGTH

    }


    public static String PrefGamersHubMain="PrefGamersHubMain";
    public static String PrefUserProfile="PrefUserProfile";



    public static void populateCategoryList() {
        CategoryList.add(CategoryFavourites);
        CategoryList.add(CategoryNew);
        CategoryList.add(CategoryBest);
        CategoryList.add(CategoryAction);
        CategoryList.add(CategoryArcade);
        CategoryList.add(CategoryShooting);
        CategoryList.add(CategoryPuzzle);
        CategoryList.add(CategoryBoard);
        CategoryList.add(CategoryRacing);
        CategoryList.add(CategoryStrategy);
    }

}
