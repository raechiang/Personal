package com.rae.gongfutimer_v1.utils;

import com.rae.gongfutimer_v1.interfaces.QuickTimerString;

/**
 * This class implements the methods required by the interface
 * {@link com.rae.gongfutimer_v1.interfaces.QuickTimerString}. It also contains private fields for
 * the character array representing the string, {@link #data}, and the {@link #size} of the array.
 */
public abstract class AbstractQuickTimerString implements QuickTimerString
{
    /**
     * The size of the {@link #data} array.
     */
    private int size;
    /**
     * The character array representing the QuickTimerString.
     */
    private char[] data;

    /**
     * Constructor that sets the size to the given size and makes a character array of that size,
     * containing values all of '0'.
     * @param size the length of the character array
     */
    AbstractQuickTimerString(int size)
    {
        if (size > 0)
        {
            this.size = size;
        }
        else
        {
            // defaults to 2
            this.size = PLACE_VALUES;
        }
        this.data = new char[this.size];
        zeroAll();
    }

    @Override
    public void shiftForwardBy(char input)
    {
        System.arraycopy(data, 1, data, 0, size - 1);
        data[size - 1] = input;
    }

    @Override
    public void shiftBackwardOnce()
    {
        System.arraycopy(data, 0, data, 1, size - 1);
        data[0] = '0';
    }

    @Override
    public void zeroAll()
    {
        for (int i = 0; i < size; ++i)
        {
            data[i] = '0';
        }
    }

    @Override
    public boolean isAllZero()
    {
        for (int i = 0; i < size; ++i)
        {
            if (data[i] != '0')
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public char[] getCharArray()
    {
        return data;
    }
}
