package grapher;

import java.util.HashMap;

public abstract class DataSelector
{
   private byte catFlag;
   
   public DataSelector(byte flag)
   {
      this.catFlag = flag;
   }
   
   public byte getCatFlag()
   {
      return catFlag;
   }
   
   public abstract HashMap<String, Double> getData(MonthlyExpenditure[] me);
   
   // required flags [B, 1 bit]
   //  C. Want Month/AllTime (pie) or SpecificCats/AllCats (line) [X]
   //  B. Want categories/groceries? [X] <--Needs Button
   //  A. Want pie/line? [X] <--Needs Button
   /*
    * Pie Chart (type < 2): 1bit flag, 1 integer?
    * - Categories by Month
    *   User picks CAT and ANY 1 MONTH, return pie of 7 segments
    *   REQUIRES MONTH
    *    - 1 Option: All 7 categories              0=
    * - Subcategories by Month (g_ only)
    *   User picks GROC and ANY 1 MONTH, return pie of 6 segments
    *   REQUIRES MONTH
    *    - 1 Option: All 6 subcategories           1=
    * - Categories by Total Time
    *   User picks CAT and NO MONTH, return pie of 7 segments
    *    - 1 Option: All 7 categories              0=
    * - Subcategories by Total Time (g_ only)
    *   User picks GROC and NO MONTH, return pie of 6 segments
    *    - 1 Option: All 6 subcategories           1=
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
    * - All Subcategories Over Total Time
    *   User picks GROC and ALL, return 6 lines
    */
   
      // 100 pie/line?
      // 010 cats/groc?
      // 001 spec/all? if spec, needs more info... Actually might need only 2 bits for general flags
      // Spec versus All maybe polymorphic? Give NOTHING for ALL; Give NUMBERS? for MONTHS, OR give ? for CATEGORIES
}
