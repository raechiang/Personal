package com.rae.gongfutimer_v2.utils;

import java.util.Locale;

/**
 * This class represents a simple two-digit timer (one that fits in mm:ss format). It stores the
 * timer in seconds and contains some methods for manipulating the value into different forms.
 */
public class TwoDigitTimer
{
    /**
     * A timer in seconds.
     */
    private long timerInSecs;

    /**
     * The empty constructor.
     */
    public TwoDigitTimer()
    {
        timerInSecs = 1;
    }
    /**
     * The constructor that requires an initial timer to instantiate {@link #timerInSecs}. It checks
     * the bounds to make sure it can fit in an mm:ss format.
     * @param timerInSecs
     */
    public TwoDigitTimer(long timerInSecs)
    {
        if (checkTimerBounds(timerInSecs))
        {
            this.timerInSecs = timerInSecs;
        }
        else
        {
            if (timerInSecs >= 6000)
            {
                // sets to highest possible value
                this.timerInSecs = 5999;
            }
            else
            {
                // sets to lowest possible value
                this.timerInSecs = 1;
            }
        }
    }

    /**
     * This checks if a passed long timerInSeconds is within the range to fit in an mm:ss format,
     * which means it must be in (0,6000).
     * @param timerInSeconds - The timer to check, represented in seconds.
     * @return - True if the timer is within (0,6000), false if not.
     */
    public static boolean checkTimerBounds(long timerInSeconds)
    {
        return (timerInSeconds > 0 && timerInSeconds < 6000);
    }

    /**
     * This checks if the number of infusions or steps is within the range of [0,99].
     * @param reps - The number of infusions or steps.
     * @return - True if the reps is within [0,99].
     */
    public static boolean checkInfusionBounds(int reps)
    {
        return (reps > 0 && reps <= 99);
    }

    // Simple getters and setters.
    /**
     * This sets the {@link #timerInSecs}.
     * @param timer - A timer in seconds.
     */
    public void setTimerInSecs(long timer)
    {
        if (checkTimerBounds(timer))
        {
            this.timerInSecs = timer;
        }
        else
        {
            if (timer > 6000)
            {
                // set to max value
                this.timerInSecs = 5999;
            }
            else
            {
                // set to min value
                this.timerInSecs = 1;
            }
        }
    }
    /**
     * This gets the {@link #timerInSecs}
     * @return - The timer in seconds.
     */
    public long getTimerInSecs()
    {
        return timerInSecs;
    }

    /**
     * This calculates and returns only the minutes of the {@link #timerInSecs}.
     * @return - The minutes of the timerInSecs.
     */
    public long getMins()
    {
        return timerInSecs / 60;
    }
    /**
     * This calculates and returns only the seconds of the {@link #timerInSecs}.
     * @return - The seconds of the timerInSecs.
     */
    public long getSecs()
    {
        return timerInSecs % 60;
    }

    /**
     * This converts only the minutes of the {@link #timerInSecs} to a two-digit character array
     * with leading zeroes.
     * @return - A character array of the minutes as two numbers with leading zeroes.
     */
    public char[] getMinCharArray()
    {
        long mins = timerInSecs / 60;
        String s = String.format(Locale.US, "%02d", mins);
        return s.toCharArray();
    }

    /**
     * This converts only the seconds of the {@link #timerInSecs} to a two-digit character array
     * with leading zeroes.
     * @return - A character array of the seconds as two numbers with leading zeroes.
     */
    public char[] getSecCharArray()
    {
        long secs = timerInSecs % 60;
        String s = String.format(Locale.US, "%02d", secs);
        return s.toCharArray();
    }

    /**
     * This converts {@link #timerInSecs} into a four-digit character array with leading zeroes, in
     * the form of mmss.
     * @return - A character array of the minutes and seconds with leading zeroes for both parts.
     */
    public char[] toCharArray()
    {
        long mins = timerInSecs / 60;
        long secs = timerInSecs % 60;

        String s = String.format(Locale.US, "%02d%02d", mins, secs);
        return s.toCharArray();
    }
}
