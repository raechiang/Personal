# Personal Projects
As the name implies, this contains personal projects.
## Contents
- <a href="https://github.com/raechiang/Personal/tree/master/2018-10/SudokuGenerator">2018-10/SudokuGenerator</a>: (C++) simple project that generates a filled Sudoku board
- <a href="https://github.com/raechiang/Personal/tree/master/2019-04/ConsumablesCharting">2019-04/ConsumablesCharting</a>: (Java) simple project that generates a graph meant for comparing and viewing expenditures
- <a href="https://github.com/raechiang/Personal/tree/master/2019-06/GongfuTimer">2019-06/GongfuTimer</a>: (Java) an Android app that runs and saves timers (work in progress)
- <a href="https://github.com/raechiang/Personal/tree/master/2021-04/MovieScheduling">2021-04/MovieScheduling</a>: (C++) simple project that finds time windows for a movie to watch given weekly availabilities of viewers
## Sudoku Generator
I used to play a lot of sudoku, so I wrote a generator kind of on a whim. It just uses backtracking. I did some light comparison of different board initiations over 55000 generations for fun.
## Consumables Chart Generator
This makes a pie or line chart. I had been collecting personal expenditure data for budgeting purposes, and I thought it would be nice to visualize the data, so I would graph it by hand, but that got old fast. Initially, I made a quick-and-dirty, ugly program that just got the job done, but it was messy to work with, so I ended up rewriting it, attempting to follow the Model-View-Presenter (MVP) design pattern. Still not very pretty, but it's better.
## Gongfu Timer (WIP)
This is a timer app whose primary purpose is for me to get an introduction to developing mobile applications. The app itself is not complete, but it is useable; it's mainly the design/visual part that is unfinished.
## MovieScheduling
This finds time windows that have a desired number of participants. My friends and I like to watch movies together, but our numerous participants have different schedules and come from different time zones. Previously, I would find members' availabilities manually by hand, using when2meet to help gather data from my friends, but now I need only to record availability intervals into a text file, and the program will automatically find time windows with my desired amount of participation. There is still one part of my scheduling procedure that I have to do manually, but this takes care of the most tedious part. The part I still have to do manually is checking for member exclusion (we have no times whatsoever with 100% participation so I need to find multiple days that collectively can cover all members); I may implement a feature to solve this in the future.
