package com.sybrin.firebasecamerasourcelibrary.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionsHandler {

    private static boolean checkedPermissions = false;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static Activity activity;
    private static Runnable successMethod;

    public static boolean handlePermissions(Activity act, Runnable onSuccess) {
        successMethod = onSuccess;
        activity = act;
        if (!checkedPermissions && !allPermissionsGranted()) {
            ActivityCompat.requestPermissions(activity, getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            checkedPermissions = true;
            return true;
        }
    }

    private static boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static String[] getRequiredPermissions() {
        String[] ps = new String[]{Manifest.permission.CAMERA};
        return ps;
    }

    public static void onPermissionResult(@NonNull int[] grantResults) {
        try {
            ArrayList<Integer> unGrantedPermissions = new ArrayList<Integer>();

            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissions.add(grant);
                }
            }

            if (unGrantedPermissions.isEmpty()) {
                if (null != successMethod) {
                    successMethod.run();
                }
            } else {
                throw new Exception("Required permissions not granted");
            }
        } catch (Exception e) {
            activity.finish();
            e.printStackTrace();
        }
    }

}
