package grapher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Parser
{
   
   public Parser()
   {
   }
   
   public boolean foundValidFile(String name)
   {
      File filepath = new File(name);
      
      try {
         BufferedReader br = new BufferedReader(new FileReader(filepath));
         String line = br.readLine();
         while (line != null)
         {
            if (isProperString(line))
            {
               //System.out.println(line);
               // want at least one line of valid-looking entry
               return true;
            }
            line = br.readLine();
         }
         br.close();
      } catch (FileNotFoundException fe) {
         // TODO Auto-generated catch block
         fe.printStackTrace();
      } catch (IOException ioe) {
         // TODO Auto-generated catch block
         ioe.printStackTrace();
      }
      return false;
   }
   
   private boolean isProperString(String s)
   {
      return s.matches("\\d{6};\\w+;\\d*.\\d{1,2}") || s.matches("\\d{6};\\w+;\\d+");
   }
   
   public MonthlyExpenditure[] getCostList(String name)
   {
      HashMap<Integer, MonthlyExpenditure> map = new HashMap<Integer, MonthlyExpenditure>();
      
      if (foundValidFile(name))
      {
         File filepath = new File(name);
         try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();
            while (line != null)
            {
               if (isProperString(line))
               {
                  // split into date;identifier;price
                  int yearMonth = Integer.parseInt(line.substring(0, line.indexOf(';')));
                  String catID = line.substring(line.indexOf(';') + 1, line.lastIndexOf(';'));
                  double amount = Double.parseDouble(line.substring(line.lastIndexOf(';') + 1));
                  if (map.containsKey(yearMonth))
                  {
                     // adjust month fields
                     map.get(yearMonth).addCat(catID, amount);
                  }
                  else
                  {
                     // add new month
                     MonthlyExpenditure newME = new MonthlyExpenditure(yearMonth);
                     newME.addCat(catID, amount);
                     map.put(yearMonth, newME);
                  }
                  
               }
               line = br.readLine();
            }
            br.close();
         } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
      MonthlyExpenditure[] costs = new MonthlyExpenditure[map.size()];
      map.values().toArray(costs);
      
      return costs;
   }
}
