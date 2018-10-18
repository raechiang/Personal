/*
Sudoku Generator [2018.10]
*/
#ifndef PUZZLEGENERATOR_H
#define PUZZLEGENERATOR_H

#include <cstdint>
#include <string>

// identifying numbers for the types of solvers
enum solverType
{
    REVERT = 0,
    COPY = 1,
    ONEROW = 2,
    ONETILE = 3,
    TWOTILES = 4,
    THREETILES = 5
};

enum Constants
{
    SIZE = 81,  // 81 entries in a 9x9 board
    INIT_P = 511 // bin:111111111=0x1FF=511
};

class PuzzleGenerator
{
    private:
        // field
        uint8_t board[Constants::SIZE];
        int type;
        // functions
        // backtracking functions
        bool solveRev(uint8_t board[Constants::SIZE], const uint16_t possibilities[Constants::SIZE]);
        bool solveCop(const uint8_t board[Constants::SIZE], const uint16_t possibilities[Constants::SIZE]);
        // helper functions
        void initializeBoards(uint8_t board[Constants::SIZE], uint16_t possibilities[Constants::SIZE]);
        void generateRow(int startIndex, uint8_t board[Constants::SIZE], uint16_t possibilities[Constants::SIZE]);
        void generateTile(int startIndex, uint8_t board[Constants::SIZE], uint16_t possibilities[Constants::SIZE]);
        int getEmptyIndex(const uint8_t board[Constants::SIZE], const uint16_t possibilities[Constants::SIZE]);
        int countCandidates(int index, const uint16_t possibilities[Constants::SIZE]);
        uint8_t pickRandCandidate(const uint16_t candidates);
        uint16_t removeCandidate(uint8_t candidate, uint16_t candidateSet);
        void update(const uint8_t newValue, const int index, uint16_t possibilities[Constants::SIZE]);
        // writes the passed board to console
        void write(const uint8_t board[Constants::SIZE]);
        // reports back if the board adheres to Sudoku rules
        bool doubleCheck();
    public:
        PuzzleGenerator();
        PuzzleGenerator(int t);
        // attempts to make a new puzzle
        bool generateNewPuzzle();
        // produces a string of the board
        std::string puzzleToString();
        // produces a string that corresponds to the solver type
        std::string getIdentifier();
};

#endif