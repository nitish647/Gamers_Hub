package com.nitish.gamershub.utils.permissionUtils;

import android.Manifest;
import android.os.Build;

import java.util.ArrayList;

public class PermissionConstants {

    public static int NotificationREQ_CODE =211;

    public static ArrayList<String> getNotificationPermission()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayList.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        return arrayList;
    }
}
