package com.nitish.gamershub.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataPassingHelper {

    public static String ConvertObjectToString(Object object)
    {

        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson= gsonBuilder.create();

      return   gson.toJson(object);
    }

    public static Object ConvertJsonStringToObject(String jsonString)
    {

        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson= gsonBuilder.create();

        return  gson.fromJson(jsonString,Object.class);
    }
}
