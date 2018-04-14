package com.github.stevenrudenko.qst.location.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/** Application common utilities. */
public class AppUtils {
    /** Log tag. */
    private static final String TAG = AppUtils.class.getSimpleName();

    /**
     * Gets application version.
     * @param context used to get application version
     * @return application version
     */
    @Nullable
    public static String getVersion(@NonNull Context context) {
        try {
            final PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Can't read package info");
        }
        return null;
    }

}
