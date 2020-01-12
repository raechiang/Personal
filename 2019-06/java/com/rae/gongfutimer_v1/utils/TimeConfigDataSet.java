package com.rae.gongfutimer_v1.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TimeConfigDataSet
{
    private ArrayList<TimeConfig> timeConfigList;

    public TimeConfigDataSet()
    {
        timeConfigList = new ArrayList<>();
    }

    public boolean addAll(List<TimeConfig> timeConfigList)
    {
        boolean returnValue = this.timeConfigList.addAll(timeConfigList);
        Collections.sort(this.timeConfigList);
        return returnValue;
    }

    // Add: one, defaults
    public boolean addNewTimeConfig(TimeConfig newTimeConfig)
    {
        boolean returnValue = timeConfigList.add(newTimeConfig);
        Collections.sort(timeConfigList);
        return returnValue;
    }
    public boolean addDefaultTimeConfigs(List<TimeConfig> defaultTimeConfigs)
    {
        boolean returnValue = false;
        List<TimeConfig> savedDefaultConfigs = getSavedDefaultConfigs();

        if (savedDefaultConfigs == null || savedDefaultConfigs.isEmpty())
        {
            // no defaults exist at the moment, so we can just add all of the defaults
            returnValue |= timeConfigList.addAll(defaultTimeConfigs);
        }
        else
        {
            // only want to add if they do not exist in the set already
            for (TimeConfig currentDefault : defaultTimeConfigs)
            {
                // for all of the default configs
                if (!(containsTimeConfig(currentDefault, savedDefaultConfigs)))
                {
                    // if it already is not already in the set, add it
                    returnValue |= timeConfigList.add(currentDefault);
                }
            }
        }
        if (returnValue)
        {
            Collections.sort(timeConfigList);
        }
        return returnValue;
    }
    private List<TimeConfig> getSavedDefaultConfigs()
    {
        List<TimeConfig> savedDefaultConfigs = new ArrayList<>();
        for (TimeConfig tc : timeConfigList)
        {
            if (tc.getIsDefault())
            {
                savedDefaultConfigs.add(tc);
            }
        }
        return savedDefaultConfigs;
    }
    private boolean containsTimeConfig(TimeConfig timeConfig, List<TimeConfig> timeConfigList)
    {
        for (TimeConfig current : timeConfigList)
        {
            if (current.isSimilar(timeConfig))
            {
                return true;
            }
        }
        return false;
    }

    // Delete: one, defaults, all
    public boolean deleteTimeConfig(int position, TimeConfig targetTimeConfig)
    {
        UUID foundConfigId = timeConfigList.get(position).getConfigId();

        if (foundConfigId.equals(targetTimeConfig.getConfigId()))
        {
            // check received config ID and position's config ID match
            return deleteTimeConfig(position);
        }
        else
        {
            // if they don't match, find the matching one
            int truePosition = findPositionById(targetTimeConfig.getConfigId());
            if (truePosition == -1)
            {
                // could not find it, no change occurs
                return false;
            }
            else
            {
                // could find it
                return deleteTimeConfig(truePosition);
            }
        }
    }
    private boolean deleteTimeConfig(int position)
    {
        if (position >= 0 && position < timeConfigList.size())
        {
            // checking bounds
            TimeConfig removed = timeConfigList.remove(position);
            if (removed != null)
            {
                Collections.sort(timeConfigList);
                return true;
            }
        }
        return false;
    }
    private int findPositionById(UUID targetId)
    {
        for (int i = 0; i < timeConfigList.size(); ++i)
        {
            if (timeConfigList.get(i).getConfigId().equals(targetId))
            {
                return i;
            }
        }
        return -1;
    }
    public boolean deleteDefaultTimeConfigs()
    {
        List<TimeConfig> newList = new ArrayList<>();
        for (TimeConfig current : timeConfigList)
        {
            if (!(current.getIsDefault()))
            {
                // collect all the non-default into new list
                newList.add(current);
            }
        }

        if (newList.size() < timeConfigList.size())
        {
            // only rewrite if a default timer has been found
            timeConfigList.clear();
            timeConfigList.addAll(newList);
            Collections.sort(timeConfigList);
            return true;
        }
        // otherwise, no change has happened
        return false;
    }
    public boolean deleteAllTimeConfigs()
    {
        if (timeConfigList.isEmpty())
        {
            // already empty, no changes
            return false;
        }
        timeConfigList.clear();
        return true;
    }

    // Edit: one
    public boolean editTimeConfig(TimeConfig targetTimeConfig, int position)
    {
        if (targetTimeConfig != null)
        {
            // check matching time config and position
            if (timeConfigList.get(position).getConfigId().equals(targetTimeConfig.getConfigId()))
            {
                timeConfigList.set(position, targetTimeConfig);
                Collections.sort(timeConfigList);
                return true;
            }
            else
            {
                // they do not match; find if there is a matching one
                int truePosition = findPositionById(targetTimeConfig.getConfigId());
                if (truePosition != -1)
                {
                    // found matching
                    timeConfigList.set(truePosition, targetTimeConfig);
                    Collections.sort(timeConfigList);
                    return true;
                }
            }
        }
        return false;
    }

    public int size()
    {
        return timeConfigList.size();
    }
    public TimeConfig get(int index)
    {
        return timeConfigList.get(index);
    }
    public boolean isEmpty()
    {
        return timeConfigList.isEmpty();
    }
    public ArrayList<TimeConfig> getTimeConfigList()
    {
        return timeConfigList;
    }
}
