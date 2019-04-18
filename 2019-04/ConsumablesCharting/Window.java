package grapher;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.*;

public class Window
{
   private JFrame frame;
   
   private JPanel fileInputPanel;
   
   private JLabel messageLabel;
   
   private JPanel optionsPanel;
   private JPanel datesRadioPanel;
   private JPanel catsCheckPanel;
   private JPanel subcatsCheckPanel;
   
   public Window(TreeSet<String> dates, TreeSet<String> cats, TreeSet<String> subcats, MonthlyExpenditure[] purchases)
   {
      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // File Input Elements
      String welcomeText = "<html>Welcome!<p>" +
    		  "This application depicts expenditures by month or category type as a pie or line chart.<p>" +
    		  "Data entry should mimic the form: \"date;category;amount\", each on their own line.<p>" +
    		  "<ul><li>date: integer date in the form \"yyyymmdd\"" +
    		  "<li>category: in general, no real restriction, but for specifying the grocery subcategory, prefix with \"g_\"" +
    		  "<li>amount: non-negative number with no more than two decimal places and no less than one if decimal point is present</ul>" +
    		  "Examples:<p>" +
    		  "20180623;g_produce;47.88<p>" +
    		  "20181213;tax;28.0<p>" +
    		  "20180405;water;14";
      JLabel welcomeLabel = new JLabel();
      welcomeLabel.setText(welcomeText);
      //welcomeLabel.setPreferredSize(new Dimension(1000,80));
      JLabel fileRequestLabel = new JLabel("Please input the file directory: ");
      JTextField fileInputField = new JTextField();
      fileInputField.addActionListener(new ActionListener() {
    	  @Override
    	  public void actionPerformed(ActionEvent e) {
    		  String fileDirectory = fileInputField.getText();
    	      fileInputField.selectAll();
    	      updateMessageLabel("ENTER " + fileDirectory);
    	  }
      });
      JButton loadButton = new JButton("Load");//creating instance of JButton
      /*loadButton.addActionListener(new ActionListener() {
  		@Override
  		public void actionPerformed(ActionEvent e) {
  			String fileDirectory = fileInputField.getText();
  			fileInputField.selectAll();
  			updateMessageLabel("LOAD " + fileDirectory);
  		}
      });*/
      fileInputPanel = new JPanel();
      fileInputPanel.setPreferredSize(new Dimension(600, 500));
      GroupLayout fipLayout = new GroupLayout(fileInputPanel);
      fileInputPanel.setLayout(fipLayout);
      fipLayout.setAutoCreateGaps(true);
      fipLayout.setAutoCreateContainerGaps(true);
      // Horizontal
      fipLayout.setHorizontalGroup(fipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
    		  .addGroup(fipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    				  .addComponent(fileInputField)
    				  .addComponent(loadButton)
    				  )
    		  );
      
      // General progress label
      messageLabel = new JLabel("(Messages will be dispalyed here.)");
      
      // Drop down elements
      String[] pielineStrings = {"Pie Chart", "Line Chart"};
      String[] catsubcatStrings = {"Categories", "Subcategories"};
      JComboBox<String> pielineComboBox = new JComboBox<String>(pielineStrings);
      JComboBox<String> catsubcatComboBox = new JComboBox<String>(catsubcatStrings);
      pielineComboBox.addActionListener(new ActionListener() {
    	  @Override
    	  public void actionPerformed(ActionEvent e) {
    		  JComboBox cb = (JComboBox)e.getSource();
    		  String selection = (String)cb.getSelectedItem();
    		  updateMessageLabel("Changed PLCB to " + selection);
    		  // TODO: update Options Panel
    		  updateOptionsPanel(selection, (String) catsubcatComboBox.getSelectedItem());
    	  }
      });
      catsubcatComboBox.addActionListener(new ActionListener() {
    	  @Override
    	  public void actionPerformed(ActionEvent e) {
    		  JComboBox cb = (JComboBox)e.getSource();
    		  String selection = (String)cb.getSelectedItem();
    		  updateMessageLabel("Changed CSCB to " + selection);
    		  // TODO: update Options Panel
    		  updateOptionsPanel((String) pielineComboBox.getSelectedItem(), selection);
    	  }
      });
      // Option elements
      //   Dates
      //String[] dateStrings = (String[]) dates.toArray();
      datesRadioPanel = new JPanel(new GridLayout(0, 1));
      ButtonGroup dateRadioButtons = generateRadioButtonGroup(dates);
      //dateRadioButtons = generateRadioButtonGroup(dates);
      //   cats/subcats
      //String[] catsStrings = (String[]) cats.toArray();
      //String[] subcatsStrings = (String[]) subcats.toArray();
      catsCheckPanel = new JPanel(new GridLayout(0, 1));
      subcatsCheckPanel = new JPanel(new GridLayout(0, 1));
      //JCheckBox[] catsCheckBoxes = generateCheckboxes(cats);
      //catsCheckBoxes = generateCheckboxes(cats);
      JCheckBox[] catsCheckBoxes = new JCheckBox[cats.size()];
	   
	   Iterator<String> catsIt = cats.iterator();
	   int i = 0;
	   while (catsIt.hasNext())
	   {
		   String s = catsIt.next();
		   System.out.print(s + " ");
		   catsCheckBoxes[i] = new JCheckBox(s);
		   catsCheckBoxes[i].setSelected(true);
	       catsCheckPanel.add(catsCheckBoxes[i]);
	       ++i;
	   }
	   
      //JCheckBox[] subcatsCheckBoxes = generateCheckboxes(subcats);
      //subcatsCheckBoxes = generateCheckboxes(subcats);
	  JCheckBox[] subcatsCheckBoxes = new JCheckBox[subcats.size()];
	  Iterator<String> subcatsIt = subcats.iterator();
	  i = 0;
	  while (subcatsIt.hasNext())
	  {
		  subcatsCheckBoxes[i] = new JCheckBox(subcatsIt.next());
		  subcatsCheckBoxes[i].setSelected(true);
		  subcatsCheckPanel.add(subcatsCheckBoxes[i]);
		  ++i;
	  }
      
      optionsPanel = new JPanel();
      
      // Graph button
      JButton graphButton = new JButton("Graph It");
      graphButton.addActionListener(new ActionListener() {
    	  @Override
    	  public void actionPerformed(ActionEvent e) {
    		  String plcbSelection = (String)pielineComboBox.getSelectedItem();
    		  String cscbSelection = (String)catsubcatComboBox.getSelectedItem();
    		  if (plcbSelection.equals("Pie Chart"))
    		  {
    			  // Pie Chart
    			  // dateRadioButtons
    			  String command = dateRadioButtons.getSelection().getActionCommand();
    			  // TODO: Construct Pie Chart depending on command
    		  }
    		  else
    		  {
    			  // Line Chart
    			  String selections = "";
    			  // catsCheckBoxes or subcatsCheckBoxes
    			  if (cscbSelection.contentEquals("Categories"))
    			  {
    				  // Categories
    				  for (int i = 0; i < catsCheckBoxes.length; ++i)
    				  {
    					  if (catsCheckBoxes[i].isSelected())
    					  {
    						  selections += catsCheckBoxes[i].getText() + ";";
    					  }
    				  }
    			  }
    			  else
    			  {
    				  // Subcategories
    				  for (int i = 0; i < subcatsCheckBoxes.length; ++i)
    				  {
    					  if (subcatsCheckBoxes[i].isSelected())
    					  {
    						  selections += subcatsCheckBoxes[i].getText() + ";";
    					  }
    				  }
    			  }
    			  // TODO: Construct Line Chart depending on command
    		  }
    		  // TODO: Collect all the information from...
    		  // pielineComboBox
    		  // catsubcatComboBox
    		  // and options {depending on above two, pick one: dateRadioButtons, catsCheckBoxes, subcatsCheckBoxes}
    		  updateMessageLabel("GRAPH requested");
    	  }
      });
      
      // TEMPORARY TODO: REMOVE LATER
      JPanel testChartPanel = PieGrapher.createPanel(purchases, (byte) 0, 0);
      testChartPanel.setPreferredSize(new Dimension(600,500));
      
      // Layout Setup
      GroupLayout layout = new GroupLayout(frame.getContentPane());
      frame.getContentPane().setLayout(layout);
      layout.setAutoCreateGaps(true);
      layout.setAutoCreateContainerGaps(true);
      
      // File Loading Elements: welcomeLabel, fileRequestLabel, fileInputField, loadButton
      // Charting Elements: pielineComboBox, catsubcatComboBox, dateRadioButtons, catsCheckBoxes, subcatsCheckBoxes
      // Other Elements: messageLabel
      // TEST ELEMENT: testChartPanel
      
      // Horizontal Group
      layout.setHorizontalGroup(layout.createSequentialGroup()
    		  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				  .addComponent(fileInputPanel)
    				  .addComponent(messageLabel)
    				  )
    		  .addComponent(optionsPanel)
    		  );
      /*layout.setHorizontalGroup(layout.createParallelGroup()
    		  .addComponent(fileInputPanel)
    		  .addComponent(testChartPanel)
    		  .addComponent(messageLabel)
    		  );*/
      
      // Vertical Group
      layout.setVerticalGroup(layout.createSequentialGroup()
    		  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				  .addComponent(fileInputPanel)
    				  .addComponent(optionsPanel)
    				  )
    		  .addComponent(messageLabel)
    		  );
      /*layout.setVerticalGroup(layout.createSequentialGroup()
    		  .addComponent(fileInputPanel)
    		  .addComponent(testChartPanel)
    		  .addComponent(messageLabel)
    		  );*/
      
      loadButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			String fileDirectory = fileInputField.getText();
    			fileInputField.selectAll();
    			updateMessageLabel("LOAD " + fileDirectory);
    			layout.replace(fileInputPanel, testChartPanel);
    		}
        });
      
      
      frame.setTitle("Charts");
      frame.pack();
      frame.setVisible(true);
   }
   
   private void updateMessageLabel(String s)
   {
	   System.out.println(s);
	   messageLabel.setText(s);
   }
   
   private JCheckBox[] generateCheckboxes(TreeSet<String> set)
   {
	   JCheckBox[] options = new JCheckBox[set.size()];
	   
	   Iterator<String> setIt = set.iterator();
	   int i = 0;
	   while (setIt.hasNext())
	   {
		   String s = setIt.next();
		   System.out.print(s + " ");
		   options[i] = new JCheckBox(s);
		   options[i].setSelected(true);
	   }
	   
	   return options;
   }
   
   private ButtonGroup generateRadioButtonGroup(TreeSet<String> dates)
   {
	   ButtonGroup g = new ButtonGroup();
	   
	   JRadioButton defaultButton = new JRadioButton("All");
	   defaultButton.setActionCommand("0");
	   defaultButton.setSelected(true);
	   defaultButton.addActionListener(new ActionListener() {
		   @Override
		   public void actionPerformed(ActionEvent e)
		   {
			   // TODO: What happens?
			   updateMessageLabel("RSELECT " + defaultButton.getActionCommand());
		   }
	   });
	   
	   Iterator<String> datesIt = dates.iterator();
	   while (datesIt.hasNext())
	   {
		   String s = datesIt.next();
		   JRadioButton b = new JRadioButton(s);
		   b.setActionCommand(s);
		   b.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e)
			   {
				   // TODO: What happens?
				   updateMessageLabel("RSELECT " + b.getActionCommand());
			   }
		   });
		   g.add(b);
		   datesRadioPanel.add(b);
	   }
	   
	   return g;
   }
   
   private void updateOptionsPanel(String chartType, String catType)
   {
	   optionsPanel.removeAll();
	   
	   if (chartType.equals("Pie Chart"))
	   {
		   // pie
		   // show months
		   optionsPanel.add(datesRadioPanel);
	   }
	   else
	   {
		   // line
		   JPanel cscPanel = new JPanel(new GridLayout(0, 1));
		   // show cat or subcat
		   if (catType.equals("Categories"))
		   {
			   // categories
			   optionsPanel.add(catsCheckPanel);
		   }
		   else
		   {
			   // subcategories
			   optionsPanel.add(subcatsCheckPanel);
		   }
	   }
   }
}
