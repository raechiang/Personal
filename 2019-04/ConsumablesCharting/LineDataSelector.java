package grapher;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map.Entry;

public class LineDataSelector extends DataSelector
{
   private LinkedList<String> inputs;
   
   public LineDataSelector(byte flag, String spec)
   {
      super(flag);
      inputs = parseInputs(spec); // spec is selected inputs of category or grocery names
      
      /*
       *
       * Line Graph (type >= 2): 1bit flag, 1 string?
    * - All Categories Over Total Time
    *   User picks CAT and ALL, return 7 lines
    *    - 1 Option: All 7 categories              0=
    * - Any Single Category Over Total Time
    *   User picks CAT and SINGLE, return 1 line
    *   REQUIRES CATEGORY
    *    - 7 Options: One for each                 0=
    * - Subcategories Over Total Time (g_ only)
    *   User picks GROC and SINGLE, return 1 line
    *   REQUIRES G_CAT
    *    - 6 Options: One for each                 1=
       */
      // y=amount, line=category name, x=date
   }

   @Override
   public HashMap<String, Double> getData(MonthlyExpenditure[] me)
   {
      // To use this getData
      HashMap<String, Double> dataMap = new HashMap<String, Double>(); // (date,amount)
      
      if (inputs.isEmpty())
      {
         return dataMap;
      }
      else
      {
         // picks next input, name of a cat/groc
         String input = inputs.pop();
         
         if (super.getCatFlag() == 0)
         {
            // wants cats
            for (int i = 0; i < me.length; ++i)
            {
               // goes through me's and grabs entries that correspond to input
               if (me[i].getMainCategories().containsKey(input))
               {
                  dataMap.put(Integer.toString(me[i].getDate()), me[i].getMainCategories().get(input));
               }
            }
         }
         else
         {
            // wants grocs
            for (int i = 0; i < me.length; ++i)
            {
               // goes through me's and grabs entries that correspond to input
               if (me[i].getGrocerySubcategories().containsKey(input))
               {
                  dataMap.put(Integer.toString(me[i].getDate()), me[i].getGrocerySubcategories().get(input));
               }
            }
         }
      }
      
      return dataMap;
   }
   
   private LinkedList<String> parseInputs(String catSpec)
   {
      LinkedList<String> outputs = new LinkedList<String>();
      
      String spec = catSpec;
      //System.out.println(spec);
      
      while (spec.length() > 1)
      {
         // names divided by ;
         int nextBreak = spec.indexOf(';');
         outputs.add(spec.substring(0, nextBreak));
         spec = spec.substring(nextBreak + 1);
         //System.out.println(spec);
      }
      
      return outputs;
   }
   
   public String getNextInput()
   {
      return inputs.get(0);
   }
   
   public boolean isInputsEmpty()
   {
      return inputs.isEmpty();
   }
}
