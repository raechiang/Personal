package chart;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class pulls data from a {@link MonthlyExpenditure} array depending on
 * the provided date and category/subcategory flag.
 * @see DataSelector
 */
public class PieDataSelector implements DataSelector
{
    /**
     * The date of the month to select data for. If this is set to 0, then it
     * will total the values when {@link #getData(MonthlyExpenditure[])} is
     * called.
     */
    private int date;
    
    /**
     * The byte indicates if the user has selected category or subcategory
     * data.
     */
    private byte catFlag;

    /**
     * A constructor that initializes the {@link #catFlag} and {@link #date}.
     * @param flag The value for catFlag that indicates whether to use data
     *             from categories or subcategories.
     * @param date The month.
     */
    public PieDataSelector(byte flag, int date)
    {
        catFlag = flag;
        this.date = date;
    }

    /**
     * This method will generate the data needed for a Pie chart by using the
     * passed in {@link #MonthlyExpenditure} array and the {@link #date} and
     * {@link #catFlag}.
     * @param me An array of monthly expenses.
     * @return A HashMap of elements of category/subcategory name matching with
     *         amount spent. It will return an empty HashMap if the
     *         {@link #date} could not be matched to the provided argument (me).
     */
    @Override
    public HashMap<String, Double> getData(MonthlyExpenditure[] me)
    {
        HashMap<String, Double> dataMap = new HashMap<String, Double>();
        if (date == 0) // overall, collects totals
        {
            if (catFlag == 0)
            {
                // cats of all months 0
                for (int i = 0; i < me.length; ++i)
                {
                    // iterates over all of the months in the array to
                    // accumulate them into one dataMap
                    for (Entry<String, Double> entry 
                            : me[i].getMainCategories().entrySet())
                    {
                        if (dataMap.containsKey(entry.getKey()))
                        {
                            // add to existing
                            double newValue = dataMap.get(entry.getKey())
                                    + entry.getValue();
                            dataMap.replace(entry.getKey(), newValue);
                        } else
                        {
                            // make new
                            dataMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                // returns the dataMap
                return dataMap;
            } else
            {
                // subcats of all months 1
                for (int i = 0; i < me.length; ++i)
                {
                    // iterates over all of the months in the array to
                    // accumulate them into one dataMap
                    for (Entry<String, Double> entry 
                            : me[i].getSubcategories().entrySet())
                    {
                        if (dataMap.containsKey(entry.getKey()))
                        {
                            // add to existing
                            double newValue = dataMap.get(entry.getKey()) 
                                    + entry.getValue();
                            dataMap.replace(entry.getKey(), newValue);
                        } else
                        {
                            // make new
                            dataMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                // returns the dataMap
                return dataMap;
            }
        } else // by the month
        {
            // gets the index that corresponds with the date provided
            int dateIndex = getDateIndex(me);
            if (dateIndex != -1) // if matching index has been found
            {
                if (catFlag == 0)
                {
                    // cats of the month
                    return generateMap(
                            me[dateIndex].getMainCategories().entrySet());
                } else
                {
                    // subcats of the month
                    return generateMap(
                            me[dateIndex].getSubcategories().entrySet());
                }
            }
        }
        // if it reaches here then it will return an empty dataMap
        return dataMap;
    }
    
    /**
     * It basically adds all the values from the passed Set to a new HashMap.
     * @param entrySet A set of entries. In this case, a set of entries that
     *                 would contain either main categories or subcategories, as
     *                 well as its associated expenditure, of a specific month.
     * @return A HashMap of names and amounts spent.
     */
    private HashMap<String, Double> generateMap(
            Set<Entry<String, Double>> entrySet)
    {
        HashMap<String, Double> dataMap = new HashMap<String, Double>();
        
        for (Entry<String, Double> e : entrySet)
        {
            if ( !(dataMap.containsKey(e.getKey())) )
            {
                // make new
                dataMap.put(e.getKey(), e.getValue());
            } else
            {
                // add to existing
                // Given how pairs are added to MonthlyExpenditure, it wouldn't
                // actually need to do this for any single month, since
                // MonthlyExpenditure should contain unique keys
                double newValue = dataMap.get(e.getKey()) + e.getValue();
                dataMap.replace(e.getKey(), newValue);
            }
        }
        
        return dataMap;
    }

    /**
     * This method goes through all the months in the array to find one that
     * matches the desired {@link #date}.
     * @param me An array of monthly expenses.
     * @return The index of the month with a matching date, or -1 if none could
     *         be found.
     */
    private int getDateIndex(MonthlyExpenditure[] me)
    {
        for (int i = 0; i < me.length; ++i)
        {
            if (me[i].getDate() == date)
            {
                return i;
            }
        }
        // shouldn't happen because the radio buttons for the Pie chart should
        // be generated by using the same "me" data
        return -1;
    }
}
