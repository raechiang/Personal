package chart;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * This class handles all the visual stuff for the application window and tries
 * really hard to be sort of lightweight in a way.
 * 
 * <p>Simply put, it is contains the following:
 * <ul>
 *     <li>swing fields
 *     <li>set methods
 *     <li>add methods
 *     <li>update methods
 *     <li>get methods
 * </ul>
 * 
 * To use it, {@link #setUp()} should be called first or at least earliest.
 * In general, set methods are called before add methods. Add methods will
 * add ActionListeners to the swing components that need them. Update methods
 * will change what information is displayed and are meant to be triggered by
 * user actions. Get methods will retrieve necessary user inputs.
 * <p>
 * The window can be divided into two general panels, a left-hand panel and a
 * right-hand panel. The left-hand panel will start with components related to
 * file input, whereas the right will be empty at first. Once a file has been
 * processed, the left panel will switch its display to a Pie chart that
 * displays a chart with total amounts over all time for categories, as seen in
 * {@link #updateLeftPanelToChart(MonthlyExpenditure[], byte, int, String)}.
 * The right side populates with the two dropdown boxes and the radio buttons,
 * caused by {@link #updateOptionsPanel(String, String)}. These are initial
 * displays, which can be changed with further user input. The dropdown boxes
 * will remain the same throughout, but the chart can be changed by invoking
 * one of the chart setters and clicking the {@link #chartButton}, and the
 * radio buttons can be swapped to checkboxes depending on the dropdown boxes'
 * current selections.
 * @see View
 */
public class GUI implements View
{
    // Main frame and layout
    /**
     * This frame displays all the contents of the GUI.
     */
    private JFrame frame;
    /**
     * The layout manager for the main {@link #frame}.
     */
    private GroupLayout layout;

    // Left panel and all of its contents
    //private JPanel leftPanel;
    /**
     * The first left-hand "page" of the application. It contains a welcome
     * label (which includes brief guidelines), a file request label, a text
     * input field, and a load button. Its construction is handled in
     * {@link #setWelcome()}.
     */
    private JPanel fileInputPanel;
    /**
     * This is the field where the user inputs the file directory, whose string
     * may be accessed via {@link #getFileInputFieldText()}.
     */
    private JTextField fileInputField;
    /**
     * This is simply a load button for when the user is done inputting the
     * file directory.
     */
    private JButton loadButton;
    /**
     * The second left-hand "page" of the application. It is where the charts
     * will be displayed. Initially, it is set through the method,
     * {@link #updateLeftPanelToChart(MonthlyExpenditure[], byte, int, String)}.
     * Subsequently though, it switches between charts using
     * {@link #setChart(JPanel)}.
     */
    private JPanel chartPanel;

    // Right panel and all of its contents
    /**
     * The right panel, which is initially empty. After receiving a valid file,
     * it populates with the options that affect the chart display. Namely, it
     * contains two dropdown boxes (one to indicate chart type, one to indicate
     * category type), either a list of radio buttons or a list of checkboxes,
     * and a "Chart It!" button. This is constructed through the method,
     * {@link #updateOptionsPanel(String, String)}.
     */
    private JPanel optionsPanel;
    /**
     * This is the dropdown box that lets the user select between using a Pie
     * chart or a Line chart.
     */
    private JComboBox<String> pielineComboBox;
    /**
     * This is the dropdown box that lets the user select between displaying
     * categories or subcategories. Set in {@link #setDropdowns()}.
     */
    private JComboBox<String> catsubcatComboBox;
    /**
     * This is a panel containing radio buttons that correspond to dates. It
     * is only displayed if the {@link #pielineComboBox} is selected to the Pie
     * Chart type. Set in {@link #setDropdowns()}.
     */
    private JPanel datesRadioPanel;
    /**
     * The radio button group. Set in {@link #setRadioButtons(TreeSet)}. The
     * selection can be accessed via the {@link #getRadioSelection()}.
     */
    private ButtonGroup radioGroup;
    /**
     * This is a panel containing checkboxes that correspond to either the
     * categories or subcategories. Set in {@link #setCheckboxes(TreeSet)}.
     */
    private JPanel checkPanel;
    /**
     * This list contains the current set of checkboxes. Set in
     * {@link #setCheckboxes(TreeSet)}.
     */
    private ArrayList<JCheckBox> checkboxes;
    /**
     * This button changes the chart displayed.
     */
    private JButton chartButton;
    
    /**
     * Width dimension.
     */
    private static final int PREFERRED_WIDTH = 800;
    /**
     * Height dimension.
     */
    private static final int PREFERRED_HEIGHT = 600;

    // Bottom panel
    /**
     * This will display action responses.
     */
    /*
     * (In reality, it was primarily for clear and obvious communication when
     * making the application, but I also think it's nice to have this sort of
     * feedback as an application user myself. Probably in a more official or
     * sophisticated application, there would be unique sound or visual
     * feedback. It does, however, display if there was an error in loading a
     * file, which is necessary for the user to know anyway.)
     */
    private JLabel messageLabel;

    /**
     * Constructor. It initializes some fields.
     */
    public GUI()
    {
        // Frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Panels and stuff
        messageLabel = new JLabel();
        optionsPanel = new JPanel();
        chartPanel = new JPanel();
    }
    
    /**
     * This should be the very first method called. It sets up the frame and
     * displays it. At this point, it will contain the {@link #fileInputPanel},
     * an empty {@link #optionsPanel}, and the {@link #messageLabel}.
     */
    @Override
    public void setUp()
    {
        // I'm not sure exactly whether this should be in the constructor or
        // if it's better here, or if the Presenter should call it. It seemed
        // more convenient to me to have it called within the GUI class, since
        // the order in which to initialize things is actually important.
        setWelcome();
        
        // Layout
        layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        optionsPanel.setPreferredSize(new Dimension(PREFERRED_WIDTH/3, PREFERRED_HEIGHT));
        
        // Horizontal
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(
                        GroupLayout.Alignment.LEADING)
                        .addComponent(fileInputPanel)
                        .addComponent(messageLabel)
                        )
                .addComponent(optionsPanel)
                );
        // Vertical
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(
                        GroupLayout.Alignment.LEADING)
                        .addComponent(fileInputPanel) 
                        .addComponent(optionsPanel)
                        )
                .addComponent(messageLabel)
                );
        
        frame.setTitle("Charts");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * This method populates the {@link #fileInputPanel} with a brief
     * introduction, guidelines, example data entry, {@link #fileInputField},
     * and the {@link #loadButton}. It is called in {@link #setUp()}.
     */
    private void setWelcome()
    {
        // Introduction, guidelines, example data entry
        String welcomeText = "<html>Welcome!<p>"
                + "This application depicts expenditures by month or category" 
                + "type as a pie or line chart.<p>Data entry should mimic the"
                + " form: \"date;category;amount\", each on their own line.<p>"
                + "<ul><li>date: integer date in the form \"yyyymm\""
                + "<li>category: in general, no real restriction, but for"
                + " specifying the grocery subcategory, prefix with \"g_\""
                + "<li>amount: non-negative number with no more than two "
                + "decimal places and no less than one if decimal point is "
                + "present</ul>Examples:"
                + "<blockquote>201806;g_produce;47.88<p>"
                + "201812;tax;28.0<p>"
                + "201804;water;14</blockquote>";
        JLabel welcomeLabel = new JLabel(welcomeText);
        JLabel fileRequestLabel = new JLabel(
                "Please input the file directory:");
        // file input field for file directory
        fileInputField = new JTextField();
        // load button which processes the text field info
        loadButton = new JButton("Load");

        fileInputPanel = new JPanel();
        fileInputPanel.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

        // Layout of the fileInputPanel
        GroupLayout fipLayout = new GroupLayout(fileInputPanel);
        fileInputPanel.setLayout(fipLayout);
        fipLayout.setAutoCreateGaps(true);
        fipLayout.setAutoCreateContainerGaps(true);
        // Horizontal
        fipLayout.setHorizontalGroup(fipLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING)
                .addComponent(welcomeLabel)
                .addComponent(fileRequestLabel)
                .addGroup(fipLayout.createSequentialGroup()
                        .addComponent(fileInputField)
                        .addComponent(loadButton)
                        )
                );
        // Vertical
        fipLayout.setVerticalGroup(fipLayout.createSequentialGroup()
                .addComponent(welcomeLabel)
                .addComponent(fileRequestLabel)
                .addGroup(fipLayout.createParallelGroup(
                        GroupLayout.Alignment.BASELINE)
                        .addComponent(fileInputField)
                        .addComponent(loadButton)
                        )
                );
    }

    /**
     * This method will create a Pie chart.
     * @param me The data.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param date The date to grab information for.
     * @param title The title of the chart.
     */
    @Override
    public void setChart(MonthlyExpenditure[] me, byte catFlag, int date, String title)
    {
        JPanel newChart = PieMaker.createPanel(me, catFlag, date, title);
        newChart.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        setChart(newChart);
    }

    /**
     * This method will create a Line chart.
     * @param me The data.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param spec The specific categories/subcategories to use.
     * @param title The title of the chart.
     */
    @Override
    public void setChart(MonthlyExpenditure[] me, byte catFlag, String spec, String title)
    {
        JPanel newChart = LineMaker.createPanel(me, catFlag, spec, title);
        newChart.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        setChart(newChart);
    }
    
    /**
     * This switches the old chart out with a new chart.
     * @param newChart The new chart.
     */
    private void setChart(JPanel newChart)
    {
        layout.replace(chartPanel, newChart);
        chartPanel = newChart;
        chartPanel.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
    }

    /**
     * This sets up the two dropdown boxes, {@link #pielineComboBox} and
     * {@link #catsubcatComboBox}.
     */
    @Override
    public void setDropdowns()
    {
        String[] pielineStrings = {"Pie Chart", "Line Chart"};
        String[] catsubcatStrings = {"Categories", "Subcategories"};
        pielineComboBox = new JComboBox<String>(pielineStrings);
        catsubcatComboBox = new JComboBox<String>(catsubcatStrings);
    }

    /**
     * This sets up the checkboxes, {@link #checkboxes} that will be displayed
     * in the {@link #checkPanel}.
     * @param fields A set of category fields from MonthlyExpenditure
     */
    @Override
    public void setCheckboxes(TreeSet<String> fields)
    {
        /*
         * This is a general setup. A few thoughts. There are a few ways I've
         * considered to go about this, I think, and I don't know if this is
         * the best way, and I doubt that it is.
         * 1. 2 checkbox lists full of cats and subcats, 1 checkPanel
         *    This would require two instantiation methods (one for each list)
         *    and one method to swap the checkPanel's contents, but the swap
         *    method would have to iterate over each checkbox to re-add the
         *    fields (something like checkPanel.add(the checkbox)). It also
         *    might need a way to remember which one to swap from or an
         *    extra input. Similarly, retrieving the checkboxes from
         *    getCheckSelections() would have to be more specific.
         * 2. 2 checkbox lists full of cats and subcats, 2 checkPanels
         *    This would require two instantiation methods (one for each list)
         *    and one method to swap between the checkPanels. This would
         *    perhaps be the lightest in terms of processing because anything
         *    beyond the initial setup is a simple swap, but it would have
         *    to save the most data. It also has the same issue as above, which
         *    is that it needs a way to remember which one to swap from or an
         *    extra input. Similarly to above as well, retrieving the
         *    checkboxes from getCheckSelections() would have to be more
         *    specific.
         * 3. 1 checkbox list full of one of cats or subcats, 1 checkPanel
         *    (Current) One instantiation method, but it's called again and 
         *    again when switching the panel. One swap also. Unlike the above
         *    two, every method used can be "straightforward" and general.
         * Maybe there are even more ways to go about it though!
         */
        checkPanel = new JPanel(new GridLayout(0, 1));
        checkboxes = new ArrayList<JCheckBox>(fields.size() + 1);
        
        // iterates over the data fields and adds them as checkboxes
        Iterator<String> it = fields.iterator();
        int i = 0;
        while (it.hasNext())
        {
            String s = it.next();
            JCheckBox ncb = new JCheckBox(s, true);
            ncb.setActionCommand(s);
            checkboxes.add(i, ncb);
            checkPanel.add(checkboxes.get(i));
            ++i;
        }
        
        JCheckBox ncb = new JCheckBox("total", true);
        ncb.setActionCommand("total");
        checkboxes.add(i, ncb);
        checkPanel.add(checkboxes.get(i));
    }

    /**
     * This sets up the radio buttons, {@link #radioGroup} that will be
     * displayed in the {@link #datesRadioPanel}.
     * @param fields The dates from MonthlyExpenditure
     */
    @Override
    public void setRadioButtons(TreeSet<String> fields)
    {
        datesRadioPanel = new JPanel(new GridLayout(0, 1));
        radioGroup = new ButtonGroup();
        
        JRadioButton defaultButton = new JRadioButton("All");
        defaultButton.setActionCommand("0");
        defaultButton.setSelected(true);
        radioGroup.add(defaultButton);
        datesRadioPanel.add(defaultButton);
        
        Iterator<String> it = fields.iterator();
        while (it.hasNext())
        {
            String s = it.next();
            JRadioButton b = new JRadioButton(s);
            b.setActionCommand(s);
            radioGroup.add(b);
            datesRadioPanel.add(b);
        }
    }

    /**
     * Simply sets up the {@link #chartButton}.
     */
    @Override
    public void setChartButton()
    {
        chartButton = new JButton("Chart It!");
    }

    /**
     * This lets one set the message displayed by {@link #messageLabel}.
     * @param s The message you want to display!
     */
    @Override
    public void setMessage(String s)
    {
        // maybe a different method name would be better for consistency's sake
        messageLabel.setText(s);
    }

    /**
     * This adds an ActionListener to the {@link #fileInputField}.
     */
    @Override
    public void addFileInputActionListener(ActionListener listener)
    {
        fileInputField.addActionListener(listener);
    }

    /**
     * This adds an ActionListener to the {@link #loadButton}.
     */
    @Override
    public void addLoadButtonActionListener(ActionListener listener)
    {
        loadButton.addActionListener(listener);
    }

    /**
     * This adds an ActionListener to the {@link #pielineComboBox}.
     */
    @Override
    public void addChartTypeActionListener(ActionListener listener)
    {
        pielineComboBox.addActionListener(listener);
    }

    /**
     * This adds an ActionListener to the {@link #catsubcatComboBox}.
     */
    @Override
    public void addCatSubcatActionListener(ActionListener listener)
    {
        catsubcatComboBox.addActionListener(listener);
    }

    /**
     * This adds an ActionListener to the {@link #chartButton}.
     */
    @Override
    public void addChartButtonActionListener(ActionListener listener)
    {
        chartButton.addActionListener(listener);
    }

    /**
     * This will update the left panel from the {@link #fileInputPanel} to the
     * {@link #chartPanel}. It is called after a proper file has been
     * processed. By default, it will generate a Pie chart.
     * @param me The data.
     * @param catFlag The indicator for whether to use categories or
     *                subcategories.
     * @param date The date to use.
     * @param title The title for the chart.
     */
    @Override
    public void updateLeftPanelToChart(
            MonthlyExpenditure[] me, byte catFlag, int date, String title)
    {
        chartPanel = PieMaker.createPanel(me, catFlag, date, title);
        layout.replace(fileInputPanel, chartPanel);
    }
    
    /**
     * Depending on the parameters, it will change whether radio or checkboxes
     * are to be displayed in the {@link #optionsPanel}.
     * @param chartType The type of chart (pie or line).
     * @param catType The type of categories (cats/subcats), which only affects
     *                the checkboxes.
     */
    @Override
    public void updateOptionsPanel(String chartType, String catType)
    {
        /*
         * Using the .replace(Component, Component) method could be cool, but
         * then the layout manager might have to be saved, or at least I'm not
         * sure how else to keep track of it. It would be probably better to
         * not have to reconstruct all of the options over and again.
         */
        optionsPanel.removeAll();
        
        JPanel selections = new JPanel();
        
        if (chartType.equals("Pie Chart"))
        {
            // pie
            // show months
            selections.add(datesRadioPanel);
        }
        else
        {
            // line
            // show cats/subcats
            selections.add(checkPanel);
        }
        
        // Layout setup
        GroupLayout opLayout = new GroupLayout(optionsPanel); 
        optionsPanel.setLayout(opLayout);
        opLayout.setAutoCreateGaps(true);
        opLayout.setAutoCreateContainerGaps(true);
        
        // Horizontal
        opLayout.setHorizontalGroup(opLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING)
                .addComponent(pielineComboBox)
                .addComponent(catsubcatComboBox)
                .addComponent(selections)
                .addComponent(chartButton)
                );
        // Vertical
        opLayout.setVerticalGroup(opLayout.createSequentialGroup()
                .addComponent(pielineComboBox)
                .addComponent(catsubcatComboBox)
                .addComponent(selections)
                .addComponent(chartButton)
                );
    }

    /**
     * Simple getter for {@link #fileInputField}.
     */
    @Override
    public String getFileInputFieldText()
    {
        // I'm not sure why the selectAll() doesn't work when the load button
        // is hit :\
        fileInputField.selectAll();
        return fileInputField.getText();
    }

    /**
     * Simple getter for the selection in {@link #pielineComboBox}.
     */
    @Override
    public String getChartTypeSelection()
    {
        return (String) pielineComboBox.getSelectedItem();
    }

    /**
     * Simple getter for the selection in {@link #catsubcatComboBox}.
     */
    @Override
    public String getCatSubcatSelection()
    {
        return (String) catsubcatComboBox.getSelectedItem();
    }

    /**
     * Simple getter for the selection among radios in the {@link #radioGroup}.
     */
    @Override
    public String getRadioSelection()
    {
        return radioGroup.getSelection().getActionCommand();
    }

    /**
     * Simple getter for the selected checkboxes from {@link #checkboxes}.
     */
    @Override
    public ArrayList<String> getCheckSelections()
    {
        ArrayList<String> selectedBoxes = new ArrayList<String>();
        for (int i = 0; i < checkboxes.size(); ++i)
        {
            if (checkboxes.get(i).isSelected())
            {
                selectedBoxes.add(checkboxes.get(i).getActionCommand());
            }
        }
        return selectedBoxes;
    }
}
