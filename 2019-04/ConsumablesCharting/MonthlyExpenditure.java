package grapher;

import java.util.HashMap;

public class MonthlyExpenditure
{
   private int date;
   private HashMap<String, Double> mainCategories;
   private HashMap<String, Double> grocerySubcategories;
   
   public MonthlyExpenditure(int date)
   {
      this.date = date;
      mainCategories = new HashMap<String, Double>();
      grocerySubcategories = new HashMap<String, Double>();
   }
   
   public void addCat(String id, double amount)
   {
      if (id.contains("g_"))
      {
         String realID = id.substring(2);
         // it is a grocery subcategory
         if (grocerySubcategories.containsKey(realID))
         {
            // add to existing value
            grocerySubcategories.replace(realID, grocerySubcategories.get(realID) + amount);
         }
         else
         {
            // new value
            grocerySubcategories.put(realID, amount);
         }
      }
      else
      {
         // regular category
         if (mainCategories.containsKey(id))
         {
            // add to existing value
            mainCategories.replace(id, mainCategories.get(id) + amount);
         }
         else
         {
            // new value
            mainCategories.put(id, amount);
         }
      }
   }
   
   public int getDate()
   {
      return date;
   }
   
   public HashMap<String, Double> getMainCategories()
   {
      return mainCategories;
   }
   
   public HashMap<String, Double> getGrocerySubcategories()
   {
      return grocerySubcategories;
   }
   
   public String toString()
   {      
      return "Date: " + date
            + "\nCategories: " + mainCategories.toString()
            + "\nGrocery Subcategories: " + grocerySubcategories.toString();
   }
}
