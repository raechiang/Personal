#ifndef DAYAVAILABILITY_H
#define DAYAVAILABILITY

#include <bitset>
#include <string>

/*
Constants used throughout the DayAvailability.
*/
enum Constants
{
    /*
    1 bit represents 15 minutes.
    */
    MINSPERBIT = 15,
    /*
    24:00 is 1440 minutes.
    */
    MAXTIME = 1440,
    /*
    The number of bits in a day, determined by day divided into 15 minutes.
    */
    BITSPERDAY = 96
};

/*
This represents the availability of a day in 96 parts. It is specifically in 96
because the day is split into 15-minute increments, since that is the format of
the tool that my real-life audience members have been using.

Availability can only be added to the day. You can check if the day has any
availability at all, or you can check if there is availability at a specific
time for a given movie length.
*/
class DayAvailability
{
    private:
        // field
        /*
        A bitset representing the availability of a movie. True is available.
        */
        std::bitset<BITSPERDAY> availability;
        // functions
        /*
        This checks if the movie length is valid, in (0,1440].
        */
        bool isMovieLengthGood(int movieDurationMin);
        /*
        Given the starting time in minutes, gets the corresponding time bit.
        */
        int getStartBit(int startMin);
        /*
        Given a length in minutes, gets the length in bits, rounding up.
        */
        int getCeilIntervalBits(int min);
    public:
        // constructors
        /*
        Empty constructor. Availability set to none.
        */
        DayAvailability();
        /*
        Instantiates the availability as the other bitset.
        */
        DayAvailability(std::bitset<96> other);
        // other functions
        /*
        Marks the given time interval as available.
        */
        void addAvailableTimes(int startMin, int endMin);
        /*
        Checks if the time is available for the movie length.
        */
        bool isAvailableAt(int startMin, int movieDurationMin, DayAvailability nextDay);
        /*
        Checks if there is any availability at all for the day.
        */
        bool hasAnyAvailability();
        /*
        Makes the bitset into a string.
        */
        std::string toBitString();
};

#endif