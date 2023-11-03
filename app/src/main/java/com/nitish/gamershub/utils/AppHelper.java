package com.nitish.gamershub.utils;

import static com.nitish.gamershub.utils.AppConstants.AdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppConstants.FirebaseFCMToken;
import static com.nitish.gamershub.utils.AppConstants.GamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppConstants.GoogleSignInAccountUser;
import static com.nitish.gamershub.utils.AppConstants.GoogleSignInUserProfile;
import static com.nitish.gamershub.utils.AppConstants.UserInfo;
import static com.nitish.gamershub.utils.AppConstants.UserProfileGlobal;
import static com.nitish.gamershub.utils.timeUtils.DateTimeHelper.TimeStampPattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.android.datatransport.runtime.BuildConfig;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.model.firebase.AdViewedStats;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.view.base.AppClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.paperdb.Paper;

public class AppHelper {


    //--------------------login page -----------------//
    public static void saveGoogleSignInAccountUser(GoogleSignInAccount googleSignInAccount) {
        Log.d("pResponse", "GoogleSignInUserProfile " + googleSignInAccount.getPhotoUrl());
        Paper.book().write(GoogleSignInUserProfile, googleSignInAccount.getPhotoUrl() + "");
        Paper.book().write(GoogleSignInAccountUser, googleSignInAccount);
    }

    public static GoogleSignInAccount getGoogleSignInAccountUser() {
        return (GoogleSignInAccount) Paper.book().read(GoogleSignInAccountUser);
    }

    public static String getGoogleSignInUserProfile() {
        return (String) Paper.book().read(GoogleSignInUserProfile);
    }


    public static void saveFireBaseFcmToken(String fcmToken) {
        Paper.book().write(FirebaseFCMToken, fcmToken);
    }

    public static String getFireBaseFcmToken() {

        if (Paper.book().read(FirebaseFCMToken) != null) {

            return Paper.book().read(FirebaseFCMToken);
        } else
            return "";
    }


    public static GamersHubData getGamersHubDataGlobal() {
        return (GamersHubData) Paper.book().read(GamersHubDataGlobal);
    }


    public static void saveAdViewedStatsGlobal(AdViewedStats adViewedStats) {
        Paper.book().write(AdViewedStatsGlobal, adViewedStats);

    }

    public static AdViewedStats getAdViewedStatsGlobal() {
        return (AdViewedStats) Paper.book().read(AdViewedStatsGlobal);

    }

    public static void saveUserProfileGlobal(UserProfile userProfile) {
        Paper.book().write(UserInfo, userProfile);


    }

    public static UserProfile getUserProfileGlobalData() {

        Paper.init(AppClass.getInstance());

        return (UserProfile) Paper.book().read(UserInfo);
    }

    public static void destroyAllPaperData() {
        Paper.book().destroy();
    }

    public static boolean isAppUpdated() {
        try {
            float currentVersionName = Float.parseFloat(getUserProfileGlobalData().getProfileData().getVersionName());
            float latestVersionName = Float.parseFloat(getGamersHubDataGlobal().getGamesData().getLatestVersionName());
            return currentVersionName >= latestVersionName;
        } catch (Exception e) {
            return false;
        }


    }


    ////-----------------Dialog Helper --------------------//
    public static void setDialogBackgroundTransparent(Dialog dialog) {
        if (dialog != null || dialog.getWindow() != null) {

            dialog.getWindow().getDecorView().setBackgroundColor(dialog.getContext().getResources().getColor(android.R.color.transparent));

        }
    }

    ////------------------------time helper-----------------------///

    public static void saveCalenderData() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);

        Log.d("savedTime", DateTimeHelper.convertDateToString(cal.getTime()));
        Paper.book().write("TimeHelperCalender", cal);


    }

    public static GradientDrawable setSingleColorRoundBackground(String color1, Float radius) {
        int[] colors = {Color.parseColor(color1)};
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, colors);

        gd.setCornerRadius(radius);
        gd.setColor(Color.parseColor(color1));
        return gd;
    }

    public static String getAppVersionName(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return BuildConfig.VERSION_NAME;
        }


    }

    public void getDominantGradient(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


        Palette.from(bitmap).generate(palette -> {
            assert palette != null;
            int vibrant = palette.getVibrantColor(0x003001); // <=== color you want
            int vibrantLight = palette.getLightVibrantColor(0x00000);
            int vibrantDark = palette.getDarkVibrantColor(0x000000);
            int muted = palette.getMutedColor(0x000000);
            int mutedLight = palette.getLightMutedColor(0x000000);
            int mutedDark = palette.getDarkMutedColor(0x000000);
        });
    }

    public static void readCalenderData() {
        Date date = new Date();
        Calendar cal = (Calendar) Paper.book().read("TimeHelperCalender");

        // TestCode: Anuraag
//        cal = null;
//        Log.d("savedTimeReturn",DateTimeHelper.convertDateToString(cal.getTime()));


    }

    /////---------------- Messages string --------------------////


    public static Uri getMailMessageUri(Context context, String subject, String body) {


        if (body.equals(""))
            body = "Hi I am  " + AppHelper.getUserProfileGlobalData().getProfileData().getName() + ", my user name is " + AppHelper.getUserProfileGlobalData().getProfileData().getEmail() + " \n I have a doubt regarding ...";


        String mailTo = "mailto:" + context.getString(R.string.contact_mail) +
                "?&subject=" + subject +
                "&body=" + Uri.encode(body);


        return Uri.parse(mailTo);
    }

    //-----------------------------JSON Helper----------//


    public static String generateRandomNumber() {
        String randomString = "";

        final int min = 1000;
        final int max = 9999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String timestamp;

        timestamp = new SimpleDateFormat(TimeStampPattern).format(DateTimeHelper.getDatePojo().getGetCurrentDate());

        randomString = timestamp + "_" + random;

        return randomString;

    }
    ///----------------clipboard helper-------------///

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
    }

    ////------------------ main class -------------////////////////
    public static void main(String[] args) {


    }


    public static void setStatusBarColor(Activity activityRef, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            activityRef.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activityRef.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activityRef.getWindow().setStatusBarColor(ContextCompat.getColor(activityRef, color));
        }
    }


}
