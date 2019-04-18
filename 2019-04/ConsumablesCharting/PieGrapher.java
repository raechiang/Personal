package grapher;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;

public class PieGrapher
{
   public PieGrapher()
   {
   }
   
   private static PieDataset createDataset(MonthlyExpenditure[] me) {
      DefaultPieDataset dataset = new DefaultPieDataset( );
      /*
      for (int i = 0; i < me.length; ++i)
      {
         // month, total
         double total = 0;
         for (Entry<String, Double> entry : me[i].getMainCategories().entrySet())
         {
            total += entry.getValue();
         }
         System.out.println(me[i].getDate() + " Total: " + total);
         String s = "M" + me[i].getDate();
         dataset.setValue(s, new Double (total));
      }
      
      return dataset;
      */
      
      // TODO: Needs information to be passed into here
      DataSelector pds = new PieDataSelector((byte) 0,0); // all categories over all months
      //DataSelector pds = new PieDataSelector((byte) 0, 201804); // categories over April 2018
      //DataSelector pds = new PieDataSelector((byte) 1, 0); // groceries over all months
      //DataSelector pds = new PieDataSelector((byte) 1, 201804); // groceries over April 2018
      //DataSelector pds = new PieDataSelector((byte) 1, 201910); // TODO: bad month input handling?
      HashMap<String, Double> data = pds.getData(me);
      if (!(data.isEmpty()))
      {
         for (Entry<String, Double> entry : data.entrySet())
         {
            dataset.setValue(entry.getKey(), entry.getValue());
         }
      }
      
      return dataset;
   }
   
   private static PieDataset createDataset(MonthlyExpenditure[] me, byte catFlag, int date) {
	      DefaultPieDataset dataset = new DefaultPieDataset( );
	      	      
	      DataSelector pds = new PieDataSelector(catFlag, date);
	      HashMap<String, Double> data = pds.getData(me);
	      if (!(data.isEmpty()))
	      {
	         for (Entry<String, Double> entry : data.entrySet())
	         {
	            dataset.setValue(entry.getKey(), entry.getValue());
	         }
	      }
	      
	      return dataset;
	   }
   
   // TODO: This is the important part, the only thing from the demo that is actually needed
   private static JFreeChart createChart( PieDataset dataset ) {
      JFreeChart chart = ChartFactory.createPieChart(      
         "Chart Title",   // chart title TODO: This information needs to be passed in.
         dataset,          // data
         true,             // include legend
         true, 
         false);

      return chart;
   }
   
   public static JPanel createPanel(MonthlyExpenditure[] me) {
      // TODO: needs to check if dataset returns something!
      JFreeChart chart = createChart(createDataset(me) );
      return new ChartPanel( chart );
   }
   
   public static JPanel createPanel(MonthlyExpenditure[] me, byte catFlag, int date) { // Note: date of 0 is "All"
	      // TODO: needs to check if dataset returns something?
	// TODO: Also add which category type it is? 0=cat, 1=subcat. String has to be more sophisticated if it's 0=ALLMONTHS
	   // (Need a sophisticated title generator)
	      String title = "Pie Chart for " + date;
	      JFreeChart chart = createChart(createDataset(me, catFlag, date), title);
	      return new ChartPanel( chart );
   }
   
   private static JFreeChart createChart( PieDataset dataset, String title) {
	      JFreeChart chart = ChartFactory.createPieChart(      
	         title,   // chart title TODO: This information needs to be passed in.
	         dataset,          // data
	         true,             // include legend
	         true, 
	         false);
	      return chart;
	}
}
