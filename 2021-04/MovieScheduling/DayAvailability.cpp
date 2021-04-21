#include "DayAvailability.h"

#include <sstream>

// CONSTRUCTORS
/*
Empty constructor. Starts with no availability.
*/
DayAvailability::DayAvailability()
{
    this->availability = std::bitset<Constants::BITSPERDAY>(0);
}

/*
A constructor with the availability given by the other bitset.
*/
DayAvailability::DayAvailability(std::bitset<96> other)
    : availability(other)
{
}

// PUBLIC FUNCTIONS
/*
Adds the interval [startMin,endMin) to the availability if possible. An interval
must be in 0 minutes and 1440 minutes to fit into a day, and the interval has
to be in the proper order (startMin < endMin). If the interval is valid, then
we can count how many bits the interval takes up, rounding down naturally with
integer division, since rounding down someone's availability is 'safer'. Then
from the startBit (the bit corresponding to the startMin), set all the times
in the interval to true (available).
*/
void DayAvailability::addAvailableTimes(int startMin, int endMin)
{
    // make changes to availability
    // 0 < timeStart < timeEnd <= MAXTIME
    if ((startMin >= 0) && (endMin > startMin)
        && (endMin <= Constants::MAXTIME))
    {
        // find number of 15 min bits to set true, rounding down
        unsigned int intervalBits = (endMin - startMin) / Constants::MINSPERBIT;
        // get position of the start time
        int startBit = getStartBit(startMin);

        if (startBit != -1)
        {
            // set availability at these times to true
            for (unsigned int i = 0; (i < intervalBits)
                && (i < availability.size()); ++i)
            {
                availability[(startBit + i)] = true;
            }
        }
    }
    // or else no change
}

/*
This checks if the day has availability at a starting minute for a given length
of a movie. Returns true if there is availability for the duration. This will
check at the "joints" (times near the end of the day and moving into the next),
so it needs the availability from the next day as well.
*/
bool DayAvailability::isAvailableAt(int startMin, int movieDurationMin, DayAvailability nextDay)
{
    // make required information from data
    int startBit = getStartBit(startMin);
    int intervalBits = getCeilIntervalBits(movieDurationMin);

    // can check the always-false cases
    if (startBit == -1 || intervalBits == -1 /* bad bounds */
        || this->availability.none() /* no avail at all */
        || !(availability[startBit]) /* not avail at timeStart */ )
    {
        return false;
    }

    unsigned int endBit = startBit + intervalBits;

    if (endBit >= this->availability.size())
    {
        // the endBit would exceed the first day, so joint check required
        // get the number of bits that will go into the next day
        int nextInterval = endBit - this->availability.size();

        // check availability for this day
        for (unsigned int i = startBit; i < this->availability.size(); ++i)
        {
            if (!(this->availability[i]))
            {
                return false;
            }
        }

        // check if bits into next day do not exceed the next day
        if (nextInterval < nextDay.availability.size())
        {
            // check availability for next day
            for (unsigned int i = 0; i < nextInterval; ++i)
            {
                if (!(nextDay.availability[i]))
                {
                    return false;
                }
            }
            // finally can return true
            return true;
        }
        else
        {
            // it shouldn't actually get here because getCeilIntervalBits
            // will have already required movie length to be no more than a
            // whole day's length
            return false;
        }
    }
    else
    {
        // movie interval is within this day, so only need to check this day
        for (unsigned int i = startBit; i < endBit; ++i)
        {
            if (!(availability[i]))
            {
                return false;
            }
        }
        return true;
    }
}

/*
This simply checks if there is any availability. Returns true if any bit is set
to true.
*/
bool DayAvailability::hasAnyAvailability()
{
    return availability.any();
}

/*
This makes a string from the availability bitset, spaced out in groups of four
(1 hour is 4 bits) and split into new lines every four hours.
For example, a day with availabilities at 00:00-02:00 and 18:00-21:30, which
would be [0,200) and [1800,2130), is...
1111 1111 0000 0000
0000 0000 0000 0000
0000 0000 0000 0000
0000 0000 0000 0000
0000 0000 1111 1111
1111 1100 0000 0000
*/
std::string DayAvailability::toBitString()
{
    std::ostringstream sout;

    for (int i = 0; i < availability.size(); ++i)
    {
        sout << availability[i];
        if (((i + 1) % 4) == 0)
        {
            if (((i + 1) % 16) == 0)
            {
                sout << "\n";
            }
            else
            {
                sout << " ";
            }
        }
    }

    return sout.str();
}

// PRIVATE FUNCTIONS
/*
This checks the length of the movie. Returns true if the movieDurationMin is
greater than 0 minutes and less than or equal to 1440 minutes.
*/
bool DayAvailability::isMovieLengthGood(int movieDurationMin)
{
    return ((movieDurationMin > 0) && (movieDurationMin <= Constants::MAXTIME));
}

/*
This calculates the bit index corresponding to the given startMin (starting time
in minutes). This will always round down to the closest hh:00, hh:15, hh:30,
hh:45 bit.
*/
int DayAvailability::getStartBit(int startMin)
{
    // check range. No negative time. 23:59 latest start time
    if (startMin < 0 || startMin > Constants::MAXTIME)
    {
        return -1;
    }

    // always rounds down
    int startBit = startMin / Constants::MINSPERBIT;

    return startBit;
}

/*
This calculates the number of bits that the minutes (min) would occupy. It will
always round up, so it assumes the movie will take up more time, which seems
safer to me for finding availability.
*/
int DayAvailability::getCeilIntervalBits(int min)
{
    // check range
    if (!(isMovieLengthGood(min)))
    {
        return -1;
    }
    // get number of bits for this amount of min
    int intervalBits = min / Constants::MINSPERBIT;
    if (min % Constants::MINSPERBIT)
    {
        // always rounds up
        intervalBits++;
    }
    return intervalBits;
}
