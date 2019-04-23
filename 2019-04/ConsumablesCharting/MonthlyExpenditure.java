package chart;

import java.util.HashMap;

/**
 * This class functions as a container for a single month and its categories,
 * subcategories, and associated expenses for each. Dates are stored as integer
 * values, preferably in the structure of "yyyymm". Category and subcategory
 * names are stored as strings, and the purchases within each are stored as
 * doubles.
 */
public class MonthlyExpenditure
{
    /**
     * The date, which should be a year-month value, in the form of yyyymm. It
     * can be accessed by using {@link #getDate()}.
     */
    // there are probably better choices for this, maybe its own class
    private int date;
    
    /**
     * A HashMap containing all the category names and their respective price
     * values. Adding to it uses {@link #addCat(String, double)} and retrieval
     * is done with {@link #getMainCategories()}. This accepts any String.
     */
    private HashMap<String, Double> mainCategories;
    
    /**
     * A HashMap containing all the subcategory names and their corresponding
     * spending values. Adding to it uses {@link #addCat(String, double)} and
     * retrieval is done with {@link #getMainCategories()}. In order to indicate
     * that a category should be a subcategory, the name <i>must</i> be prefixed
     * with "g_".
     */
    private HashMap<String, Double> subcategories;

    /**
     * To create a MonthlyExpenditure, a date must be provided, which will be
     * saved into {@link #date}. This constructor will also make empty HashMaps
     * for {@link #mainCategories} and {@link #subcategories}.
     * @param date An integer value, preferably in the form yyyymm.
     */
    public MonthlyExpenditure(int date)
    {
        this.date = date;
        mainCategories = new HashMap<String, Double>();
        subcategories = new HashMap<String, Double>();
    }

    /**
     * This method will add one category or subcategory to one of the two
     * fields: {@link #mainCategories} or {@link #subcategories}. The presence
     * of a prefix "g_" is required to indicate that a category is to be saved
     * into the {@link #subcategories}. The amount spent in a given category
     * or subcategory is also saved with the name.
     * @param id The name to use for the category or subcategory.
     * @param amount The monetary value associated with the expense of the
     *               provided category or subcategory.
     */
    public void addCat(String id, double amount)
    {
        if (id.contains("g_"))
        {
            // removes the g_ in the name
            String realID = id.substring(2);
            // it is a grocery subcategory
            if (subcategories.containsKey(realID))
            {
                // add to existing value
                subcategories.replace(realID, subcategories.get(realID) + amount);
            } else
            {
                // new value
                subcategories.put(realID, amount);
            }
        } else
        {
            // regular category
            if (mainCategories.containsKey(id))
            {
                // add to existing value
                mainCategories.replace(id, mainCategories.get(id) + amount);
            } else
            {
                // new value
                mainCategories.put(id, amount);
            }
        }
    }

    /**
     * Gets the integer {@link #date}.
     * @return The month
     */
    public int getDate()
    {
        return date;
    }

    /**
     * Gets the HashMap {@link #mainCategories}, whose elements are a pair with
     * the name and associated spending amount.
     * @return The main categories and their expenses.
     */
    public HashMap<String, Double> getMainCategories()
    {
        return mainCategories;
    }

    /**
     * Gets the HashMap {@link #subcategories}, whose elements are a pair with
     * the name and associated spending amount.
     * @return The subcategories and their expenses.
     */
    public HashMap<String, Double> getSubcategories()
    {
        return subcategories;
    }

    /**
     * Generates an easily readable string based on the data it contains.
     */
    public String toString()
    {
        return ("Date: " + date
                + "\nCategories: " + mainCategories.toString()
                + "\nSubcategories: " + subcategories.toString());
    }
}