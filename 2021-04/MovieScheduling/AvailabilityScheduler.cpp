#include "AvailabilityScheduler.h"

#include <fstream>
#include <iostream>
#include <sstream>

// CONSTRUCTOR
/*
Empty constructor
*/
AvailabilityScheduler::AvailabilityScheduler()
{
    sundayMatch.assign("(sunday)(.*)", std::regex_constants::icase);
    mondayMatch.assign("(monday)(.*)", std::regex_constants::icase);
    tuesdayMatch.assign("(tuesday)(.*)", std::regex_constants::icase);
    wednesdayMatch.assign("(wednesday)(.*)", std::regex_constants::icase);
    thursdayMatch.assign("(thursday)(.*)", std::regex_constants::icase);
    fridayMatch.assign("(friday)(.*)", std::regex_constants::icase);
    saturdayMatch.assign("(saturday)(.*)", std::regex_constants::icase);
}

// PUBLIC FUNCTIONS
/*
Given a file, this will populate the audience vector with Viewer data. If it was
unable to open the file, it will return false. It will first look for Viewer
entries starting with "name:". Once detected, a day line will be expected, and
it will attempt to parseDay(std::string, Viewer). This will repeat until the end
of the file.

File Formatting:
To begin a Viewer entry, start with "name:" (case-insensitive). The whole
remainder of the line will be treated as the name string, with preceding and
trailing spaces ignored. Each day of an entry must be separated with a new line.
Each day entry needs to start with the day of the week (case-insensitive), and
the order of the days of the week does not matter. Each time interval within a
day needs to take the format of "[<start>,<end>)", where start and end are non-
negative integers corresponding to times in 24-hour hhMM format. Poorly-
formatted time intervals are ignored. Each day is separated by new lines. Time
intervals can be listed on the same line for a single day, or they can be listed
on separate lines (with the preceding day keyword). To illustrate, the
availability given by this single line
    Sunday [0,300) [900,1800)
is the same as that given by all of these lines together:
    Sunday [0,300)
    sunday: [900,1000)
    sunday[1000,1530)[1530,1600),[1600,1630) [1630,1800)
The above example can be seen in "sample02same.txt".
Due to the sequence of events, it is necessary for the last line of the file to
either be empty or carry irrelevant information.
*/
bool AvailabilityScheduler::parseFile(std::string fileName)
{
    // Sample data
    std::ifstream samplefis;
    samplefis.open(fileName);
    if (samplefis.is_open())
    {
        std::cout << "\tProcessing " << fileName;
        std::string line;

        std::getline(samplefis, line);
        while (!(samplefis.eof()))
        {
            std::regex nameIdMatch("(name:)(.*)", std::regex_constants::icase);

            // name line
            if (std::regex_match(line, nameIdMatch))
            {
                // get name
                std::string name = line.substr(5);
                while (name[0] == ' ')
                {
                    name = name.substr(1);
                }
                while (name[name.size() - 1] == ' ')
                {
                    name = name.substr(0, name.size() - 1);
                }

                Viewer newViewer(name);
                std::cout << "\n\t" << name;

                // get days
                std::string entry;
                std::getline(samplefis, entry);
                while (!(std::regex_match(entry, nameIdMatch))
                    && !(samplefis.eof()))
                {
                    if (parseDay(entry, newViewer))
                    {
                        std::getline(samplefis, entry);
                    }
                    else
                    {
                        // not a day
                        std::getline(samplefis, entry);
                        line = entry;
                    }
                }

                std::vector<Viewer>::iterator it;
                it = audience.end();
                audience.insert(it, newViewer);
            }
            else
            {
                // move cursor
                std::getline(samplefis, line);
            }
        }
        std::cout << "\n\t(EOF)" << std::endl;
    }
    else
    {
        return false;
    }

    samplefis.close();
    return true;
}

/*
This function will print out time windows that match the given arguments. It
goes through all of the Viewers' availabilities every day and adds them to a
list if they are available at the starting time for the duration of the movie.
The time window (and the names of the participants) will be printed if the
number of participants on that day is greater than or equal to the argument
minimumParticipants.
*/
void AvailabilityScheduler::findSchedules(int minMovieDuration,
    int minimumParticipants)
{
    std::cout << "\nTimes with at least " << minimumParticipants
        << " viewers for a movie that is " << minMovieDuration
        << " minutes long:";
    for (int dayNum = 0; dayNum < 7; ++dayNum)
    {
        std::cout << "\nDAY " << dayNum;
        std::vector<std::pair<int, std::string>> goodTimes(96);
        for (int i = 0; i < goodTimes.size(); ++i)
        {
            goodTimes[i] = std::pair<int, std::string>(0, "");
        }
        int goodIt = 0;
        // for every day
        for (int startMin = 0; startMin < 1440; startMin += 15)
        {
            // for every 15 min increment
            std::vector<Viewer>::iterator it;
            for (it = audience.begin(); it < audience.end(); ++it)
            {
                // if person is available, add counter and name to list
                if ((*it).isAvailableAt(dayNum, startMin, minMovieDuration))
                {
                    if (goodTimes[goodIt].first != 0)
                    {
                        goodTimes[goodIt].second
                            = goodTimes[goodIt].second.append(", " + ((*it).getName()));
                    }
                    else
                    {
                        goodTimes[goodIt].second = ((*it).getName());
                    }
                    goodTimes[goodIt].first = goodTimes[goodIt].first + 1;
                }
            }
            ++goodIt;
        }

        // write the time window if minimumParticipants is met
        for (int i = 0; i < goodTimes.size(); ++i)
        {
            if (goodTimes[i].first >= minimumParticipants)
            {
                int totalMins = i * 15;
                std::cout << "\n" << toHHMMString(totalMins)
                    << "-" << toHHMMString(totalMins + minMovieDuration);
                std::cout << " (" << goodTimes[i].first << ") "
                    << goodTimes[i].second;
            }
        }
    }
}

/*
This will look for time windows that match a movie that is 120 minutes long with
at least 3 audience members available. It also prints the audience schedules.
*/
void AvailabilityScheduler::findSchedulesTest()
{
    // Using sample data
    printAllAudienceSchedules();
    findSchedules(120, 3);
}

// PRIVATE FUNCTIONS
/*
This will determine if a day line has been encountered and then parse the time
intervals if it has. Each line must match a day of the week regular expression,
whose patterns can be seen in the constructor. Once matched, it will call the
function parseTime(std::string, Viewer, int) to pull all of the intervals in the
line and save them to the Viewer. If a day was found, it will return true. If
not, then it will return false.
*/
bool AvailabilityScheduler::parseDay(std::string line, Viewer& v)
{
    if (std::regex_match(line, sundayMatch))
    {
        std::cout << " Su0";
        parseTime(line, v, Viewer::DayOfWeek::SUNDAY);
        return true;
    }
    else if (std::regex_match(line, mondayMatch))
    {
        std::cout << " Mo1";
        parseTime(line, v, Viewer::DayOfWeek::MONDAY);
        return true;
    }
    else if (std::regex_match(line, tuesdayMatch))
    {
        std::cout << " Tu2";
        parseTime(line, v, Viewer::DayOfWeek::TUESDAY);
        return true;
    }
    else if (std::regex_match(line, wednesdayMatch))
    {
        std::cout << " We3";
        parseTime(line, v, Viewer::DayOfWeek::WEDNESDAY);
        return true;
    }
    else if (std::regex_match(line, thursdayMatch))
    {
        std::cout << " Th4";
        parseTime(line, v, Viewer::DayOfWeek::THURSDAY);
        return true;
    }
    else if (std::regex_match(line, fridayMatch))
    {
        std::cout << " Fr5";
        parseTime(line, v, Viewer::DayOfWeek::FRIDAY);
        return true;
    }
    else if (std::regex_match(line, saturdayMatch))
    {
        std::cout << " Sa6";
        parseTime(line, v, Viewer::DayOfWeek::SATURDAY);
        return true;
    }
    else
    {
        // not a day
        return false;
    }
}

/*
This will parse time intervals in the form of "[<start>,<end>)", where the start
and end are positive integers in a 24-hour hhMM format. Poorly-formatted times
are ignored. It will search the whole line for time intervals. The times will
be saved directly to the passed Viewer under the given day.
*/
bool AvailabilityScheduler::parseTime(std::string line, Viewer& v, int day)
{
    std::size_t openPos = line.find_first_of('[');
    while (openPos != std::string::npos)
    {
        // find an interval [,)
        std::size_t closePos = line.find_first_of(')', openPos);
        std::size_t commaPos = line.find_first_of(',', openPos);
        if ((closePos != std::string::npos) && (commaPos != std::string::npos)
            && (commaPos < closePos) && (closePos > openPos))
        {
            // pull the number strings from the interval
            std::string start = line.substr(openPos + 1, commaPos - openPos - 1);
            std::string end = line.substr(commaPos + 1, closePos - commaPos - 1);
            if (std::regex_match(start, std::regex("[ ]*[0-9]+[ ]*"))
                && std::regex_match(end, std::regex("[ ]*[0-9]+[ ]*")))
            {
                // pull the hhMM integers
                int timeStart = std::stoi(start);
                int timeEnd = std::stoi(end);
                // set the day of the Viewer v
                v.addToDayAvailability(day, timeStart, timeEnd);
            }
            else
            {
                // it tells you a bad time was found
                std::cout << "(bad time interval given: "
                    << start << "," << end << ")";
            }
        }
        // move cursor
        openPos = line.find_first_of('[', openPos + 1);
    }
    return true;
}

/*
This simply converts a time in minutes to a 24-hour HH:MM format.
*/
std::string AvailabilityScheduler::toHHMMString(int timeInMin)
{
    std::ostringstream sout;
    int timeHH = timeInMin / 60;
    int timeMM = timeInMin % 60;

    // HH
    if (timeHH >= 24)
    {
        // don't want 2400+ as a time
        timeHH -= 24;
    }
    if (timeHH >= 10)
    {
        sout << timeHH;
    }
    else if (timeHH > 0)
    {
        // need to append 0 because this is HH:MM, not hh:MM
        sout << "0" << timeHH;
    }
    else
    {
        // hour is 0
        sout << "00";
    }

    sout << ":";

    // mm
    if (timeMM >= 10)
    {
        sout << timeMM;
    }
    else if (timeMM > 0)
    {
        sout << "0" << timeMM;
    }
    else
    {
        sout << "00";
    }
    return sout.str();
}

/*
This prints the audience members' availability schedules using the Viewer's
toString() function.
*/
void AvailabilityScheduler::printAllAudienceSchedules()
{
    std::vector<Viewer>::iterator it;
    for (it = audience.begin(); it < audience.end(); ++it)
    {
        std::cout << (*it).toString() << "\n\n";
    }
}