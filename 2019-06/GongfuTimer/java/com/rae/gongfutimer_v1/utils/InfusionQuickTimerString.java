package com.rae.gongfutimer_v1.utils;

/**
 * The QuickTimerString that represents the total number of infusions (repetitions). It represents
 * a String of length 2.
 */
public class InfusionQuickTimerString extends AbstractQuickTimerString
{
    /**
     * Initializes the size to {@link com.rae.gongfutimer_v1.interfaces.QuickTimerString#PLACE_VALUES}
     * and makes a character array of that length filled with only '0's.
     */
    public InfusionQuickTimerString()
    {
        super(PLACE_VALUES);
    }

    /**
     * Converts the String's character array into an integer value.
     * @return an integer parsed from the String
     */
    public int getInfusionsInt()
    {
        return Integer.parseInt(new String(getCharArray()));
    }
}
