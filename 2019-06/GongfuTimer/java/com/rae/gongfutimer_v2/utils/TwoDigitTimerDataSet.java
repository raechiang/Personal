package com.rae.gongfutimer_v2.utils;

import java.util.ArrayList;
import java.util.List;

public class TwoDigitTimerDataSet
{
    private ArrayList<TwoDigitTimer> timerList;

    public TwoDigitTimerDataSet()
    {
        timerList = new ArrayList<>();
    }

    public boolean addAll(List<TwoDigitTimer> timerList)
    {
        return this.timerList.addAll(timerList);
    }

    public boolean add(TwoDigitTimer timer)
    {
        return timerList.add(timer);
    }

    public boolean remove(int position)
    {
        if (position < timerList.size())
        {
            timerList.remove(position);
            return true;
        }
        return false;
    }

    public boolean edit(int position, long value)
    {
        if (position < timerList.size())
        {
            timerList.get(position).setTimerInSecs(value);
            return true;
        }
        return false;
    }

    public int size()
    {
        return timerList.size();
    }

    public TwoDigitTimer get(int index)
    {
        return timerList.get(index);
    }

    public boolean isEmpty()
    {
        return timerList.isEmpty();
    }

    public ArrayList<TwoDigitTimer> getTimerList()
    {
        return timerList;
    }

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
