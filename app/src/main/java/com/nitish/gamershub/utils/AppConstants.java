package com.nitish.gamershub.utils;

import com.nitish.gamershub.R;
import com.nitish.gamershub.model.local.CategoryItem;

import java.util.ArrayList;

public class AppConstants {
    public static String gameDataObject = "gameDataObject";
    public static String UserMail = "UserMail";
    public static String GamersHub_ParentCollection = "Gamers Hub";

    public static String GamersHub_DATA = "Gamers Hub Data";

    public static String DateTimeGlobal = "DateTimeGlobal";
    public static String FavouriteList = "FavouriteList";
    public static String MainGamesList = "MainGamesList";
    public static String NewGamesList = "NewGamesList";
    public static String PopularGamesList = "PopularGamesList";
    public static String UserInfo = "UserInfo";
    public static String GoogleSignInAccountUser = "GoogleSignInAccountUser";
    public static String GoogleSignInUserProfile = "GoogleSignInUserProfile";
    // general reward coins to reward after a game
    public static String GeneralRewardCoins = "GeneralRewardCoins";

    public static String DailyBonus = "DailyBonus";
    public static String WatchViewReward = "WatchViewReward";
    public static String UserProfileGlobal = "userProfileGlobal";
    public static String GamersHubDataGlobal = "GamersHubDataGlobal";
    public static String AdViewedStatsGlobal = "AdViewedStatsGlobal";
    public static String FirebaseFCMToken = "FirebaseFCMToken";
    public static String From = "From";
    public static String IntentData = "IntentData";
    public static String BundleData = "BundleData";

    public static String WithdrawalDone = "WithdrawalSuccess";

    public static int AccountActive = 0;
    public static int AccountSuspended = 1;
    public static int AccountBanned = 2;

    // Category Names
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


    public static String timerHourMinuteSecond = "00:00:00";

    public static enum ConnectionSignalStatus {
        NO_CONNECTIVITY, GOOD_STRENGTH, FAIR_STRENGTH, POOR_STRENGTH

    }

    public static String ASSETS_URL_PRIVACY_POLICY = "file:///android_asset/policy.html";
    public static String ASSETS_URL_ABOUT_US = "file:///android_asset/about_us.html";


    public static String upiImageLink = "https://i.ibb.co/JB3b9fc/upi-icon-1.png";
    public static String paytmImageLink = "https://i.ibb.co/C9VzmMx/paytm-icom.png";



    public static String PrefGamersHubMasterData = "PrefGamersHubMasterData";

    public static enum StringEnum {

    }
    public static String PrefGamersHubMain = "PrefGamersHubMain";
    public static String PrefUserProfile = "PrefUserProfile";
    public static String PrefUserMail = "PrefUserMail";
    public static String PrefGamersHubData = "PrefGamersHubData";
    public static String PrefFavList = "PrefFavList";
    public static String PrefRecentlyPlayedList = "PrefRecentlyPlayedList";

    public static String PrefGoogleSignInAccount = "PrefGoogleSignInAccount";
    public static String Pref_FCM_TOKEN = "Pref_FCM_TOKEN";


    public static String PrefLoginStatus = "PrefLoginStatus";
    public static String PrefLoginStatus_LoggedIn = "PrefLoginStatus_LoggedIn";
    public static String PrefLoginStatus_LoggedOUT = "PrefLoginStatus_LoggedOUT";


    public static ArrayList<CategoryItem> getCategoryList() {


//        categoryList.add(new Pair<>(CategoryFavourites, R.drawable.fav));


        ArrayList<CategoryItem> categoryItemArrayList = new ArrayList<>();
        categoryItemArrayList.add(new CategoryItem(CategoryNew, R.drawable.category_new_games));
        categoryItemArrayList.add(new CategoryItem(CategoryBest, R.drawable.category_popular_games));
        categoryItemArrayList.add(new CategoryItem(CategoryAction, R.drawable.category_action_game));
        categoryItemArrayList.add(new CategoryItem(CategoryArcade, R.drawable.category_arcade_game));
        categoryItemArrayList.add(new CategoryItem(CategoryShooting, R.drawable.cateogry_shooting_game_gun_icon));
        categoryItemArrayList.add(new CategoryItem(CategoryPuzzle, R.drawable.category_puzzle_game));
        categoryItemArrayList.add(new CategoryItem(CategoryBoard, R.drawable.category_board_game));
        categoryItemArrayList.add(new CategoryItem(CategoryRacing, R.drawable.category_racing_game1));
        categoryItemArrayList.add(new CategoryItem(CategoryStrategy, R.drawable.category_strategy_game2));

        return categoryItemArrayList;
    }

}
