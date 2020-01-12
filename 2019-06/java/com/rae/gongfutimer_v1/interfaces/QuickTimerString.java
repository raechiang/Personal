package com.rae.gongfutimer_v1.interfaces;

/**
 * An interface that describes the behavior of timer strings for the BlankConfigActivity (which
 * itself involves a quick set-up of only the three timer values: start timer, increment amount, and
 * total number of repetitions). BlankConfigActivity has a custom, fixed input system for which
 * there are only number inputs, backspace, and clear, and only "two" {@link #PLACE_VALUES} are used
 * for each individual component. That is, the start timer will have two place values for minutes
 * and two place values for seconds, the increment amount will have two place values for minutes and
 * two place values for seconds, and the number of repetitions also contains only two place values.
 * A mm:ss format is familiar for time, and so two place values is reasonable. Repetitions adopts
 * this restriction for a purely aesthetic reason--to maintain a symmetrical look. Given a number
 * input, the string must {@link #shiftForwardBy(char)}. A backspace will reverse that and
 * {@link #shiftBackwardOnce()}. A press of the clear button will {@link #zeroAll()}. The method
 * {@link #isAllZero()} just checks if the String contains only zeroes.
 */
public interface QuickTimerString
{
    /**
     * The number of place values for each component of the string.
     */
    public final static int PLACE_VALUES = 2;

    /**
     * Shifts the String left, dropping the leftmost character and appending the input to the right.
     * @param input the character (hopefully a number) to shift the string by
     */
    public void shiftForwardBy(char input);
    /**
     * Shifts the String right, dropping the rightmost character and appending a zero to the left.
     */
    public void shiftBackwardOnce();
    /**
     * Sets all characters in the String to '0'.
     */
    public void zeroAll();

    /**
     * Checks if all characters in the String are '0'.
     * @return true if all characters in the String are '0'
     */
    public boolean isAllZero();

    /**
     * Simply returns a character array representing the String.
     * @return a character array representing the String
     */
    public char[] getCharArray();
}
