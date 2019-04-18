package grapher;

import java.util.HashMap;
import java.util.Map.Entry;

public class PieDataSelector extends DataSelector
{
   private int date;
   public PieDataSelector(byte flag, int date)
   {
      super(flag);
      this.date = date;
   }

   @Override
   public HashMap<String, Double> getData(MonthlyExpenditure[] me)
   {
      HashMap<String, Double> dataMap = new HashMap<String, Double>();
      if (date == 0) // overall
      {
         if (super.getCatFlag() == 0)
         {
            // cats of all months 0
            for (int i = 0; i < me.length; ++i)
            {
               for (Entry<String, Double> entry : me[i].getMainCategories().entrySet())
               {
                  if (dataMap.containsKey(entry.getKey()))
                  {
                     // add to existing
                     double newValue = dataMap.get(entry.getKey()) + entry.getValue();
                     dataMap.replace(entry.getKey(), newValue);
                  }
                  else
                  {
                     // make new
                     dataMap.put(entry.getKey(), entry.getValue());
                  }
               }
            }
            return dataMap;
         }
         else
         {
            // grocs of all months 1
            for (int i = 0; i < me.length; ++i)
            {
               for (Entry<String, Double> entry : me[i].getGrocerySubcategories().entrySet())
               {
                  if (dataMap.containsKey(entry.getKey()))
                  {
                     // add to existing
                     double newValue = dataMap.get(entry.getKey()) + entry.getValue();
                     dataMap.replace(entry.getKey(), newValue);
                  }
                  else
                  {
                     // make new
                     dataMap.put(entry.getKey(), entry.getValue());
                  }
               }
            }
            return dataMap;
         }
      }
      else // by the month
      {
         int dateIndex = getDateIndex(me);
         if (dateIndex != -1)
         {
            if (super.getCatFlag() == 0)
            {
               // cats of the month
               for (Entry<String, Double> entry : me[dateIndex].getMainCategories().entrySet())
               {
                  if (dataMap.containsKey(entry.getKey()))
                  {
                     // add to existing
                     double newValue = dataMap.get(entry.getKey()) + entry.getValue();
                     dataMap.replace(entry.getKey(), newValue);
                  }
                  else
                  {
                     // make new
                     dataMap.put(entry.getKey(), entry.getValue());
                  }
               }
               return dataMap;
            }
            else
            {
               // grocs of the month
               for (Entry<String, Double> entry : me[dateIndex].getGrocerySubcategories().entrySet())
               {
                  if (dataMap.containsKey(entry.getKey()))
                  {
                     // add to existing
                     double newValue = dataMap.get(entry.getKey()) + entry.getValue();
                     dataMap.replace(entry.getKey(), newValue);
                  }
                  else
                  {
                     // make new
                     dataMap.put(entry.getKey(), entry.getValue());
                  }
               }
               return dataMap;
            }
         }
      }
      return null;
   }
   
   private int getDateIndex(MonthlyExpenditure[] me)
   {
      for (int i = 0; i < me.length; ++i)
      {
         if (me[i].getDate() == date)
         {
            return i;
         }
      }
      return -1;
   }
}
