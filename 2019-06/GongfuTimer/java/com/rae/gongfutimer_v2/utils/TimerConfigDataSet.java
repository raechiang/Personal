package com.rae.gongfutimer_v2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TimerConfigDataSet
{
    private ArrayList<TimerConfig> timerConfigList;

    public TimerConfigDataSet()
    {
        timerConfigList = new ArrayList<>();
    }

    public boolean addAll(List<TimerConfig> timerConfigList)
    {
        boolean returnValue = this.timerConfigList.addAll(timerConfigList);
        Collections.sort(this.timerConfigList);
        return returnValue;
    }

    // Add: one, defaults
    public boolean addNewTimerConfig(TimerConfig newTimerConfig)
    {
        boolean returnValue = timerConfigList.add(newTimerConfig);
        Collections.sort(timerConfigList);
        return returnValue;
    }
    public boolean addDefaultTimerConfigs(List<TimerConfig> defaultTimerConfigs)
    {
        boolean returnValue = false;
        List<TimerConfig> savedDefaultConfigs = getSavedDefaultConfigs();

        if (savedDefaultConfigs == null || savedDefaultConfigs.isEmpty())
        {
            // no defaults exist at the moment, so we can just add all of the defaults
            returnValue = timerConfigList.addAll(defaultTimerConfigs);
        }
        else
        {
            // only want to add if they do not exist in the set already
            for (TimerConfig currentDefault : defaultTimerConfigs)
            {
                // for all of the default configs
                if (!(containsTimerConfig(currentDefault, savedDefaultConfigs)))
                {
                    // if it is not in the set already, add it
                    returnValue |= timerConfigList.add(currentDefault);
                }
            }
        }
        if (returnValue)
        {
            Collections.sort(timerConfigList);
        }
        return returnValue;
    }
    private List<TimerConfig> getSavedDefaultConfigs()
    {
        List<TimerConfig> savedDefaultConfigs = new ArrayList<>();
        for (TimerConfig tc : timerConfigList)
        {
            if (tc.getIsDefault())
            {
                savedDefaultConfigs.add(tc);
            }
        }
        return savedDefaultConfigs;
    }
    private boolean containsTimerConfig(TimerConfig timerConfig, List<TimerConfig> timerConfigList)
    {
        for (TimerConfig current : timerConfigList)
        {
            if (current.isSimilar(timerConfig))
            {
                return true;
            }
        }
        return false;
    }

    // Delete: one, defaults, all
    public boolean deleteTimerConfig(int position, TimerConfig target)
    {
        UUID positionConfigId = timerConfigList.get(position).getConfigId();

        if (positionConfigId.equals(target.getConfigId()))
        {
            // want to see if the unique IDs match before deleting
            return deleteTimerConfig(position);
        }
        else
        {
            // they don't match, so they are not actually the same TimerConfig
            int truePosition = findPositionById(target.getConfigId());
            if (truePosition == -1)
            {
                // target could not be found in the list, no change occurs
                return false;
            }
            else
            {
                // found true target
                return deleteTimerConfig(truePosition);
            }
        }
    }
    private boolean deleteTimerConfig(int position)
    {
        if (position >= 0 && position < timerConfigList.size())
        {
            // bounds check
            TimerConfig removed = timerConfigList.remove(position);
            if (removed != null)
            {
                // success
                Collections.sort(timerConfigList);
                return true;
            }
        }
        // failure
        return false;
    }
    public boolean deleteDefaultTimerConfigs()
    {
        List<TimerConfig> newList = new ArrayList<>();
        for (TimerConfig current : timerConfigList)
        {
            if (!(current.getIsDefault()))
            {
                // collect non-default into new list
                newList.add(current);
            }
        }

        if (newList.size() < timerConfigList.size())
        {
            // only requires rewrite if a default timer has been found
            timerConfigList.clear();
            timerConfigList.addAll(newList);
            Collections.sort(timerConfigList);
            return true;
        }

        // otherwise, no change occurred
        return false;
    }
    public boolean deleteAllTimerConfigs()
    {
        if (timerConfigList.isEmpty())
        {
            // already empty, no changes
            return false;
        }
        timerConfigList.clear();
        return true;
    }

    // Edit: one
    public boolean editTimerConfig(int position, TimerConfig target)
    {
        if (target != null)
        {
            // check matching config and position
            if (timerConfigList.get(position).getConfigId().equals(target.getConfigId()))
            {
                timerConfigList.set(position, target);
                Collections.sort(timerConfigList);
                return true;
            }
            else
            {
                // they do not match, so find if there is a matching one
                int truePosition = findPositionById(target.getConfigId());
                if (truePosition != -1)
                {
                    // match found
                    timerConfigList.set(truePosition, target);
                    Collections.sort(timerConfigList);
                    return true;
                }
            }
        }
        return false;
    }

    private int findPositionById(UUID targetId)
    {
        for (int i = 0 ; i < timerConfigList.size(); ++i)
        {
            if (timerConfigList.get(i).getConfigId().equals(targetId))
            {
                return i;
            }
        }
        return -1;
    }

    public int size()
    {
        return timerConfigList.size();
    }
    public TimerConfig get(int index)
    {
        return timerConfigList.get(index);
    }
    public boolean isEmpty()
    {
        return timerConfigList.isEmpty();
    }
    public ArrayList<TimerConfig> getTimerConfigList()
    {
        return timerConfigList;
    }
}
