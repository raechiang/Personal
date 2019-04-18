package grapher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class LineGrapher extends ApplicationFrame
{
   public LineGrapher(String title, String chartTitle, MonthlyExpenditure[] me)
   {
      super(title);
      JFreeChart lineChart = ChartFactory.createLineChart(
            chartTitle,
            "Months","Amount for Alcohol",
            createDataset(me),
            PlotOrientation.VERTICAL,
            true,true,false);

         ChartPanel chartPanel = new ChartPanel( lineChart );
         chartPanel.setPreferredSize( new java.awt.Dimension( 600 , 500 ) );
         setContentPane( chartPanel );
   }

   private DefaultCategoryDataset createDataset(MonthlyExpenditure[] me)
   {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

      /*for (int i = 0; i < me.length; ++i) // it won't be in chronological order, must sort
      {
         for (Entry<String, Double> entry : me[i].getMainCategories().entrySet())
         {
            dataset.addValue(me[i].getMainCategories().get("alcohol"), "alcohol", Integer.toString(me[i].getDate()));
         }
      }*/

      //LineDataSelector lds = new LineDataSelector((byte) 0, "alcohol;supplement;tax;groceries;water;out;care;");
      LineDataSelector lds = new LineDataSelector((byte) 0, "alcohol;supplement;tax;groceries;water;care;");
      while (!(lds.isInputsEmpty()))
      {
         String lineName = lds.getNextInput();
         HashMap<String,Double> dataMap = lds.getData(me);

         if (!(dataMap.isEmpty()))
         {
            TreeSet<String> keySet = new TreeSet<String>(dataMap.keySet());
            Iterator<String> it = keySet.iterator();
            while (it.hasNext())
            {
               String nextDate = it.next();
               dataset.addValue(dataMap.get(nextDate), lineName, nextDate);
            }
         }
      }

      return dataset;
   }
}
