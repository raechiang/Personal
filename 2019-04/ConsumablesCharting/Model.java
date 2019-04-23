package chart;

import java.util.TreeSet;

/**
 * This class stores data and provides access to it. It contains an array of
 * purchases, each as a {@link MonthlyExpenditure}. Additionally, it keeps track
 * of the different dates, category names, and subcategory names in TreeSet
 * structures. Data is set with {@link #setData(MonthlyExpenditure[])} and can
 * be retrieved with the respective get functions.
 */
public class Model
{
    /**
     * An array of all the monthly expenses. Accessed via the get method,
     * {@link #getPurchases()}.
     */
    private MonthlyExpenditure[] purchases;
    
    // TreeSets of Strings
    /**
     * A TreeSet of dates of months as Strings, which can be accessed through
     * {@link #getDates()}.
     */
    private TreeSet<String> dates;
    /**
     * A TreeSet of category names, accessible by using {@link #getCats()}.
     */
    private TreeSet<String> cats;
    /**
     * A TreeSet of subcategory names, retrieved with {@link #getSubcats()}.
     */
    private TreeSet<String> subcats;

    /**
     * Constructor instantiates TreeSet data structures.
     */
    public Model()
    {
        // TreeSet was picked because it sorts itself.
        dates = new TreeSet<String>();
        cats = new TreeSet<String>();
        subcats = new TreeSet<String>();
    }

    /**
     * This method populates the {@link #purchases}, {@link #dates},
     * {@link #cats}, and {@link #subcats} fields, given an array of purchases.
     * @param p An array of MonthlyExpenditure purchases
     */
    public void setData(MonthlyExpenditure[] p)
    {
        purchases = new MonthlyExpenditure[p.length];

        for (int i = 0; i < p.length; ++i)
        {
            // just iterates over the array and adds them all
            purchases[i] = p[i];

            dates.add(Integer.toString(purchases[i].getDate()));
            cats.addAll(purchases[i].getMainCategories().keySet());
            subcats.addAll(purchases[i].getSubcategories().keySet());
            System.out.println(purchases[i].toString());
        }
    }

    /**
     * Returns the TreeSet {@link #dates}.
     * @return Months as strings
     */
    public TreeSet<String> getDates()
    {
        return dates;
    }

    /**
     * Returns the TreeSet {@link #cats}.
     * @return Category names
     */
    public TreeSet<String> getCats()
    {
        return cats;
    }

    /**
     * Returns the TreeSet {@link #subcats}.
     * @return Subcategory names
     */
    public TreeSet<String> getSubcats()
    {
        return subcats;
    }

    /**
     * Returns the array of MonthlyExpenditures, {@link #purchases}.
     * @return An array of expenses organized by month
     */
    public MonthlyExpenditure[] getPurchases()
    {
        return purchases;
    }
}
