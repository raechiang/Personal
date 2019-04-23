package chart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class contains methods that check to see if a file exists and is valid
 * and that can parse the data into an array of {@link MonthlyExpenditure}
 * objects.
 */
public class Parser
{
    /**
     * Constructor.
     */
    public Parser() {}

    /**
     * This method checks if the file or file directory provided exists and
     * contains at least one line that matches the string format of
     * date;identifier;cost.
     * @param name The file or file directory.
     * @return True if a proper file has been found, false if not.
     */
    public static boolean foundValidFile(String name)
    {
        File filepath = new File(name);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();
            while (line != null)
            {
                if (isProperString(line))
                {
                    // ensures at least one line of valid-looking entry
                    return true;
                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return false;
    }

    /**
     * This method makes sure that the String provided matches the format of
     * date;identifier;cost. To be exact,
     * <ul>
     * <li><b>date</b>: in a yyyymm format, or at least it requires 6 digits
     *     </li>
     * <li><b>identifier</b>: any word works</li>
     * <li><b>cost</b>: a number value - note that if there is a decimal, then
     *     it must be followed by one or two digits</li>
     * </ul>
     * @param s A data entry line with the format date;identifier;cost
     * @return True if it is a proper expression, false if it is not.
     */
    private static boolean isProperString(String s)
    {
        return s.matches("\\d{6};\\w+;\\d*.\\d{1,2}")
                || s.matches("\\d{6};\\w+;\\d+");
    }

    /**
     * This method collects the monthly expenses into an array, given a file or
     * file path.
     * @param name The file/file path.
     * @return An array of monthly expenses
     */
    public static MonthlyExpenditure[] getCostList(String name)
    {
        HashMap<Integer, MonthlyExpenditure> map = new 
                HashMap<Integer, MonthlyExpenditure>();

        if (foundValidFile(name))
        {
            File filepath = new File(name);
            try
            {
                BufferedReader br = new BufferedReader(
                        new FileReader(filepath));
                String line = br.readLine();
                while (line != null)
                {
                    if (isProperString(line))
                    {
                        // line is split into date;identifier;price
                        // parse the info from each
                        int yearMonth = parseDate(line);
                        String catID = parseID(line);
                        double amount = parseAmount(line);
                        
                        if (map.containsKey(yearMonth))
                        {
                            // month exists
                            // add category
                            map.get(yearMonth).addCat(catID, amount);
                        } else
                        {
                            // month doesn't exist
                            // add new month and category
                            MonthlyExpenditure newME = new
                                    MonthlyExpenditure(yearMonth);
                            newME.addCat(catID, amount);
                            map.put(yearMonth, newME);
                        }

                    }
                    line = br.readLine();
                }
                br.close();
            } catch (FileNotFoundException fnfe)
            {
                fnfe.printStackTrace();
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        MonthlyExpenditure[] costs = new MonthlyExpenditure[map.size()];
        map.values().toArray(costs);

        return costs;
    }

    /**
     * This extracts the six-digit date from "date;identifier;cost".
     * @param line The line to parse.
     * @return The first part of the line, the date.
     */
    private static int parseDate(String line)
    {
        return (Integer.parseInt(line.substring(0, line.indexOf(';'))));
    }

    /**
     * This extracts the identifier (category or subcategory name) from
     * "date;identifier;cost".
     * @param line The line to parse.
     * @return The second part of the line, the name.
     */
    private static String parseID(String line)
    {
        return (line.substring(line.indexOf(';') + 1, line.lastIndexOf(';')));
    }

    /**
     * This extracts the amount spent from "date;identifier;cost".
     * @param line The line to parse.
     * @return The third part of the line, the amount spent.
     */
    private static double parseAmount(String line)
    {
        return (Double.parseDouble(line.substring(line.lastIndexOf(';') + 1)));
    }
}
