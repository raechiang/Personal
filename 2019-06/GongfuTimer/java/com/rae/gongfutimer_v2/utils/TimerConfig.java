package com.rae.gongfutimer_v2.utils;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class TimerConfig implements Serializable, Comparable<TimerConfig>
{
    private UUID configId;
    @NonNull
    private String name;
    private long[] timerSeconds;
    private String iconStyle;
    private boolean isFavorite;
    private boolean isDefault;

    // empty constructor
    public TimerConfig()
    {
        this.configId = UUID.randomUUID();
        this.name = "Unsaved Timer"; // TODO: Do I want this here
        this.timerSeconds = new long[1];
        timerSeconds[0] = 1;
        this.iconStyle = "default"; // TODO: put default icon file name here
        isFavorite = false;
        isDefault = false;
    }

    // constructor for default timers
    TimerConfig(String name, long[] timerSeconds, String iconStyle, boolean isFavorite, boolean isDefault)
    {
        this.configId = UUID.randomUUID();
        this.name = name;
        this.timerSeconds = new long[timerSeconds.length];
        System.arraycopy(timerSeconds, 0, this.timerSeconds, 0, timerSeconds.length);
        this.iconStyle = iconStyle;
        this.isFavorite = isFavorite;
        this.isDefault = isDefault;
    }

    // constructor for other timers
    public TimerConfig(String name, long[] timerSeconds, String iconStyle, boolean isFavorite)
    {
        this.configId = UUID.randomUUID();
        this.name = name;
        this.timerSeconds = new long[timerSeconds.length];
        System.arraycopy(timerSeconds, 0, this.timerSeconds, 0, timerSeconds.length);
        this.iconStyle = iconStyle;
        this.isFavorite = isFavorite;
        isDefault = false;
    }

    // Getters and Setters
    //  Getters
    public UUID getConfigId()
    {
        return configId;
    }
    public String getName()
    {
        return name;
    }
    public long[] getTimerSeconds()
    {
        return timerSeconds;
    }
    public String getIconStyle()
    {
        return iconStyle;
    }
    public boolean getIsFavorite()
    {
        return isFavorite;
    }
    public boolean getIsDefault()
    {
        return isDefault;
    }
    public int getTotalInfusions()
    {
        return timerSeconds.length;
    }
    //  Setters
    public void setName(String name)
    {
        if (name != null)
        {
            if (!(this.name.equals(name)))
            {
                this.name = name;
                unsetDefault();
            }
        }
    }
    public void setTimerSeconds(long[] newTimerSeconds)
    {
        if (!(Arrays.equals(timerSeconds, newTimerSeconds)))
        {
            timerSeconds = new long[newTimerSeconds.length];
            System.arraycopy(newTimerSeconds, 0, timerSeconds, 0, newTimerSeconds.length);
            unsetDefault();
        }
    }
    public void setFavorite(boolean isFavorite)
    {
        if (this.isFavorite != isFavorite)
        {
            this.isFavorite = isFavorite;
            unsetDefault();
        }
    }

    @Override
    public int compareTo(TimerConfig other)
    {
        // negative if current < other
        // positive if current > other
        // 0 if current = other

        // if only one of them is a favorite, it should take priority
        // otherwise it must be compared further to find out
        if (this.isFavorite ^ other.isFavorite)
        {
            if (this.isFavorite)
            {
                // current > other
                return -1; // neg because Collections sorts lower to greater
            }
            else
            {
                // current < other
                return 1;
            }
        }

        int nameCheck = name.compareToIgnoreCase(other.name);
        if (nameCheck != 0)
        {
            return nameCheck;
        }
        // otherwise, nameCheck = 0, meaning the names are the same

        return configId.compareTo(other.configId);
    }

    public boolean isSimilar(TimerConfig other)
    {
        // checks only essential info
        return (this.name.equals(other.name)
                && Arrays.equals(this.timerSeconds, other.timerSeconds));
    }

    private void unsetDefault()
    {
        this.isDefault = false;
    }

    @Override
    public String toString()
    {
        return ("(" + name + ": " + Arrays.toString(timerSeconds) + ")");
    }
}