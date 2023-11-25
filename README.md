# Personal Projects
As the name implies, this contains personal projects.
## Contents
- <a href="https://github.com/raechiang/Personal/tree/master/2018-10/SudokuGenerator">2018-10/SudokuGenerator</a>: (C++) simple project that generates a filled Sudoku board
- <a href="https://github.com/raechiang/Personal/tree/master/2019-04/ConsumablesCharting">2019-04/ConsumablesCharting</a>: (Java) simple project that generates a graph meant for comparing and viewing expenditures
- <a href="https://github.com/raechiang/Personal/tree/master/2019-06/GongfuTimer">2019-06/GongfuTimer</a>: (Java) an Android app that runs and saves timers (work in progress)
- <a href="https://github.com/raechiang/Personal/tree/master/2021-04/MovieScheduling">2021-04/MovieScheduling</a>: (C++) simple project that finds time windows for a movie to watch given weekly availabilities of viewers
- <a href="https://github.com/raechiang/Personal/tree/master/2022-12/freeCodeCamp_ResponsiveWebDesign">2022-12/freeCodeCamp_ResponsiveWebDesign</a>: Free Code Camp projects, including making a survey form, a tribute page, a technical document page, a product landing page, and a personal portfolio page
- <a href="https://github.com/raechiang/Personal/tree/master/2023-10/PKS%20Move">2023-10/PKS Move</a>: Simple script that takes data from one Excel sheet and writes the information to a different sheet, using a template and user-requested columns.
- <a href="https://github.com/raechiang/Personal/tree/master/2023-11/Commission%20Move">2023-11/Commission Move</a>: Script that takes an Excel sheet or a PDF as input and writes it to an aggregate master Excel sheet, (currently) with unique instructions for two commission sources. PDF sheets are turned into images and text is parsed from the image using Tesseract.
- <a href="https://github.com/raechiang/Personal/tree/master/2023-11/JBS%20PDF%20Fill">2023-11/JBS PDF Fill</a>: Script that takes three template PDFs, one generic Excel sheet, and one office-specific Excel sheet as inputs; fills the PDFs with relevant data using pdfrw; and merges the PDFs, grouping data and PDFs by household identifiers.

## Sudoku Generator
I used to play a lot of sudoku, so I wrote a generator kind of on a whim. It just uses backtracking. I did some light comparison of different board initiations over 55000 generations for fun.
## Consumables Chart Generator
This makes a pie or line chart. I had been collecting personal expenditure data for budgeting purposes, and I thought it would be nice to visualize the data, so I would graph it by hand, but that got old fast. Initially, I made a quick-and-dirty, ugly program that just got the job done, but it was messy to work with, so I ended up rewriting it, attempting to follow the Model-View-Presenter (MVP) design pattern. Still not very pretty, but it's better.
## Gongfu Timer (WIP)
This is a timer app whose primary purpose is for me to get an introduction to developing mobile applications. The app itself is not complete, but it is useable; it's mainly the design/visual part that is unfinished.
## MovieScheduling
This finds time windows that have a desired number of participants. My friends and I like to watch movies together, but our numerous participants have different schedules and come from different time zones. Previously, I would find members' availabilities manually by hand, using when2meet to help gather data from my friends, but now I need only to record availability intervals into a text file, and the program will automatically find time windows with my desired amount of participation. There is still one part of my scheduling procedure that I have to do manually, but this takes care of the most tedious part. The part I still have to do manually is checking for member exclusion (we have no times whatsoever with 100% participation so I need to find multiple days that collectively can cover all members); I may implement a feature to solve this in the future.
## freeCodeCamp Responsive Web Design Projects
This directory contains five projects:
- Survey Form: An app with different types of input fields
- Tribute Page: An app with an image that resizes and some text information
- Technical Document Page: An app of technical documentation describing some basic Javascript concepts, and modifies its navigation bar based on screen sizes
- Product Landing Page: An app demonstrating a product (magic frogs)
- Personal Portfolio Page: An app representing my portfolio
## Python Requested Scripts
This is currently a series of three Python scripts to help automate and alleviate the tedious work of moving data among spreadsheets and PDFs. They were requested by someone else and are purposely written perhaps oddly because they were required to be used and modifiable in specific ways.
- PKS Move: Simple Excel sheet input and template Excel sheet output.
- Commission Move: Excel sheet or PDF input and aggregated template Excel sheet output.
- JBS PDF Fill: Two Excel sheet inputs and template PDF outputs.
