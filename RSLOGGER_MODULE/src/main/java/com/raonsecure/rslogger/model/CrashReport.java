package com.raonsecure.rslogger.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashReport {
    private static final String SHARED_PREFERENCES_FILE = "rsch_preferences";
    private static final String SHARED_PREFERENCES_FIELD_CRASHDATA = "last_crash_data";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";

    public CrashReport() { }

    public static boolean isHasLastCrashReport() {
        boolean isHasCrash = (Configuration.getInstance().getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(SHARED_PREFERENCES_FIELD_CRASHDATA, "")).length() > 0;
        return isHasCrash;
    }

    public static void saveLastCrashReport(String stackTraceString) {
        Configuration.getInstance().getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putString(SHARED_PREFERENCES_FIELD_CRASHDATA, makeCrashReport(stackTraceString)).commit();
    }

    public static String loadLastCrashReportWithFlush() {
        Context context = Configuration.getInstance().getRsContext();
        String crashReport = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(SHARED_PREFERENCES_FIELD_CRASHDATA, "");

        //flush
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putString(SHARED_PREFERENCES_FIELD_CRASHDATA, "").commit();

        return crashReport;
    }

    public static void setLastCrashTimestamp(long timestamp) {
        Configuration.getInstance().getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit();
    }

    public static long getLastCrashTimestamp () {
        return Configuration.getInstance().getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1);
    }


    private static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static String makeCrashReport(String stackTraceString) {
        Context context = Configuration.getInstance().getRsContext();
        String LINE_SEPARATOR = "\n";
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("\n***** RSCrash HANDLER Library ");
        errorReport.append("\n***** by RaonSecure \n");
        errorReport.append("\n***** DEVICE INFO \n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Manufacturer: ");
        errorReport.append(Build.MANUFACTURER);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n***** APP INFO \n");
        String versionName = getVersionName(context);
        errorReport.append("Version: ");
        errorReport.append(versionName);
        errorReport.append(LINE_SEPARATOR);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
        if (!TextUtils.isEmpty(firstInstallTime)) {
            errorReport.append("Installed On: ");
            errorReport.append(firstInstallTime);
            errorReport.append(LINE_SEPARATOR);
        }
        String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
        if (!TextUtils.isEmpty(lastUpdateTime)) {
            errorReport.append("Updated On: ");
            errorReport.append(lastUpdateTime);
            errorReport.append(LINE_SEPARATOR);
        }
        errorReport.append("Current Date: ");
        errorReport.append(dateFormat.format(currentDate));
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n***** ERROR LOG \n");
        errorReport.append(stackTraceString);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n***** END OF LOG *****\n");

        return errorReport.toString();
    }
}
