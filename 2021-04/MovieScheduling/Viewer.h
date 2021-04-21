#ifndef VIEWER_H
#define VIEWER_H

#include <string>
#include <array>
#include "DayAvailability.h"

/*
This represents a viewer with a name and their availability over the week.
*/
class Viewer
{
    private:
        // fields
        /*
        The name of a viewer, which must be set in the constructor.
        */
        std::string name;
        /*
        The availability over the seven days of the week.
        */
        std::array<DayAvailability, 7> days;
        // functions
        /*
        This converts a 24-hour hhMM time to minutes.
        */
        int timeToMin(int hhmmTime);
    public:
        /*
        This holds the days of the week and their associated numbers.
        */
        enum DayOfWeek
        {
            SUNDAY = 0,
            MONDAY = 1,
            TUESDAY = 2,
            WEDNESDAY = 3,
            THURSDAY = 4,
            FRIDAY = 5,
            SATURDAY = 6
        };
        /*
        The constructor, which requires the viewer's name n.
        */
        Viewer(std::string n);
        /*
        This will add availability to a day, given the corresponding day number
        (see DayOfWeek) and the start and end time intervals in 24-hour hhMM.
        */
        void addToDayAvailability(int day, int timeHHMMStart, int timeHHMMEnd);
        /*
        This checks if the Viewer is available at a given starting time for the
        length of the movieDurationMin on the day of the week.
        */
        bool isAvailableAt(int day, int startMin, int movieDurationMin);
        /*
        This converts the Viewer to a string with the name of the Viewer and the
        availability over the course of the week.
        */
        std::string toString();
        /*
        This returns the name of the Viewer.
        */
        std::string getName();
};

#endif