package com.nitish.gamershub.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public  class DateTimeHelper {

  public   static String time_7_am = "07:00:00";
  public static String simpleDateFormatPattern ="yyyy-MM-dd HH:mm:ss";
    public static DatePojo datePojo()
    {





        DatePojo datePojo = new DatePojo();

        datePojo.setGetCurrentDateString(new SimpleDateFormat(simpleDateFormatPattern).format(new Date()));





        return datePojo;



    }
    public static String  convertDateToString(Date date)
    {

        return  DateTimeHelper.datePojo().getSimpleDateFormat().format(date);
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
