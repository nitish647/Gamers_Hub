package com.nitish.gamershub.utils;

public class StringHelper {


    public static boolean isStringNullOrEmpty(String string)
    {
        return ( isStringNull(string) || isStringEmpty(string) );
    }
    public static boolean isStringEmpty(String string,boolean trim){

        if(trim)
        {
            string = string.trim();
        }
        return isStringEmpty(string);

    }

    public static boolean isStringEmpty(String string){
        return  string.isEmpty();
    }
    public static boolean isStringNull(String string)
    {
        return  string==null;
    }
}
