package com.rae.gongfutimer_v1.utils;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class TimeConfig implements Serializable, Comparable<TimeConfig>
{
    private UUID configId;
    @NonNull
    private String name;
    private long timerInitial;
    private long timerIncrement;
    private int totalInfusions;
    private boolean isDefault;
    private boolean isFavorite;
    private String notes;
    /*
     * color, sound, animation?
     * For example: temperature_c, temperature_f
     */

    // Custom pairs must follow format of "<type>:<data>;<type>:<data>;" and so on
    public TimeConfig(UUID id, String name, long initial, long increment, int totalInfusions, String notes)
    {
        this.configId = id;
        this.name = name;
        this.timerInitial = initial;
        this.timerIncrement = increment;
        this.totalInfusions = totalInfusions;
        this.isDefault = false;
        this.isFavorite = false;
        this.notes = notes;
    }

    public TimeConfig(UUID id, String name, long initial, long increment, int totalInfusions, String notes, boolean isDefault)
    {
        this.configId = id;
        this.name = name;
        this.timerInitial = initial;
        this.timerIncrement = increment;
        this.totalInfusions = totalInfusions;
        this.isDefault = isDefault;
        this.isFavorite = false;
        this.notes = notes;
    }

    /**
     * Makes a new TimeConfig with completely default values.
     */
    public TimeConfig()
    {
        this.configId = UUID.randomUUID();
        this.name = "(name)";
        // minimum values
        this.timerInitial = 1;
        this.timerIncrement = 0;
        this.totalInfusions = 1;
        this.isDefault = false;
        this.isFavorite = false;
    }

    // Getters and Setters
    // getters
    public UUID getConfigId()
    {
        return configId;
    }
    public String getName()
    {
        return name;
    }
    public long getTimerInitial()
    {
        return timerInitial;
    }
    public long getTimerIncrement()
    {
        return timerIncrement;
    }
    public int getTotalInfusions()
    {
        return totalInfusions;
    }
    public boolean getIsDefault() { return isDefault; }
    public boolean getIsFavorite()
    {
        return isFavorite;
    }
    // setters
    public void setName(String name)
    {
        if (!(this.name.equals(name)))
        {
            this.name = name;
            unsetDefault();
        }
    }
    public void setTimerInitial(long initial)
    {
        if (this.timerInitial != initial)
        {
            this.timerInitial = initial;
            unsetDefault();
        }
    }
    public void setTimerIncrement(long increment)
    {
        if (this.timerIncrement != increment)
        {
            this.timerIncrement = increment;
            unsetDefault();
        }
    }
    public void setTotalInfusions(int repetitions)
    {
        if (this.totalInfusions != repetitions)
        {
            this.totalInfusions = repetitions;
            unsetDefault();
        }
    }
    public void setFavorite(boolean isFavorite)
    {
        this.isFavorite = isFavorite;
        unsetDefault();
    }

    public String toString()
    {
        String s = "(" + name + "=" + timerInitial + "+" + timerIncrement + "for" + totalInfusions + ")";
        return s;
    }

    @Override
    public int compareTo(TimeConfig other)
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
        // nameCheck = 0, same names

        return configId.compareTo(other.configId);
    }

    public boolean isSimilar(TimeConfig other)
    {
        // Only checks essentials
        return (this.name.equals(other.name)
                && this.timerInitial == other.timerInitial
                && this.timerIncrement == other.timerIncrement
                && this.totalInfusions == other.totalInfusions);
    }

    private void unsetDefault()
    {
        // Unmarks default status if it has been changed
        this.isDefault = false;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }
}
