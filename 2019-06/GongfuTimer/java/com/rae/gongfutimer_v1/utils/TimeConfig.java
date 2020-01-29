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
    //private long timerIncrement;
    //private long[] timerIncrements;
    private int totalInfusions;
    private TimeConfigIncrements timerIncrements;
    //private boolean isLinear;
    private boolean isDefault;
    private boolean isFavorite;
    private String notes;
    /*
     * color, sound, animation?
     * For example: temperature_c, temperature_f
     */

    // Custom pairs must follow format of "<type>:<data>;<type>:<data>;" and so on
    public TimeConfig(UUID id, String name, long initial, /*long increment,*/ long[] increments, boolean isLinear, int totalInfusions, String notes)
    {
        this.configId = id;
        this.name = name;
        this.timerInitial = initial;
        //this.timerIncrement = increment;
        this.totalInfusions = totalInfusions;
        this.timerIncrements = new TimeConfigIncrements(increments, isLinear);

        this.isDefault = false;
        this.isFavorite = false;
        this.notes = notes;
    }

    public TimeConfig(UUID id, String name, long initial, /*long increment,*/ long[] increments, boolean isLinear, int totalInfusions, String notes, boolean isDefault)
    {
        this.configId = id;
        this.name = name;
        this.timerInitial = initial;
        //this.timerIncrement = increment;
        this.totalInfusions = totalInfusions;
        this.isDefault = isDefault;
        this.timerIncrements = new TimeConfigIncrements(increments, isLinear);

        this.isFavorite = false;
        this.notes = notes;
    }

    TimeConfig(UUID id, String name, long initial, long increment, int totalInfusions, String notes, boolean isDefault)
    {
        this.configId = id;
        this.name = name;
        this.timerInitial = initial;
        this.totalInfusions = totalInfusions;
        timerIncrements = new TimeConfigIncrements(increment);
        this.isFavorite = false;
        this.notes = notes;
        this.isDefault = true;
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
        //this.timerIncrement = 0;
        //this.isLinear = true;
        //this.timerIncrements = new long[1];
        this.timerIncrements = new TimeConfigIncrements();
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
    /*
    public long[] getTimerIncrements()
    {
        return timerIncrements;
    }
    */
    /*
    public long getTimerIncrement()
    {
        return timerIncrement;
    }
    */
    public long getTimerIncrement(int position)
    {
        return timerIncrements.get(position);
    }
    public int getTotalInfusions()
    {
        return totalInfusions;
    }
    public boolean getIsDefault()
    {
        return isDefault;
    }
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
    /*
    public void setTimerIncrement(long increment)
    {
        if (this.timerIncrement != increment)
        {
            this.timerIncrement = increment;
            unsetDefault();
        }
    }
    */
    public void setTimerIncrements(long[] newIncrements, boolean isLinear)
    {
        timerIncrements.setIncrements(newIncrements, isLinear);
    }
    public boolean setTimerIncrement(int position, long value)
    {
        boolean wasSuccessful = timerIncrements.set(position, value);
        if (wasSuccessful)
        {
            unsetDefault();
        }
        return wasSuccessful;
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
        String s = "(" + name + "=" + timerInitial + "+" + timerIncrements.toString() + "for" + totalInfusions + ")";
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
                //&& this.timerIncrement == other.timerIncrement
                && this.timerIncrements.isEqual(other.timerIncrements)
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
