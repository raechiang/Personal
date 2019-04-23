package chart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * This class generates a Line chart contained in a JPanel by receiving
 * MonthlyExpenditure data, a category/subcategory flag, a list of names
 * corresponding to the cat/subcats, and a title.
 */
public class LineMaker
{
    /**
     * Constructor.
     */
    public LineMaker() {}

    /**
     * This method will generate a Line chart placed in a JPanel.
     * @param me An array of MonthlyExpenditure data to pull information from.
     * @param catFlag A byte that determines whether to use categories or
     *                subcategories.
     * @param spec A list of names corresponding to category/subcategory names,
     *             formatted as a String with the structure of "x;y;z", where
     *             individuals are divided by semicolons.
     * @param title The title of the chart.
     * @return A JPanel of a chart.
     */
    public static JPanel createPanel(
            MonthlyExpenditure[] me,
            byte catFlag,
            String spec,
            String title)
    {
        JFreeChart chart = createChart(createDataset(me, catFlag, spec), title);
        return new ChartPanel(chart);
    }

    /**
     * This method will generate a Line chart.
     * @param dataset The exact data to be used in the chart.
     * @param title The title of the chart.
     * @return A Line chart.
     */
    private static JFreeChart createChart(
            DefaultCategoryDataset dataset,
            String title)
    {
        JFreeChart chart = ChartFactory.createLineChart(title,
                "Date", "Expenses", // Axis labels
                dataset,
                PlotOrientation.VERTICAL,
                true, // legend
                true, // tool tip
                false); // URL
        return chart;
    }

    /**
     * This will generate a dataset that is compatible with the Line chart.
     * @param me An array of data.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param spec A list of names corresponding to category/subcategory names,
     *             formatted as a String with the structure of "x;y;z", where
     *             individuals are divided by semicolons.
     * @return A dataset for the Line chart.
     */
    private static DefaultCategoryDataset createDataset(
            MonthlyExpenditure[] me,
            byte catFlag,
            String spec)
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LineDataSelector lds = new LineDataSelector(catFlag, spec);

        // Generates chart data line by line
        while (!(lds.isInputsEmpty()))
        {
            // the name of the line is a category/subcategory
            String lineName = lds.getNextInput();
            // generates a HashMap with entries <dateString,amountSpent>
            // for the current line
            HashMap<String, Double> dataMap = lds.getData(me);

            if (!(dataMap.isEmpty()))
            {
                // keySet has all of the date strings
                TreeSet<String> keySet = new TreeSet<String>(dataMap.keySet());
                Iterator<String> it = keySet.iterator();
                while (it.hasNext())
                {
                    // iterating over all of the dates
                    String nextDate = it.next();
                    // adds a value to the dataset 
                    dataset.addValue(
                            dataMap.get(nextDate), // amount spent in the month
                            lineName, // name of the line
                            nextDate); // the month
                }
            }
        }

        return dataset;
    }
}
