/*
MovieScheduling

Given a file containing the names of people and their weekly availability, this
program will print time slots of a given length for a given minimum number of
participants. It will query a file name, the duration of a movie in minutes,
and the minimum number of participants available desired to display a time
window.

This program was built around an existing scheduling input on the internet
called When2Meet because that was what my friends and I were using to collect
all of our availabilities. Thus, the time availability slots follow a 15-minute
increment, since that is what when2meet uses. This can be clearly seen in the
DayAvailability class files, and more information about it can be found there.

Text File Requirements:
The text file provided must adhere to a specific written format. The first line
of an entry requires the string "name:", with any case. The remainder of this
name line will be treated as the entire name string of the Viewer. Subsequent
lines will be the days of the week, in any order, and these will serve as the
availability data for the Viewer with the last preceding name. Different Viewer
entries are separated by the "name:" keyword, and as such, any repeated days of
weeks under each name  will add to the availability; there is no overwriting or
removing of availability. Each time interval for a day must be given exactly as
"[<start>,<end>)" in 24-hour format. All times will be assumed as under the same
timezone. These are a few acceptable examples:
name: my name
Sunday: [1200,2330)
Monday [1800,2000)[2100,2300)
tuesday [900,1300),[ 800,1600)
saturday: [2000 ,2330)
Friday [500,2400)
Name: another example
thursday
thursday [0,2400)
The Viewer entries do not need to be separated with more than one new line, as
long as the "name:" keyword to start the entry is present, but as many empty
lines can exist between new lines. Please include a final ending new line to
indicate the end of the file.

Notably, it only prints valid time windows. I may add the ability to find
combinations of two days to allow all audience members to participate (if
possible) at some point, since that would help me find coverage for my friends
with all their varying schedules.
*/
#include "AvailabilityScheduler.h"

#include <iostream>

void test();
void runUserInputMode();

int main()
{
    //test();

    runUserInputMode();

    return 0;
}

/*
This is just so that I can have parameters already set to avoid inputting them
manually every time.
*/
void test()
{
    AvailabilityScheduler scheduler;
    //std::string fileName("sample00.txt");
    std::string fileName("sample01.txt");
    //std::string fileName("sample02same.txt");
    
    if (scheduler.parseFile(fileName))
    {
        scheduler.findSchedulesTest();
    }
}

/*
This is a simple and quick method that queries the user for the file name,
movie duration in minutes, and the minimum number of participants that a time
window should have.
*/
void runUserInputMode()
{
    std::string fileName;
    int minMovieDuration = 0;
    int minimumParticipants = 0;
    bool wantContinue = true;
    AvailabilityScheduler scheduler;

    while (!(scheduler.parseFile(fileName)))
    {
        std::cout << "Enter file name: ";
        std::cin >> fileName;
    }
    while (minMovieDuration <= 0 || minMovieDuration > 1440)
    {
        std::cout << "Enter movie duration in minutes: ";
        std::cin >> minMovieDuration;
    }
    while (minimumParticipants <= 0)
    {
        std::cout << "Enter minimum number of participants for a time slot: ";
        std::cin >> minimumParticipants;
    }
    scheduler.findSchedules(minMovieDuration, minimumParticipants);

    std::cout << "\nThank you. Goodbye.";
}