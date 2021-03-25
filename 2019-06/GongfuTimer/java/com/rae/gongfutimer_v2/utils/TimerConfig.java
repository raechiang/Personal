package com.rae.gongfutimer_v2.utils;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.rae.gongfutimer_v2.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * This represents a timer configuration. Each configuration is given a unique ID using UUID. Each
 * requires a name, an array of the timers in seconds, the style of the configuration icon, whether
 * or not a timer has been set as a Favorite, whether or not a timer has come from the XML of
 * default configurations, and the color to set its icon.
 */
public class TimerConfig implements Serializable, Comparable<TimerConfig>
{
    /**
     * A unique ID for the configuration.
     */
    private UUID configId;
    /**
     * The name of the timer configuration that will be displayed for the user to identify.
     */
    @NonNull
    private String name;
    /**
     * An array of the timers in seconds.
     */
    private long[] timerSeconds;
    /**
     * The integer ID that corresponds with the style of the icon to use.
     */
    private int iconStyle;
    /**
     * This indicates if the timer configuration has been set as a favorite. In this application, it
     * will cause the timer to appear at the top of the LoadConfigActivity's list of timers.
     */
    private boolean isFavorite;
    /**
     * This indicates if the timer configuration was parsed from the default configurations XML and
     * has never been edited.
     */
    private boolean isDefault;
    /**
     * This is the color that the icon should be set to.
     */
    private int iconColor;

    /**
     * An empty constructor. It defaults to having the name "Unnamed Timer", a single step timer of
     * 1 second, the classic icon style (type 1), and a light green color.
     */
    public TimerConfig()
    {
        this.configId = UUID.randomUUID();
        this.name = "Unnamed Timer";
        this.timerSeconds = new long[1];
        timerSeconds[0] = 1;
        this.iconStyle = R.drawable.ic_gongfutimerlogo01;
        this.iconColor = Color.parseColor("#50AD14");
        isFavorite = false;
        isDefault = false;
    }

    /**
     * The constructor used for default timers.
     * @param name - The name of the timer.
     * @param timerSeconds - The array containing all of the seconds of the timers.
     * @param iconColor - The color of the icon.
     * @param isFavorite - Indicates if the timer is a favorited timer.
     * @param isDefault - Indicates if the timer comes from the default configurations xml.
     */
    TimerConfig(String name, long[] timerSeconds, String iconColor, boolean isFavorite, boolean isDefault)
    {
        // TODO: If this is for default timers only, maybe should change it a bit. See reasons below
        this.configId = UUID.randomUUID();
        this.name = name;
        this.timerSeconds = new long[timerSeconds.length]; // COULD construct this here?
        System.arraycopy(timerSeconds, 0, this.timerSeconds, 0, timerSeconds.length);
        this.iconStyle = R.drawable.ic_gongfutimerlogo01;
        this.iconColor = Color.parseColor(iconColor);
        this.isFavorite = isFavorite; // Needing a param for this seems redundant...
        this.isDefault = isDefault; // Needing a param for this seems redundant...
    }

    /**
     * The constructor for other timers.
     * @param name - The name of the timer.
     * @param timerSeconds - The array containing all of the seconds of the timers.
     * @param iconStyle - The type of icon to use.
     * @param iconColor - The color of the icon.
     * @param isFavorite - Indicates if the timer is a favorited timer.
     */
    public TimerConfig(String name, long[] timerSeconds, int iconStyle, String iconColor, boolean isFavorite)
    {
        this.configId = UUID.randomUUID();
        this.name = name;
        this.timerSeconds = new long[timerSeconds.length];
        System.arraycopy(timerSeconds, 0, this.timerSeconds, 0, timerSeconds.length);
        this.iconStyle = iconStyle;
        this.iconColor = Color.parseColor(iconColor);
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
    public int getIconStyle()
    {
        return iconStyle;
    }
    public int getIconColor()
    {
        return iconColor;
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
    public void setIconStyle(int id) {
        this.iconStyle = id;
        unsetDefault();
    }
    public void setIconColor(int color)
    {
        this.iconColor = color;
        unsetDefault();
    }

    /**
     * This compares two timer configurations. Timers that have been favorited are considered
     * greater than timers that have not been favorited, which can be useful for sorting, since
     * favorited timers should appear first. Otherwise, the name determines its ranking.
     * @param other - The other timer configuration to compare with.
     * @return - 0 if they are equal, -1 if other is less than current, 1 if greater than.
     */
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

    /**
     * This determines if two timer configurations have identical names and timers.
     * @param other - The other timer configuration to compare with.
     * @return - True if they have identical names and timers, false if not.
     */
    public boolean isSimilar(TimerConfig other)
    {
        // checks only essential info
        return (this.name.equals(other.name)
                && Arrays.equals(this.timerSeconds, other.timerSeconds));
    }

    /**
     * This method sets the Default to false. There is no way to revert an existing timer to be
     * marked as a default timer.
     */
    private void unsetDefault()
    {
        this.isDefault = false;
    }

    /**
     * This generates a string containing the name of the timer configuration as well as a list of
     * the timers in seconds.
     * @return - A readable string that represents the essential information of a TimerConfig.
     */
    @Override
    public String toString()
    {
        return ("(" + name + ": " + Arrays.toString(timerSeconds) + ")");
    }
}