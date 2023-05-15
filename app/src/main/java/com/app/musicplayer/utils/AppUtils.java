package com.app.musicplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    public static final int PERMISSION_REQUESTS = 1;

    public static String[] getRequiredPermissions(Context context) {
        try {
            Log.v("TAG", "getRequiredPermissions called.....");
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    public static boolean allPermissionsGranted(Context context) {
        Log.v("TAG", "allPermissionsGranted called.....");
        for (String permission : getRequiredPermissions(context)) {
            if (isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static void getRuntimePermissions(Context context) {
        try {
            Log.v("TAG", "getRuntimePermissions called.....");
            List<String> allNeededPermissions = new ArrayList<>();
            for (String permission : getRequiredPermissions(context)) {
                if (isPermissionGranted(context, permission)) {
                    allNeededPermissions.add(permission);
                }
            }

            if (allNeededPermissions.size() > 0) {
                ActivityCompat.requestPermissions(((Activity) context), allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static void hideKeyboardOnClick(Context context, View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
