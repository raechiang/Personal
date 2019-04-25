package chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class controls the View and relays relevant information from the Model
 * to the View. To begin, run {@link #start()}. The rest of the methods run
 * based on Actions taken by the user through the GUI.
 */
public class Presenter
{
    /**
     * This contains all of the monthly expense data.
     */
    private Model model;
    /**
     * This controls only the GUI display.
     */
    private View view;

    /**
     * The Constructor instantiates the {@link #model} and {@link #view} fields.
     * @param model The Model, which contains the data.
     * @param view The View, which regards the GUI.
     */
    public Presenter(Model model, View view)
    {
        this.model = model;
        this.view = view;
    }
    
    /**
     * This method basically sets up the application for the user.
     */
    public void start()
    {
        // Setup
        view.setUp(); // frame and file input request display
        view.setMessage("(Messages will be displayed here.)");
        addActionListenersInit(); // makes ActionListeners for the components
    }
    
    /**
     * This method will add Action Listeners to the file input text field and
     * to the load button. They use the same ActionListener because they
     * trigger the same effect: they simply try to open the file given in the
     * file input text field.
     */
    private void addActionListenersInit()
    {
        view.addFileInputActionListener(generateOpenActionListener());
        view.addLoadButtonActionListener(generateOpenActionListener());
    }
    
    /**
     * This is called by {@link #generateOpenActionListener()}. If the user has
     * provided a proper filepath, it will begin the "second" phase, during
     * which the user may generate different charts. As default, a Pie chart
     * view is generated.
     */
    private void startChartPhase()
    {
        // Default view is the pie/month type
        // set up the pie/line and cat/subcat dropdown boxes
        view.setDropdowns();
        // make radio buttons using the dates
        view.setRadioButtons(model.getDates());
        // makes a "chart" ("go") button
        view.setChartButton();
        // adds action listeners for the new components
        addChartPhaseActionListeners();
        // set up options panel to reflect the default chart
        view.updateOptionsPanel("Pie Chart", "Categories");
        // set up the default chart
        byte temp = 0;
        String title = "Pie Chart by Category for All Dates (Default)";
        view.updateLeftPanelToChart(model.getPurchases(),
                temp,
                0,
                title);
    }
    
    /**
     * This is called by {@link #startChartPhase()}. It adds action listeners
     * to the two dropdown boxes and the chart (go) button.
     */
    private void addChartPhaseActionListeners()
    {
        // Action Listener added to the pie/line dropdown box
        view.addChartTypeActionListener(generateDropActionListener());
        // Action Listener added to the category/subcategory dropdown box
        view.addCatSubcatActionListener(generateDropActionListener());
        // Action Listener added to the chart (go) button
        view.addChartButtonActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        // If the chart button is clicked, it will change the
                        // chart that is displayed depending on the chart type
                        // selection, the category/subcategory selection, and
                        // the specific cats/subcats selected or the date.
                        String plSelection = view.getChartTypeSelection();
                        String csSelection = view.getCatSubcatSelection();
                        String title = generateChartTitle(
                                getCSCode(csSelection),
                                plSelection);
                        if (plSelection.equals("Pie Chart"))
                        {
                            // Pie chart wanted
                            // set a message for the user
                            view.setMessage(title + " requested.");
                            // set the chart panel,
                            // using data, flag, date, title
                            view.setChart(model.getPurchases(),
                                    getCSCode(csSelection),
                                    Integer.parseInt(view.getRadioSelection()),
                                    title);
                        }
                        else
                        {
                            // Line chart wanted
                            // need the specific cats/subcats
                            String spec = generateLineSpecString(
                                    getCSCode(csSelection),
                                    view.getCheckSelections());
                            // set a message for the user
                            view.setMessage(title + " requested.");
                            // set the chart panel,
                            // using data, flag, line specifications, title
                            view.setChart(model.getPurchases(),
                                    getCSCode(csSelection),
                                    spec,
                                    title);
                        }
                    }
                });
    }
    
    /**
     * This method makes an Action Listener for the file opening components. It
     * checks file validity and will either open the file if it's valid or set
     * an error message. If a valid file has been found, then the
     * {@link #model} will be able to save the data from it and the "second"
     * phase, the chart phase, can begin.
     * @return A general Action Listener for file opening components.
     */
    private ActionListener generateOpenActionListener()
    {
        return (new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String fileDirectory = view.getFileInputFieldText();
                        if (Parser.foundValidFile(fileDirectory))
                        {
                            view.setMessage("Load " + fileDirectory);
                            model.setData(Parser.getCostList(fileDirectory));
                            startChartPhase();
                        }
                        else
                        {
                            view.setMessage(
                                    "Provided file directory did not match."
                                    + " Please try again.");
                        }
                    }
                }
        );
    }
    
    /**
     * This method makes an Action Listener for the dropdown boxes. There are
     * two dropdown boxes: one that chooses between Pie Chart and Line Chart and
     * one that chooses between Categories or Subcategories. If Pie is selected,
     * it must display radio buttons, so the options panel must be updated. If
     * Line is selected, it must display checkboxes, so the options panel must
     * be updated. Selection between Categories or Subcategories would only
     * change the options panel if the Line chart was picked.
     * @return A general Action Listener for dropdown boxes.
     */
    private ActionListener generateDropActionListener()
    {
        return (new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        // tells user that an action has been detected
                        view.setMessage("Set " + view.getChartTypeSelection()
                                         + "/" + view.getCatSubcatSelection());
                        if (view.getChartTypeSelection().equals("Line Chart"))
                        {
                            // sets up the right checkboxes if Line is selected
                            setLineCheckOptions(view.getCatSubcatSelection());
                        }
                        // update the options panel
                        view.updateOptionsPanel(view.getChartTypeSelection(),
                                view.getCatSubcatSelection());
                    }
                }
        );
    }
    
    /**
     * This sets the checkboxes to display the proper categories or
     * subcategories, depending on the type selected in the relevant dropdown
     * box. This is called if the chart type is set to Line, in
     * {@link #generateDropActionListener()}.
     * @param catType The String stating "Categories" or "Subcategories"
     */
    private void setLineCheckOptions(String catType)
    {
        // line only...
        if (catType.equals("Categories"))
        {
            view.setCheckboxes(model.getCats());
        }
        else
        {
            view.setCheckboxes(model.getSubcats());
        }
    }
    
    /**
     * This simply makes a byte depending on the selection of the cat/subcat
     * dropbox.
     * @param selection A String that is "Categories" or "Subcategories".
     * @return A byte of 0 if it is "Categories" or 1 if it is "Subcategories".
     */
    private byte getCSCode(String selection)
    {
        if (selection.equals("Categories"))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
    /**
     * This method makes a large portion of the title for a chart.
     * @param catFlag A byte of 0 if it is a chart of categories, or 1 if it is
     *                of subcategories.
     * @param chartType A String of the chart type.
     * @return A title for the chart.
     */
    private String generateChartTitle(byte catFlag, String chartType)
    {
        String title = chartType;
        // Format is " by <Category/Subcategory> for "
        if (catFlag == 0)
        {
            title += " by Category";
        }
        else
        {
            title += " by Subcategory";
        }
        
        // Either a list of names or a date
        title += " for ";
        
        if (chartType.equals("Pie Chart"))
        {
            // date
            if (view.getRadioSelection().equals("0"))
            {
                title += "All Dates";
            }
            else
            {
                title += view.getRadioSelection();
            }
        }
        else
        {
            // name(s)
            ArrayList<String> selections = view.getCheckSelections();
            if (selections.isEmpty())
            {
                if (catFlag == 0)
                {
                    title += "All Categories";
                }
                else
                {
                    title += "All Subcategories";
                }
            }
            else
            {
                if (catFlag == 0)
                {
                    // by cats
                    if (selections.size() == model.getCats().size())
                    {
                        title += "All Categories";
                    }
                    else
                    {
                        // make string
                        title += generateLineTitlePart(selections);
                    }
                }
                else
                {
                    // by subcats
                    if (selections.size() == model.getSubcats().size())
                    {
                        title += "All Subcategories";
                    }
                    else
                    {
                        // make string
                        title += generateLineTitlePart(selections);
                    }
                }
            }
        }
        
        return title;
    }
    
    /**
     * This method writes the names in a format closer to how it would be listed
     * in regular English.
     * @param names An ArrayList of Strings corresponding to category or
     *              subcategory names.
     * @return A String that is meant to be used for the title of a Line chart.
     */
    private String generateLineTitlePart(ArrayList<String> names)
    {
        String piece = "";
        
        if (names.size() == 2)
        {
            piece += names.get(0) + " ";
        }
        else
        {
            for (int i = 0; i < names.size() - 1; ++i) // to last
            {
                piece += names.get(i) + ", ";
            }
        }
        
        if (names.size() > 1)
        {
            piece += "and ";
        }
        
        piece += names.get(names.size() - 1);
        
        return piece;
    }
    
    /**
     * This method picks up the data from the View's checkboxes and generates a
     * single String of the selections, separated by semicolons. This single
     * line can be passed to the {@link LineDataSelector}. Notably, if an empty
     * list of selections was passed in, it will automatically populate the
     * selections to simply contain all of them, respective to the category type
     * chosen. This is used when a Line chart has been requested.
     * @param catFlag Indicates the type between category or subcategory.
     * @param selections An array list corresponding to the selections of the
     *                   checkboxes.
     * @return A single String describing all the selections.
     */
    private String generateLineSpecString(
            byte catFlag,
            ArrayList<String> selections)
    {
        String spec = "";
        if (selections.isEmpty())
        {
            view.setMessage("No selections. By default, all are displayed.");
            if (catFlag == 0)
            {
                // cats
                Iterator<String> it = model.getCats().iterator();
                while (it.hasNext())
                {
                    selections.add(it.next());
                }
            }
            else
            {
                // subcats
                Iterator<String> it = model.getSubcats().iterator();
                while (it.hasNext())
                {
                    selections.add(it.next());
                }
            }
        }
        
        for (int i = 0; i < selections.size(); ++i)
        {
            // in retrospect, could've passed a List all the way through! ...
            spec += selections.get(i) + ";";
        }
        
        return spec;
    }
}
