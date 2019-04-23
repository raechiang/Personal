package chart;

import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;
import java.util.HashMap;

/**
 * This class grabs the desired information from a {@link MonthlyExpenditure}
 * array depending on the user-selected categories/subcategories and a flag.
 * It is specifically designed for {@link LineMaker}.
 * 
 * @see DataSelector
 * @see LineMaker
 */
public class LineDataSelector implements DataSelector
{
    /**
     * This field contains a list of inputs corresponding to names of
     * categories/subcategories.
     */
    private LinkedList<String> inputs;
    
    /**
     * This field indicates whether the data requested involves the categories
     * or subcategories from {@link MonthlyExpenditure}. 
     */
    private byte catFlag;

    /**
     * This instantiates the two fields {@link #inputs} and {@link #catFlag}.
     * @param flag This is a byte value where 0=category and 1=subcategory
     * @param spec This is a list of the selected category/subcategory names.
     *             The string must be formatted as "x;y;z", where individuals
     *             are divided by semicolons. This allows proper use of
     *             {@link #parseInputs(String)}.
     */
    public LineDataSelector(byte flag, String spec)
    {
        catFlag = flag;
        inputs = parseInputs(spec);
    }

    /**
     * This will construct a HashMap that is unique for making a Line chart.
     * Each entry in the map contains a String of the date (month) and the
     * corresponding amount for said month. The external use for this is
     * somewhat convoluted compared to that of {@link PieDataSelector}. It
     * would be more straightforward to use a unique data structure for making
     * the Line Graphs, and I should have put more effort into making the chart
     * generators do less work.
     * @param me The data to pull information from.
     */
    @Override
    public HashMap<String, Double> getData(MonthlyExpenditure[] me)
    {
        // (date,amount)
        HashMap<String, Double> dataMap = new HashMap<String, Double>();

        if (inputs.isEmpty())
        {
            // Actually it will not ever be empty in my case because it will
            // default to selecting all of the checkboxes, but I'll leave that
            // there just in case
            System.out.println("LDS: How did that happen?");
            return dataMap;
        }
        else
        {
            // picks next input, a name of a cat/groc
            String input = inputs.pop();

            // It might look prettier to switch it to for{if-else}?
            if (catFlag == 0)
            {
                // wants cats
                if (input.equals("total"))
                {
                    // needs to total the amounts
                    for (int i = 0; i < me.length; ++i)
                    {
                        // go through me's
                        dataMap.put(Integer.toString(me[i].getDate()),
                                findTotal(
                                        me[i].getMainCategories().entrySet()));
                    }
                } else
                {
                    // should be regular name
                    for (int i = 0; i < me.length; ++i)
                    {
                        // goes through me's and grabs entries that correspond
                        // to input (the name)
                        if (me[i].getMainCategories().containsKey(input))
                        {
                            // <dateString,amountSpent>
                            dataMap.put(
                                    Integer.toString(me[i].getDate()),
                                    me[i].getMainCategories().get(input)
                                    );
                        }
                    }
                }
            } else
            {
                // wants subcats
                if (input.equals("total"))
                {
                    // needs to total the amounts
                    for (int i = 0; i < me.length; ++i)
                    {
                        dataMap.put(Integer.toString(me[i].getDate()),
                                findTotal(
                                        me[i].getSubcategories().entrySet()));
                    }
                } else
                {
                    // should be regular name
                    for (int i = 0; i < me.length; ++i)
                    {
                        // goes through me's and grabs entries that correspond
                        // to input (the name)
                        if (me[i].getSubcategories().containsKey(input))
                        {
                            // <dateString,amountSpent>
                            dataMap.put(
                                    Integer.toString(me[i].getDate()),
                                    me[i].getSubcategories().get(input)
                                    );
                        }
                    }
                }
            }
        }

        return dataMap;
    }

    /**
     * This parses the input from the constructor's parameter spec.
     * @param catSpec This is a list of the selected category/subcategory names
     *                formatted as a string in the form of "x;y;z", where
     *                individuals are divided by semicolons.
     * @return A LinkedList of the specified category/subcategory names.
     */
    private LinkedList<String> parseInputs(String catSpec)
    {
        LinkedList<String> outputs = new LinkedList<String>();

        String spec = catSpec;

        while (spec.length() > 1)
        {
            // names divided by ;
            int nextBreak = spec.indexOf(';');
            outputs.add(spec.substring(0, nextBreak));
            spec = spec.substring(nextBreak + 1);
        }

        return outputs;
    }

    /**
     * A getter for the first element in the {@link #inputs} list.
     * @return A String corresponding to a category/subcategory name.
     */
    public String getNextInput()
    {
        return inputs.get(0);
    }

    /**
     * A getter for whether the {@link #inputs} list is empty.
     * @return A boolean of whether the inputs list is empty or not.
     */
    public boolean isInputsEmpty()
    {
        return inputs.isEmpty();
    }

    /**
     * Given a set with keys of String names and values of double expenses, this
     * method will sum the total expenses.
     * @param entrySet The set of entries to find the total over.
     * @return A total of expenses.
     */
    private double findTotal(Set<Entry<String, Double>> entrySet)
    {
        double total = 0;
        for (Entry<String, Double> e : entrySet)
        {
            total += e.getValue();
        }
        return total;
    }
}
