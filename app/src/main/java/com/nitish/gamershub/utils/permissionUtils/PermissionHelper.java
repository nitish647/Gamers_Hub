package com.nitish.gamershub.utils.permissionUtils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public  class PermissionHelper {







    public static void check_permission(Activity activity,ArrayList<String> permissions, int request_code,PermissionResultCallback permissionResultCallback) {

        if (checkAndRequestPermissions(activity,permissions, request_code)) {
            permissionResultCallback.onPermissionGranted(request_code);

        }

    }



    public static boolean checkAndRequestPermissions(Activity activity,ArrayList<String> permissions, int request_code) {

        if (permissions.size() > 0) {
           ArrayList<String> listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(activity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                requestForPermission(activity,  listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                return false;
            }
        }

        return true;
    }

    public static void requestForPermission(Activity current_activity,String[] listPermissionsNeeded,int request_code)
    {
        ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded, request_code);

    }

    public static Boolean checkPermissionOnly( Context context,String[] permissions,String tag)
    {
        boolean isPermGranted = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermGranted = false;
                break;
            }
        }


        return  isPermGranted;
    }


    public static void onRequestPermissionsResult(
            Activity activity,
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults,
            PermissionResultCallback callback) {

        if (callback == null || permissions.length == 0) {
            return;
        }

        List<String> deniedPermissions = getDeniedPermissions(permissions, grantResults);

        boolean allPermissionsGranted = true;

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            callback.onPermissionGranted(requestCode);
        } else {
            boolean showRationale = false;
            for (String permission : deniedPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    showRationale = true;
                    break;
                }
            }

            callback.onPermissionDenied(requestCode, deniedPermissions, showRationale);

        }

    }


    private static List<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }


    public static interface PermissionResultCallback {
        void onPermissionGranted(int request_code);

     default   void onPartialPermissionGranted(int request_code, ArrayList<String> granted_permissions){};

        void onPermissionDenied(int requestCode, List<String> deniedPermissions,boolean neverAskAgain);

    }
}
