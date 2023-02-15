package com.nitish.gamershub.Utils;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.nitish.gamershub.Utils.ConstantsHelper.AdViewedStatsGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.FirebaseFCMToken;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHubDataGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInUserProfile;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserProfileGlobal;
import static com.nitish.gamershub.Utils.DateTimeHelper.TimeStampPattern;
import static com.nitish.gamershub.Utils.DateTimeHelper.simpleDateFormatPattern;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.datatransport.runtime.BuildConfig;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.Activities.LoginPage;
import com.nitish.gamershub.Activities.Splash_Screen;
import com.nitish.gamershub.Pojo.FireBase.AdViewedStats;
import com.nitish.gamershub.Pojo.FireBase.GamersHubData;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;

import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class AppHelper {




    //--------------------login page -----------------//
    public static void saveGoogleSignInAccountUser(GoogleSignInAccount googleSignInAccount)
    {
        Log.d("pResponse","GoogleSignInUserProfile "+googleSignInAccount.getPhotoUrl());
        Paper.book().write(GoogleSignInUserProfile,googleSignInAccount.getPhotoUrl()+"");
        Paper.book().write(GoogleSignInAccountUser,googleSignInAccount);
    }

    public static GoogleSignInAccount getGoogleSignInAccountUser()
    {
       return (GoogleSignInAccount) Paper.book().read(GoogleSignInAccountUser);
    }
    public static String getGoogleSignInUserProfile()
    {
        return (String) Paper.book().read(GoogleSignInUserProfile);
    }



    public static void saveFireBaseFcmToken(String fcmToken)
    {
        Paper.book().write(FirebaseFCMToken,fcmToken);
    }
    public static String getFireBaseFcmToken()
    {

        if(Paper.book().read(FirebaseFCMToken)!=null){

            return Paper.book().read(FirebaseFCMToken);
        }
        else
            return "";
    }


    public static GamersHubData getGamersHubDataGlobal()
    {
        return (GamersHubData) Paper.book().read(GamersHubDataGlobal);
    }


    public static void saveAdViewedStatsGlobal(AdViewedStats adViewedStats)
    {
        Paper.book().write(AdViewedStatsGlobal,adViewedStats);

    }
    public static AdViewedStats getAdViewedStatsGlobal()
    {
        return (AdViewedStats) Paper.book().read(AdViewedStatsGlobal);

    }

    public static void saveUserProfileGlobal(UserProfile userProfile)
    {
        Paper.book().write(UserProfileGlobal,userProfile);


    }
    public static UserProfile getUserProfileGlobalData()
    {

        return (UserProfile)Paper.book().read(UserProfileGlobal);
    }
    public static void destroyAllPaperData()
    {
        Paper.book().destroy();
    }

    public static  boolean isAppUpdated()
    {
        try {
            float currentVersionName = Float.parseFloat(getUserProfileGlobalData().getProfileData().getVersionName());
            float latestVersionName = Float.parseFloat(getGamersHubDataGlobal().getGamesData().getLatestVersionName());
            return currentVersionName>=latestVersionName ;
        } catch (Exception e)
        {
            return false;
        }




    }



    ////------------------------time helper-----------------------///

    public static void saveCalenderData()
    {
        Date date= new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);

        Log.d("savedTime",DateTimeHelper.convertDateToString(cal.getTime()));
        Paper.book().write("TimeHelperCalender",cal);


    }

    public static String getAppVersionName(Context context)
    {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return   BuildConfig.VERSION_NAME;
        }


    }
    public static void readCalenderData()
    {
        Date date= new Date();
        Calendar cal =      (Calendar) Paper.book().read("TimeHelperCalender");

        // TestCode: Anuraag
//        cal = null;
//        Log.d("savedTimeReturn",DateTimeHelper.convertDateToString(cal.getTime()));


    }

                     /////---------------- Messages string --------------------////


    public static Uri getMailMessageUri(Context context ,String subject , String body)
    {


        if(body.equals(""))
         body = "Hi I am  "+AppHelper.getUserProfileGlobalData().getProfileData().getName()+", my user name is "+AppHelper.getUserProfileGlobalData().getProfileData().getEmail()+" \n I have a doubt regarding ...";


        String mailTo = "mailto:" + context.getString(R.string.contact_mail) +
                "?&subject=" + subject +
                "&body=" + Uri.encode(body);



        return Uri.parse(mailTo);
    }

    //-----------------------------JSON Helper----------//


    public static String generateRandomNumber()
    {
        String randomString="";

        final int min = 1000;
        final int max = 9999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String timestamp;

         timestamp =    new SimpleDateFormat(TimeStampPattern).format(  DateTimeHelper.getDatePojo().getCurrentDate);

        randomString = timestamp+"_"+random;

        return randomString;

    }
    ///----------------clipboard helper-------------///

    public static void copyToClipboard(Context context,String text)
    {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
    }

    ////------------------ main class -------------////////////////
    public static void main(String[] args)
    {


    }



    public static void setStatusBarColor(Activity activityRef, int color)
    {
        if (Build.VERSION.SDK_INT >= 21) {
            activityRef.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activityRef.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activityRef.getWindow().setStatusBarColor(ContextCompat.getColor(activityRef, color));
        }
    }


}
