package chart;

import java.util.HashMap;

/**
 * This is a simple interface that ensures that the data selectors return a
 * HashMap structure when given the monthly expenditure information.
 * 
 * @see LineDataSelector
 * @see PieDataSelector
 */
public interface DataSelector
{
    /**
     * The string should correspond to the organization parameter name for the
     * specific type of chart selector. For Line charts, this should be the date
     * string, whereas for Pie charts, this should be the category/subcategory
     * name string.
     * @param me An array of {@link MonthlyExpenditure} objects, which contain
     *           a date and the spendings associated with specific categories
     *           and subcategories.
     * @return A HashMap of the desired information when certain parameters are
     *         set, which can differ between data selector classes. 
     */
    public HashMap<String, Double> getData(MonthlyExpenditure[] me);
}
