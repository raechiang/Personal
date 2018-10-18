/*
Sudoku Generator [2018.10]
--------------------------------------------------------------------------------
This solver uses backtracking to generate Sudoku puzzles. There are a handful
of slightly different approaches as well, which mostly regard board
initialization, but also include how the data is used when backtracking. It
outputs a total of 30 boards into a file called sampleOutput.txt, and it
generates a report called "stats.csv", which contains these three data points
about each of the different solving methods: the type of method, the total time
used to attempt 5000 generations of puzzles, and the number of successful
solutions generated.
--------------------------------------------------------------------------------
*/

#include <iostream>
#include <ctime>
#include <chrono>
#include <fstream>

#include "PuzzleGenerator.h"

int main()
{
    srand(time(NULL));

    std::ofstream samplefos;
    samplefos.open("sampleOutput.txt");
    std::ofstream statsfos;
    statsfos.open("stats.csv");
    statsfos << "type,totalTime,totalOptimal" << "\n";
    
    // there are six solver types.
    for (int t = 0; t < 6; ++t)
    {
        double time = 0;
        double completeOptimal = 0;
        int iterations = 5000;
        int sampleCount = 5;

        PuzzleGenerator pg(t);

        for (int i = 0; i < iterations; ++i)
        {
            auto start = std::chrono::system_clock::now();
            if (pg.generateNewPuzzle())
            {
                ++completeOptimal;
                if (sampleCount > 0)
                {
                    --sampleCount;
                    samplefos << pg.getIdentifier();
                    samplefos << pg.puzzleToString() << "\n";
                }
            }
            auto end = std::chrono::system_clock::now();
            std::chrono::duration<double> elapsed_seconds = end - start;

            time += elapsed_seconds.count();
        }
        statsfos << pg.getIdentifier() << "," << time << "," << completeOptimal << "\n";
        std::cout << ".";
    }

    std::cout << "\nGenerations complete!";

    samplefos.close();
    statsfos.close();

    return 0;
}