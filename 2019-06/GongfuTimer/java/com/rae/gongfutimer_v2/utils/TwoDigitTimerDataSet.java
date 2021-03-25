package com.rae.gongfutimer_v2.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains a list of TwoDigitTimers.
 */
public class TwoDigitTimerDataSet
{
    /**
     * The list of TwoDigitTimers.
     */
    private ArrayList<TwoDigitTimer> timerList;

    /**
     * The constructor.
     */
    public TwoDigitTimerDataSet()
    {
        timerList = new ArrayList<>();
    }

    /**
     * This adds all the timers from a timerList to this {@link #timerList}.
     * @param timerList - A list of timers to add to the current list of timers.
     * @return - True if addAll() succeeded, which is if the list has been changed.
     */
    public boolean addAll(List<TwoDigitTimer> timerList)
    {
        return this.timerList.addAll(timerList);
    }

    /**
     * This adds a single timer to this {@link #timerList}.
     * @param timer - The TwoDigitTimer to be added to the list.
     * @return - True if add() succeeded, which is if the list has been changed.
     */
    public boolean add(TwoDigitTimer timer)
    {
        return timerList.add(timer);
    }

    /**
     * This removes a TwoDigitTimer from the {@link #timerList} at the given index.
     * @param index - The index of the timer to remove.
     * @return - True if the index is in bounds and the list has removed the timer.
     */
    public boolean remove(int index)
    {
        if (index < timerList.size())
        {
            timerList.remove(index);
            return true;
        }
        return false;
    }

    /**
     * This edits the TwoDigitTimer at the given position, replacing the seconds with the given
     * value.
     * @param index - The index of the timer to edit.
     * @param value - The timer in seconds to replace the existing timer's seconds with.
     * @return - True if the index is in bounds and the list is modified.
     */
    public boolean edit(int index, long value)
    {
        if (index < timerList.size())
        {
            timerList.get(index).setTimerInSecs(value);
            return true;
        }
        return false;
    }

    /**
     * This returns the size (number of elements) of the list.
     * @return - The size of the list.
     */
    public int size()
    {
        return timerList.size();
    }

    /**
     * This returns the element at the given index.
     * @param index - The index of the element to retrieve.
     * @return - The TwoDigitTimer element at the index.
     */
    public TwoDigitTimer get(int index)
    {
        return timerList.get(index);
    }

    /**
     * This indicates whether there are elements in {@link #timerList}.
     * @return - True if there are no elements in the list.
     */
    public boolean isEmpty()
    {
        return timerList.isEmpty();
    }

    /**
     * This returns the {@link #timerList}.
     * @return - The ArrayList of TwoDigitTimers.
     */
    public ArrayList<TwoDigitTimer> getTimerList()
    {
        return timerList;
    }

    /**
     * This returns a long array of the TwoDigitTimers in {@link #timerList}.
     * @return - A long array of TwoDigitTimers.
     */
    public long[] getTimerLongArray()
    {
        long[] timers = new long[timerList.size()];
        for (int i = 0; i < timers.length; ++i)
        {
            timers[i] = timerList.get(i).getTimerInSecs();
        }
        return timers;
    }
}
