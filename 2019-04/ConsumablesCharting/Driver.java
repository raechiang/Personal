/*
 * Consumables Charting [2019.04]
 * --------------------------------------------------------------------------------
 * This program can generate pie or line charts.
 * --------------------------------------------------------------------------------
 */
package chart;
/**
 * Given a file populated with data in a specific format, this program generates
 * some pie or line charts. Pie charts are selectable by month (or by one big
 * total), and line charts will display user-selected lines. Both can display
 * either the general consumables categories or the subcategories. To start, the
 * user must provide a file directory, which contains data entries separated by
 * new lines, in a format like so: date;category_name;amount
 * <ul>
 * <li>date: in the format of yyyymm</li>
 * <li>category name: any kind of single-word string, but subcategories require
 *     the prefix of "g_" to be saved as a subcategory</li>
 * <li>amount: some non-negative number with no more than two decimal places but
 *     no less than one if a decimal point is present</li>
 * </ul>
 * Examples:
 * <blockquote>
 *     201806;g_produce;147.88<p>
 *     201812;tax;28.0<p>
 *     201804;water;14
 * </blockquote>
 * 
 * After loading and scanning the file, a pie chart containing the totals for
 * the primary categories will be displayed by default. Two dropdown boxes on
 * the right allow switching of the chart type (between pie and line) and
 * category type (between the primary "categories" and secondary "sub-
 * categories"). Further options are displayed below them and may change depen-
 * ding on the selections in the dropdown boxes. Pie charts will always display
 * radio buttons with the dates, whereas the line chart will display checkboxes
 * of either the categories or subcategories. There are four major permutations:
 * <ol>
 *     <li>pie chart with categories according to a date,</li>
 *     <li>pie chart with subcategories according to a date,</li>
 *     <li>line chart with lines picked from categories over all time, and</li>
 *     <li>line chart with lines picked from subcategories over all time.</li>
 * </ol>
 * The user must click the "Chart It" button to generate a new chart.
 * <p>Although it was originally intended for general consumables with the
 * subdivision of groceries, it could probably be extended for anything and one
 * subcategory type.
 */
public class Driver
{
    /**
     * Main method
     * @param args - not used
     */
    public static void main(String[] args)
    {
        /*
         * Attempted to use MVP design pattern to organize code
         */
        Model model = new Model();
        View view = new GUI();
        Presenter presenter = new Presenter(model, view);
        presenter.start();
    }
}
