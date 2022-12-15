package com.nitish.gamershub.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.instacart.library.truetime.TrueTime;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.tempo.Tempo;

public  class DateTimeHelper extends Application {

   public static Context context ;
  public   static String time_7_am = "07:00:00";
  public static String simpleDateFormatPattern ="yyyy-MM-dd HH:mm:ss";

    public static String TimeStampPattern ="HHmmss";
    @Override
    public void onCreate() {
        super.onCreate();
        context= getApplicationContext();
    }

    public static DatePojo getDatePojo()
    {

        DatePojo datePojo = new DatePojo();



//        datePojo.setGetCurrentDateString(getSimpleDateFormat().format(new Date()));
//        datePojo.setGetCurrentDate(new Date());


        try {



            if(TrueTime.isInitialized()) {
                datePojo.setGetCurrentDateString(getSimpleDateFormat().format(TrueTime.now()));
                datePojo.setGetCurrentDate(TrueTime.now());
            }
            else {
                 AsyncTask.execute(new Runnable() {
                     @Override
                     public void run() {
                         try {

                             TrueTime.build().initialize();
                         } catch (IOException e) {
                             Log.d("pError","error in time114 "+e);
                             e.printStackTrace();
                         }
                     }
                 });

                if(Tempo.isInitialized()) {

                    datePojo.setGetCurrentDateString(getSimpleDateFormat().format(new Date(Tempo.nowOrNull())));
                    datePojo.setGetCurrentDate(new Date(Tempo.nowOrNull()));
                }
                else {
                    datePojo.setGetCurrentDateString(getSimpleDateFormat().format(new Date()));
                    datePojo.setGetCurrentDate(new Date());
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Tempo.initialize(((Activity) context).getApplication());
                            } catch (Exception e) {
                                Log.d("pError","error in time112 "+e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }catch (Exception e)
        {


            Log.d("pError","error in time113 "+e);
        }


//        datePojo.setGetCurrentDateString(getSimpleDateFormat().format(new Date(TimeManager.getInstance().getServerTime())));
//        datePojo.setGetCurrentDate(new Date(TimeManager.getInstance().getServerTime()));

//                datePojo.setGetCurrentDateString(getSimpleDateFormat().format(new Date(Tempo.nowOrNull())));
//                datePojo.setGetCurrentDate(new Date(Tempo.nowOrNull()));
        return datePojo;



    }
    public static boolean isDateCorrect(String date)
    {
        // checking if the date is correct and is parsable
        boolean isDateCorrect ;
        try {
            getSimpleDateFormat().parse(date);
            isDateCorrect = true;
        } catch (ParseException e) {
            e.printStackTrace();
            isDateCorrect = false;
        }
        return isDateCorrect;
    }
    public static  SimpleDateFormat getSimpleDateFormat()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDateFormatPattern);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat;

    }

    public static String formatTimeToMMSS(int minutes , int seconds)
    {

        return String.format(Locale.getDefault(),"%02d:%02d",  minutes,seconds);
    }
    public static String formatTimeToMMSS( int seconds)
    {

        return String.format(Locale.getDefault(),"%02d:%02d",  (seconds / 60) % 60, seconds % 60);
    }

    public float convertSecondsToMinute(int seconds)
    {

        return  (float) seconds/60;
    }
    public static String  convertDateToString(Date date)
    {

        return  DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date);
    }
    public static Date convertStringIntoDate(String dateString)
    {
        Date date1 =new Date();

        try {
            date1 = new SimpleDateFormat(simpleDateFormatPattern).parse(dateString);
        } catch (ParseException e) {

            Log.d("gError","error in parsing time 11234: "+e);
            e.printStackTrace();
        }
        return date1;


    }
    // with this function will reset the time of a date to a given time
    public static String resetDateToATime(Object dateObj, String time)
    {
        Log.d("pDate","reset date object "+dateObj);

        Date date=null;

        if(dateObj instanceof String)
        {
            date = convertStringIntoDate(dateObj+"");
        }
        else
        if(dateObj instanceof Date)
        {
            date = (Date) dateObj;
        }
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        int mYear = cal.get(Calendar.YEAR); // current year
        int mMonth = cal.get(Calendar.MONTH)+1; //fixing the month starts from 0 issue
        int mDay = cal.get(Calendar.DAY_OF_MONTH); // current day


        String timeToReset =""+ mYear+"-"+mMonth +"-"+mDay;

        timeToReset = timeToReset+" "+time;

        return timeToReset;

    }

    public static class DatePojo{
        String getCurrentDateString;
        Date getCurrentDate;

        SimpleDateFormat simpleDateFormat;
        String resetDateToATime = time_7_am;






        public DatePojo() {
        }

        public DatePojo(String resetDateToATime) {
            this.resetDateToATime = resetDateToATime;
        }

        public Date getGetCurrentDate() {
            return getCurrentDate;
        }

        public void setGetCurrentDate(Date getCurrentDate) {
            this.getCurrentDate = getCurrentDate;
        }

        public String getResetDateToATime() {
            return resetDateToATime;
        }

        public void setResetDateToATime(String resetDateToATime) {
            this.resetDateToATime = resetDateToATime;
        }


        public SimpleDateFormat getSimpleDateFormat() {
            return new SimpleDateFormat(simpleDateFormatPattern);
        }

        public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
            this.simpleDateFormat = simpleDateFormat;
        }

        public String getGetCurrentDateString() {
            return getCurrentDateString;
        }

        public void setGetCurrentDateString(String getCurrentDateString) {
            this.getCurrentDateString = getCurrentDateString;
        }
    }
}
