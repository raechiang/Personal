package chart;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * This class generates a Pie chart contained in a JPanel by receiving
 * MonthlyExpenditure data, a category/subcategory flag, a date corresponding to
 * a specific month (or 0 if it wants all months), and a title.
 */
public class PieMaker
{
    /**
     * Constructor.
     */
    public PieMaker() {}

    /**
     * This method will generate a Pie chart placed in a JPanel.
     * @param me An array of MonthlyExpenditure data to pull information from.
     * @param catFlag A byte that determines whether to use categories or
     *                subcategories.
     * @param date An integer corresponding with a date from the array of
     *             monthly expenses, or 0 if the user wants totals from all the
     *             months.
     * @param title The title of the chart.
     * @return A JPanel of a chart. An empty chart will be generated if there is
     *         no data given.
     */
    public static JPanel createPanel(
            MonthlyExpenditure[] me,
            byte catFlag,
            int date,
            String title)
    {
        JFreeChart chart = createChart(createDataset(me, catFlag, date), title);
        return new ChartPanel(chart);
    }

    /**
     * This method will generate a Pie Chart.
     * @param dataset The exact data to be used in the chart.
     * @param title The title of the chart.
     * @return A Pie chart.
     */
    private static JFreeChart createChart(PieDataset dataset, String title)
    {
        JFreeChart chart = ChartFactory.createPieChart(title, // chart title
                dataset, // data
                true, // include legend
                true, // tooltips
                false); // URL
        return chart;
    }

    /**
     * This will generate a dataset that is compatible with the Pie chart.
     * @param me An array of data.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param date The date of the month to select for, or 0 if the user wants
     *             totals from all the months.
     * @return A dataset for the Pie chart.
     */
    private static PieDataset createDataset(
            MonthlyExpenditure[] me,
            byte catFlag,
            int date)
    {
        /*
         * I think the generation of a HashMap of the desired data could have
         * been done outside of this class, so as to minimize the interaction
         * between this UI element and the data from Model. I mean that some of
         * this could have (or maybe should have) been done in Presenter, and the
         * only arguments used in this entire class should have been a HashMap
         * and a title. (This idea applies to LineMaker too)
         */
        DefaultPieDataset dataset = new DefaultPieDataset();

        DataSelector pds = new PieDataSelector(catFlag, date);
        HashMap<String, Double> data = pds.getData(me);
        if (!(data.isEmpty()))
        {
            // pretty simply copies keys and values over
            for (Entry<String, Double> entry : data.entrySet())
            {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        return dataset;
    }
}
