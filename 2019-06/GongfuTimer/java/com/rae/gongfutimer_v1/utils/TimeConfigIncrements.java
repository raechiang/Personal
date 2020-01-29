package com.rae.gongfutimer_v1.utils;

import java.io.Serializable;
import java.util.Arrays;

public class TimeConfigIncrements implements Serializable
{
    private int size;
    private long[] increments;
    private boolean isLinear;

    public TimeConfigIncrements()
    {
        size = 1;
        increments = new long[size];
        increments[0] = 0;
        this.isLinear = true;
    }

    public TimeConfigIncrements(long increment)
    {
        size = 1;
        increments = new long[size];
        increments[0] = increment;
        this.isLinear = true;
    }

    public TimeConfigIncrements(long[] increments, boolean isLinear)
    {
        if (isLinear)
        {
            size = 1;
            this.increments = new long[size];
            this.increments[0] = increments[0];
        }
        else
        {
            size = increments.length;
            this.increments = new long[size];
            System.arraycopy(increments, 0, this.increments, 0, size);
        }
    }

    public long get(int position)
    {
        if (position < 0)
        {
            return 0;
        }
        if (position >= size)
        {
            if (isLinear)
            {
                return increments[0];
            }
            else
            {
                return 0;
            }
        }
        // if position >= 0 && position < size
        return increments[position];
    }

    public boolean set(int position, long value)
    {
        if (position >= 0 && position < size)
        {
            increments[position] = value;
            return true;
        }
        return false;
    }

    public void setIncrements(long[] newIncrements, boolean isLinear)
    {
        this.isLinear = isLinear;
        if (this.isLinear)
        {
            size = 1;
            increments = new long[size];
            increments[0] = newIncrements[0];
        }
        else
        {
            size = newIncrements.length;
            increments = new long[size];
            System.arraycopy(newIncrements, 0, increments, 0, size);
        }
    }

    public boolean isEqual(TimeConfigIncrements other)
    {
        if (this.size == other.size && this.isLinear == other.isLinear)
        {
            for (int i = 0; i < size; ++i)
            {
                if (this.increments[i] != other.increments[i])
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return Arrays.toString(increments);
    }
}
