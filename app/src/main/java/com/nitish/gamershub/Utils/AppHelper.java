package com.nitish.gamershub.Utils;

import static com.nitish.gamershub.Utils.ConstantsHelper.AdViewedStatsGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.FirebaseFCMToken;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHubDataGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserProfileGlobal;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nitish.gamershub.Pojo.FireBase.AdViewedStats;
import com.nitish.gamershub.Pojo.FireBase.GamersHubData;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;

import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class AppHelper {




    //--------------------login page -----------------//
    public static void saveGoogleSignInAccountUser(GoogleSignInAccount googleSignInAccount)
    {
        Paper.book().write(GoogleSignInAccountUser,googleSignInAccount);
    }

    public static GoogleSignInAccount getGoogleSignInAccountUser()
    {
       return (GoogleSignInAccount) Paper.book().read(GoogleSignInAccountUser);
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
        float currentVersionName = Float.parseFloat(getUserProfileGlobalData().getProfileData().getVersionName());
        float latestVersionName = Float.parseFloat(getGamersHubDataGlobal().getGamesData().getLatestVersionName());

        return currentVersionName>=latestVersionName ;


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

    public static void readCalenderData()
    {
        Date date= new Date();
        Calendar cal =      (Calendar) Paper.book().read("TimeHelperCalender");

        Log.d("savedTimeReturn",DateTimeHelper.convertDateToString(cal.getTime()));



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



}
