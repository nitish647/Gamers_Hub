package com.nitish.gamershub.Utils;

import android.os.Build;
import android.util.Log;

public class DeviceHelper {


    public static String getDeviceNameAndVersion() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        String androidVersion ="androidVersion : "+ android.os.Build.VERSION.SDK_INT+"";


        if (model.startsWith(manufacturer)) {
            return capitalize(model)+" "+androidVersion;
        } else {
            return capitalize(manufacturer) + " " + " "+model+" "+androidVersion;
        }

    }



    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
