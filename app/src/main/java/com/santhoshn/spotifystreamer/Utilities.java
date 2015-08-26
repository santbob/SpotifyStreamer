package com.santhoshn.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by santhosh on 22/08/15.
 */
public class Utilities {

    /**
     * Formats the time in milliseconds to mm:ss
     * */
    public static String getFormatedTime(long milliSeconds) {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
    }

    /**
     * Calculates Percentage completion
     * returns the int number used to update seekbar
     * */
    public static int getProgressPercentage(int currentDuration, int totalDuration){
        return (int)(currentDuration * 100)/totalDuration;
    }

    /**
     * Converts the progress (scrubbing) to milliseconds based on total Duration
     * returns current duration in milliseconds
     * */
    public static int getCompletedTime(int progress, int totalDuration) {
        return (int)(totalDuration * progress)/100;
    }

    public static String getCountry(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_country_key),
                context.getString(R.string.pref_country_default_value));
    }

    public static boolean isLockScreenNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_enable_notification_lockscreen_key),
                Boolean.valueOf(context.getString(R.string.pref_enable_notification_lockscreen_default_value)));
    }

    public static boolean isDrawerNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_enable_notification_drawer_key),
                Boolean.valueOf(context.getString(R.string.pref_enable_notification_drawer_default_value)));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}