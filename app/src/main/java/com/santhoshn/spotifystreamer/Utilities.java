package com.santhoshn.spotifystreamer;

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
}