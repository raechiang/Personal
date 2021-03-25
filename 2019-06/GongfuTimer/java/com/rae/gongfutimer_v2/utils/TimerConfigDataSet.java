package com.rae.gongfutimer_v2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This contains a list of TimerConfigs.
 */
public class TimerConfigDataSet
{
    /**
     * The list of TimerConfigs.
     */
    private ArrayList<TimerConfig> timerConfigList;

    /**
     * The constructor.
     */
    public TimerConfigDataSet()
    {
        timerConfigList = new ArrayList<>();
    }

    /**
     * This adds the elements from a passed timerConfigList to the current data set and then sorts
     * {@link #timerConfigList}.
     * @param timerConfigList - A list to add to the data set.
     * @return - The result from addAll() called on the ArrayList data set: true if list changed.
     */
    public boolean addAll(List<TimerConfig> timerConfigList)
    {
        boolean returnValue = this.timerConfigList.addAll(timerConfigList);
        Collections.sort(this.timerConfigList);
        return returnValue;
    }

    // Add: one, defaults
    /**
     * This adds a single element, the newTimerConfig, to the current data set and then sorts the
     * {@link #timerConfigList}.
     * @param newTimerConfig - The TimerConfig to add to the data set.
     * @return - The result from add() called on the ArrayList data set: true if list changed.
     */
    public boolean addNewTimerConfig(TimerConfig newTimerConfig)
    {
        boolean returnValue = timerConfigList.add(newTimerConfig);
        Collections.sort(timerConfigList);
        return returnValue;
    }
    /**
     * This adds a list of default TimerConfigs to the current data set and sorts them if adding
     * them was successful. It will not add default timer configurations that already exist in the
     * data set.
     * @param defaultTimerConfigs - A list of timer configurations from the default configs xml.
     * @return - The collective result from applicable add() calls: true if list changed.
     */
    public boolean addDefaultTimerConfigs(List<TimerConfig> defaultTimerConfigs)
    {
        boolean returnValue = false;
        // gets default configs already in the data set
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
                    // if it is not in the set already, add it and set true if successful
                    returnValue |= timerConfigList.add(currentDefault);
                }
            }
        }
        if (returnValue)
        {
            // if a change was made to the list, need to sort again
            Collections.sort(timerConfigList);
        }
        return returnValue;
    }
    /**
     * This searches the data set for TimerConfigs marked as default and returns them as a list.
     * @return - A List of TimerConfigs with isDefault field equal to true.
     */
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
    /**
     * This checks if the data set contains a TimerConfig that is similar
     * @param timerConfig - A TimerConfig to compare with those existing in the timerConfigList.
     * @param timerConfigList - A List of TimerConfigs to compare its elements with timerConfig
     * @return - True if there exists a TimerConfig that is considered similar.
     */
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
    /**
     * This will delete the TimerConfig if the target TimerConfig has the same UUID as that of the
     * element at the position of the data set. If the UUIDs don't match, it will search the list
     * for the real position and attempt to delete that one instead.
     * @param position - The position in the list of the target to delete.
     * @param target - The TimerConfig to delete.
     * @return - True if the TimerConfig was successfully deleted.
     */
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
    /**
     * This will delete the TimerConfig at the given position.
     * @param position - The position of the TimerConfig to delete.
     * @return - True if the TimerConfig at the position was deleted.
     */
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
    /**
     * This makes a new List for the data set that has no default timer configurations in it.
     * @return - True if changes occurred to the list.
     */
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
    /**
     * This deletes all of the elements in the data set unless it is already empty.
     * @return - True if changes occurred to the list.
     */
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
    /**
     * This sets the TimerConfig in the data set at the given position to the the given target
     * TimerConfig. It first checks to see if the existing TimerConfig at the position has a
     * matching UUID; if they match, it will simply set it, but if they do not match, then it will
     * look for the TimerConfig with a matching UUID and attempt to edit the TimerConfig there.
     * @param position - The index of the target TimerConfig to edit.
     * @param target - The changed TimerConfig to set at the position in the data set.
     * @return - True if changes were made to the list.
     */
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

    /**
     * This searches the {@link #timerConfigList} for the index of the element with a matching UUID.
     * @param targetId - The UUID to search for.
     * @return - The index of the matching element or -1 if a match was not found.
     */
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

    /**
     * This retrieves the index of a given target TimerConfig.
     * @param target - The TimerConfig to search for.
     * @return - The result of the ArrayList's indexOf().
     */
    public int indexOf(TimerConfig target)
    {
        return timerConfigList.indexOf(target);
    }
    /**
     * This returns the size (number of elements) in {@link #timerConfigList}.
     * @return - Integer of the size of the list of TimerConfigs.
     */
    public int size()
    {
        return timerConfigList.size();
    }
    /**
     * This retrieves the TimerConfig at the given index in {@link #timerConfigList}.
     * @param index - The position of an element to retrieve from the list of TimerConfigs.
     * @return - The TimerConfig at the given index.
     */
    public TimerConfig get(int index)
    {
        return timerConfigList.get(index);
    }
    /**
     * This method indicates whether the list contains any TimerConfigs.
     * @return - True if it is empty, false if it has at least one element.
     */
    public boolean isEmpty()
    {
        return timerConfigList.isEmpty();
    }
    /**
     * This method returns the data set.
     * @return - The ArrayList of TimerConfigs.
     */
    public ArrayList<TimerConfig> getTimerConfigList()
    {
        return timerConfigList;
    }
}
