package chart;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Classes that implement this interface should handle all of the visual stuff
 * for the application window and try to be lightweight.
 * 
 * @see GUI
 */
public interface View
{
    /**
     * This should be the first or an early method to call. It should construct
     * the layout and also make the frame itself visible.
     */
    public void setUp();
    // Setting panels/groups of panels
    // [L] chart itself
    /**
     * This method should generate a Pie chart and display it.
     * @param me An array of monthly expenses.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param date The date of the month to get data for.
     * @param title The title of the chart.
     */
    public void setChart(MonthlyExpenditure[] me, byte catFlag, int date, String title);
    /**
     * This method should generate a Line chart and display it.
     * @param me An array of monthly expenses.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param specThe specific categories/subcategories to use.
     * @param title The title of the chart.
     */
    public void setChart(MonthlyExpenditure[] me, byte catFlag, String spec, String title); // Line
    // [R] drop down boxes
    /**
     * This should make the dropdown boxes for choosing between pie or line
     * charts and between categories or subcategories.
     */
    public void setDropdowns();
    // [R] checkboxes
    /**
     * This method should populate the checkboxes with relevant names for the
     * Line chart.
     * @param fields - A TreeSet of Strings with category/subcategory names.
     */
    public void setCheckboxes(TreeSet<String> fields);
    // [R] radio buttons
    /**
     * This method should set up the radio buttons with dates.
     * @param fields - A TreeSet of Strings that represent dates.
     */
    public void setRadioButtons(TreeSet<String> fields);
    // [R] chart button
    /**
     * This should set up the "Chart It" or "Go" button.
     */
    public void setChartButton();
    // [B] message label
    /**
     * This should set up a message label which delivers notifications of action
     * responses to the user.
     * @param s The String to set the text of the label to.
     */
    public void setMessage(String s);

    // Adding Action Listeners
    // opening stuff
    /**
     * This should add an ActionListener to a file input text field.
     * @param listener The ActionListener to add.
     */
    public void addFileInputActionListener(ActionListener listener);
    /**
     * This should add an ActionListener to the file load button.
     * @param listener The ActionListener to add.
     */
    public void addLoadButtonActionListener(ActionListener listener);
    // drop down boxes
    /**
     * This should add an ActionListener to the dropdown combo box that selects
     * between pie or line chart.
     * @param listener The ActionListener to add.
     */
    public void addChartTypeActionListener(ActionListener listener);
    /**
     * This should add an ActionListener to the dropdown combo box that selects
     * between categories or subcategories.
     * @param listener
     */
    public void addCatSubcatActionListener(ActionListener listener);
    // chart button
    /**
     * This should add an ActionListener to the chart/go button.
     * @param listener
     */
    public void addChartButtonActionListener(ActionListener listener);
    
    /**
     * This should update the left panel from a file input display to a chart
     * display.
     * @param me The data of the monthly expenses.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param date The date to use.
     * @param title The title for the chart.
     */
    public void updateLeftPanelToChart(MonthlyExpenditure[] me, byte catFlag, int date, String title);
    /**
     * This should change the display between radio buttons and checkboxes and
     * set up the panels properly.
     * @param chartType The type of chart (pie/line).
     * @param catType The type of categories (categories/subcategories).
     */
    public void updateOptionsPanel(String chartType, String catType);
    
    // Getters
    /**
     * This must return the user's inputed filepath of the data.
     * @return A String representing the filepath.
     */
    public String getFileInputFieldText();
    /**
     * This must return the type of chart that is selected by the dropdown
     * combo box.
     * @return A String of the chart type selection.
     */
    public String getChartTypeSelection();
    /**
     * This must return the type of category that is selected by the dropdown
     * combo box.
     * @return A String of the category type selection.
     */
    public String getCatSubcatSelection();
    /**
     * This must return the selection from the radio buttons.
     * @return A String of the selected date.
     */
    public String getRadioSelection();
    /**
     * This must return the selected checkboxes.
     * @return A List of Strings of each of the slected checkboxes.
     */
    public ArrayList<String> getCheckSelections();
}
