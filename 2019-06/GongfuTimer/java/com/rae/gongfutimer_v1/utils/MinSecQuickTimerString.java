package com.rae.gongfutimer_v1.utils;

/**
 * The QuickTimerString that represents the a timer string reminiscent of the mm:ss format, without
 * the colon. The String is of length 4.
 */
public class MinSecQuickTimerString extends AbstractQuickTimerString
{
    /**
     * Initializes the size to two times the number of
     * {@link com.rae.gongfutimer_v1.interfaces.QuickTimerString#PLACE_VALUES} and makes a character
     * array of that length filled with only '0's.
     */
    public MinSecQuickTimerString()
    {
        super(PLACE_VALUES * 2);
    }

    /**
     * Converts the String's character array into a long value in seconds.
     * @return a long value representing the string
     */
    public long getSecondsLong()
    {
        long minSecLong = Long.parseLong(new String(getCharArray()));
        long min = minSecLong / 100; // take minutes
        long sec = minSecLong % 100; // take seconds
        return ((60 * min) + sec); // convert to seconds
    }

    /**
     * Converts a provided long value (representing time in seconds) into a character array of mm:ss
     * format. Notably, it is not restricted by a fixed number of place values for the minutes.
     * @param timeInSecs the time in seconds to convert
     * @return a character array representing the time in seconds in an mmss format.
     */
    public static char[] getTimerString(long timeInSecs)
    {
        long min = timeInSecs / 60;
        long sec = timeInSecs % 60;
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(min / 10); // min tens
        sBuilder.append(min % 10); // min ones
        sBuilder.append(sec / 10); // sec tens
        sBuilder.append(sec % 10); // sec ones
        return sBuilder.toString().toCharArray();
    }
}
