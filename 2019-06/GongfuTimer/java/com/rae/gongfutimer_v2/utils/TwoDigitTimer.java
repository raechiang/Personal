package com.rae.gongfutimer_v2.utils;

import java.util.Locale;

public class TwoDigitTimer
{
    private long timerInSecs;

    public TwoDigitTimer()
    {
        timerInSecs = 0;
    }
    public TwoDigitTimer(long timerInSecs)
    {
        this.timerInSecs = timerInSecs;
    }

    public static boolean checkTimerBounds(long timerInSeconds)
    {
        return (timerInSeconds > 0 && timerInSeconds < 6000);
    }

    public static boolean checkIncrementBounds(long timerInSeconds)
    {
        return (timerInSeconds >= 0 && timerInSeconds < 6000);
    }

    public static boolean checkInfusionBounds(int reps)
    {
        return (reps > 0 && reps <= 99);
    }

    public void setTimerInSecs(long timer)
    {
        this.timerInSecs = timer;
    }

    public long getTimerInSecs()
    {
        return timerInSecs;
    }

    public long getMins()
    {
        return timerInSecs / 60;
    }

    public long getSecs()
    {
        return timerInSecs % 60;
    }

    public char[] getMinCharArray()
    {
        long mins = timerInSecs / 60;
        String s = String.format(Locale.US, "%02d", mins);
        return s.toCharArray();
    }

    public char[] getSecCharArray()
    {
        long secs = timerInSecs % 60;
        String s = String.format(Locale.US, "%02d", secs);
        return s.toCharArray();
    }

    public char[] toCharArray()
    {
        long mins = timerInSecs / 60;
        long secs = timerInSecs % 60;

        String s = String.format(Locale.US, "%02d%02d", mins, secs);
        return s.toCharArray();
    }
}
