package grapher;

import java.util.Iterator;
import java.util.TreeSet;

import org.jfree.ui.RefineryUtilities;

public class Main {

   public static void main(String[] args)
   {
      Parser p = new Parser();
      MonthlyExpenditure[] purchases = p.getCostList("H:/Documents/Projects/ConsumablesCharting/sample.dat"); // held by month

      int greatestCatSize = 0;
      int greatestGrocSize = 0;
      TreeSet<String> dates = new TreeSet<String>();
      TreeSet<String> cats = new TreeSet<String>();
      TreeSet<String> subcats = new TreeSet<String>();

      for (int i = 0; i < purchases.length; ++i)
      {
         System.out.println(purchases[i].toString() + "\n");

         dates.add(Integer.toString(purchases[i].getDate()));
         if (purchases[i].getMainCategories().size() > greatestCatSize)
         {
            //types.addAll(purchases[i].getMainCategories().keySet());
        	cats.addAll(purchases[i].getMainCategories().keySet());
         }
         if (purchases[i].getGrocerySubcategories().size() > greatestGrocSize)
         {
            //types.addAll(purchases[i].getGrocerySubcategories().keySet());
        	subcats.addAll(purchases[i].getGrocerySubcategories().keySet());
         }
      }

      /*
      Iterator<String> dateIt = dates.iterator();
      System.out.println("All Dates: ");
      while (dateIt.hasNext())
      {
         System.out.print(dateIt.next() + ", ");
      }

      Iterator<String> typesIt = types.iterator();
      System.out.println("\nAll Types: ");
      while (typesIt.hasNext())
      {
         System.out.print(typesIt.next() + ", ");
      }
      System.out.println();
      */

      /*
      LineGrapher g = new LineGrapher("Graph App", "Amount vs Months", purchases);
      g.pack();
      RefineryUtilities.centerFrameOnScreen(g);
      g.setVisible(true);
      */

      new Window(dates, cats, subcats, purchases);
   }
}
