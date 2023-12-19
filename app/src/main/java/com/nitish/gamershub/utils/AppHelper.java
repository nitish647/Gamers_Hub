package com.nitish.gamershub.utils;

import static com.nitish.gamershub.utils.AppConstants.AdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppConstants.FirebaseFCMToken;
import static com.nitish.gamershub.utils.timeUtils.DateTimeHelper.TimeStampPattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.android.datatransport.runtime.BuildConfig;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.nitish.gamershub.model.firebase.adviewStatus.AdViewedStats;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.view.base.AppClass;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import io.paperdb.Paper;

public class AppHelper {


    //--------------------login page -----------------//


    public static void saveFireBaseFcmToken(String fcmToken) {
        Paper.book().write(FirebaseFCMToken, fcmToken);
    }

    public static String getFireBaseFcmToken() {

        if (Paper.book().read(FirebaseFCMToken) != null) {

            return Paper.book().read(FirebaseFCMToken);
        } else
            return "";
    }

    public static PreferenceHelper getPreferenceHelperInstance() {
        return new PreferenceHelper(AppClass.getInstance().getApplicationContext());
    }


    public static void saveAdViewedStatsGlobal(AdViewedStats adViewedStats) {
        Paper.book().write(AdViewedStatsGlobal, adViewedStats);


    }


    public static AdViewedStats getAdViewedStatsGlobal() {

        return (AdViewedStats) Paper.book().read(AdViewedStatsGlobal);

    }


    public static <K> Type getTypedTokenOfList(ArrayList<K> list) {

        Type type = new TypeToken<ArrayList<K>>() {
        }.getType();
        return type;
    }


    public static void destroyAllPaperData() {
        Paper.book().destroy();
    }

    public static ArrayList<GamesItems> removeDuplicatesFromGamesList(ArrayList<GamesItems> gamesItemsArrayList) {
        Set<GamesItems> set = new LinkedHashSet<>();
        set.addAll(gamesItemsArrayList);


        ArrayList<GamesItems> nonDuplicateList = new ArrayList<>();
        nonDuplicateList.addAll(set);
        return nonDuplicateList;

    }

    public static boolean isNullOrEmpty(String string) {
        if (string == null || string.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean isAppUpdated(UserProfile userProfile) {
        try {
            float currentVersionName = Float.parseFloat(userProfile.getProfileData().getVersionName());
            float latestVersionName = Float.parseFloat(AppHelper.getPreferenceHelperInstance().getGamersHubData().getGamesData().getLatestVersionName());
            return currentVersionName >= latestVersionName;
        } catch (Exception e) {
            return false;
        }


    }
    public static Boolean checkIfHorizontal(GamesItems gamesItems)
    {
        return gamesItems.getOrientation().toLowerCase().contains("hori");

    }


    ////-----------------Dialog Helper --------------------//
    public static void setDialogBackgroundTransparent(Dialog dialog) {
        if (dialog != null || dialog.getWindow() != null) {

            dialog.getWindow().getDecorView().setBackgroundColor(dialog.getContext().getResources().getColor(android.R.color.transparent));

        }
    }

    ////------------------------time helper-----------------------///

    public static void checkBundleSize(Bundle bundle, String tag) {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(bundle);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        double bundleSizeInKb = ((double) bytes.length) / 1000;
        Log.d(tag, "bundleSizeInKb " + bundleSizeInKb + " kb");

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


    public static  void getDominantGradient(ImageView imageView, OnPalleteColorGet onPalleteColorGet) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


        Palette.from(bitmap).generate(palette -> {
            assert palette != null;
            int vibrant = palette.getVibrantColor(0x003001); // <=== color you want
            onPalleteColorGet.getVibrantColor(vibrant);

            int dominantColor = palette.getDominantColor(0x00000);
            onPalleteColorGet.getDominantColor(dominantColor);


            int vibrantDark = palette.getDarkVibrantColor(0x000000);
            onPalleteColorGet.getVibrantColorDark(vibrantDark);

            int muted = palette.getMutedColor(0x000000);
            onPalleteColorGet.getMuted(muted);

            int mutedLight = palette.getLightMutedColor(0x000000);
            onPalleteColorGet.getMutedLight(mutedLight);

            int mutedDark = palette.getDarkMutedColor(0x000000);
            onPalleteColorGet.getMutedDark(mutedDark);

        });



    }
    public static void openNotificationSettings(Context context) {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }

        context.startActivity(intent);
    }

    public static  interface OnPalleteColorGet
    {
         void getDominantColor(int color);

        default void getVibrantColor(int color){};
        default void getVibrantColorDark(int color){};
        default void getMutedDark(int color){};
        default void getMutedLight(int color){};
        default void getMuted(int color){};

    }

    public static void readCalenderData() {
        Date date = new Date();
        Calendar cal = (Calendar) Paper.book().read("TimeHelperCalender");

        // TestCode: Anuraag
//        cal = null;
//        Log.d("savedTimeReturn",DateTimeHelper.convertDateToString(cal.getTime()));


    }

    /////---------------- Messages string --------------------////


    public static Uri getMailMessageUri(Context context, UserProfile userProfile, String subject, String body) {


        if (body.equals(""))
            body = "Hi I am  " + userProfile.getProfileData().getName() + ", my user name is " + userProfile.getProfileData().getEmail() + " \n I have a doubt regarding ...";


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

        activityRef.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activityRef.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activityRef.getWindow().setStatusBarColor(ContextCompat.getColor(activityRef, color));

    }


}
