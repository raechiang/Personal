# Consumables Chart Generator
This program makes a pie or line chart given a properly-formatted file of data. Pie charts are selectable by month (or by one total), and line charts will display user-selected lines. Both can display either the general consumables categories or the subcategories. The input file should contain lines of entries like so: date;category_name;amount
- date: in the format of yyyymm
- category name: any kind of single-word string, but subcategories require the prefix of "g_" to be saved as a subcategory
- amount: some non-negative number with no more than two decimal places but no less than one if a decimal point is present
Although it was originally intended for general consumables with the subdivision of groceries, it could probably be extended for anything and one subcategory type (just keep the "g_" key prefix).
## Sample Output
![Sample 0](https://github.com/raechiang/Personal/blob/master/2019-04/ConsumablesCharting/Examples/pic00.png)
