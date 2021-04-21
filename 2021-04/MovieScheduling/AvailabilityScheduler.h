#ifndef AVAILABILITYSCHEDULER_H
#define AVAILABILITYSCHEDULER_H

#include <string>
#include <vector>
#include <regex>

#include "Viewer.h"

/*
This class contains an audience, which is a vector of Viewers, that are parsed
from a file. It will list valid time windows given a movie duration and minimum
number of participants.

After being constructed, a file must be given to populate the audience, using
parseFile(std::string). Then, the intersecting availabilities of the viewers in
the audience can be found with findSchedules(int,int) or findSchedulesTest();
the time windows will be printed to console.
*/
class AvailabilityScheduler
{
    private:
        // fields
        /*
        This holds all the Viewers that are parsed from a file using the method
        parseFile(std::string).
        */
        std::vector<Viewer> audience;
        /*
        Seven regular expressions that correspond to days of the week. They are
        used every time a day is expected to be parsed.
        */
        std::regex sundayMatch;
        std::regex mondayMatch;
        std::regex tuesdayMatch;
        std::regex wednesdayMatch;
        std::regex thursdayMatch;
        std::regex fridayMatch;
        std::regex saturdayMatch;
        // functions
        /*
        This will check to see if a line starts with a day of the week. If a day
        is found, it will then parseTime(std::string, Viewer, int).
        */
        bool parseDay(std::string line, Viewer &v);
        /*
        This will parse time intervals found in a day line.
        */
        bool parseTime(std::string line, Viewer &v, int day);
        /*
        This converts a time in minutes to a string of HH:MM format.
        */
        std::string toHHMMString(int timeInMin);
        /*
        This will simply print the schedules of every Viewer in the audience.
        */
        void printAllAudienceSchedules();
    public:
        // constructor
        AvailabilityScheduler();
        /*
        Parses audience data from a file given the fileName. Returns true if it
        has opened and read the file successfully.
        */
        bool parseFile(std::string fileName);
        /*
        Finds time windows of intersecting availabilities for the movie duration
        in minutes and the minimum amount of participants needed.
        */
        void findSchedules(int minMovieDuration, int minimumParticipants);
        /*
        Finds time windows of intersecting availabilities for the duration of
        the movie, but the values of the movie duration and minimum amount of
        participants are hardcoded.
        */
        void findSchedulesTest();
};

#endif // !AVAILABILITYSCHEDULER_H
