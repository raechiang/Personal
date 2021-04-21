#include "Viewer.h"

#include <sstream>

// CONSTRUCTOR
/*
The constructor, which requires a string n for the name of the Viewer.
*/
Viewer::Viewer(std::string n)
    : name(n)
{
}

// PUBLIC FUNCTIONS
/*
Given a day number, it will add the availability given by the time interval
[timeHHMMStart,timeHHMMEnd) to the corresponding day in the days array.
*/
void Viewer::addToDayAvailability(int day, int timeHHMMStart, int timeHHMMEnd)
{
    if (day >= 0 && day < days.size())
    {
        days[day].addAvailableTimes(timeToMin(timeHHMMStart), timeToMin(timeHHMMEnd));
    }
}

/*
Given a day number, it will check to see if the Viewer is available at the start
time in minutes (startMin) for the length in minutes (movieDurationMin). It must
check times all the way up to midnight, so it also needs access to the next day
of the week.
*/
bool Viewer::isAvailableAt(int day, int startMin, int movieDurationMin)
{
    // check availability
    if (day == DayOfWeek::SATURDAY)
    {
        // after Saturday is Sunday, so pass Sunday as nextDay
        return days[day].isAvailableAt(startMin, movieDurationMin, days[DayOfWeek::SUNDAY]);
    }
    else
    {
        // otherwise, after any day is just the next day in the array
        return days[day].isAvailableAt(startMin, movieDurationMin, days[day + 1]);
    }
}

/*
This returns the Viewer as a string. It writes their name and each day and their
availability as bits in increments of 15 minutes.
*/
std::string Viewer::toString()
{
    std::ostringstream sout;

    sout << "\nViewer Name: " << name;
    for (int i = 0; i < days.size(); ++i)
    {
        if (days[i].hasAnyAvailability())
        {
            sout << "\n\tDay " << i;
            // bits for easier visualization
            sout << "\n" << days[i].toBitString();
        }
    }

    return sout.str();
}

/*
This returns the name of the Viewer.
*/
std::string Viewer::getName()
{
    return name;
}

// PRIVATE FUNCTIONS
/*
This converts a time in 24-hour hhMM format to minutes from midnight.
*/
int Viewer::timeToMin(int hhmm)
{
    int hh = hhmm / 100;
    int mm = hhmm % 100;
    return (hh * 60) + mm;
}