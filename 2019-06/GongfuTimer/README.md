# Gongfu Timer (WIP)
This is a timer app that has the ability to store many configurations of potentially multi-step timers with some visual customization. The app is not complete, but it is useable; mainly the design/visual parts that remain unfinished.
This timer is originally made for tea-making, in the eastern style of *gongfu*, which involves frequent and short steeps with high leaf-to-water ratios in small vessels. Later steeps are done over longer durations. If you are into tea-brewing, you probably also have a large arsenal of different leaves, so this app also saves multiple custom timer configurations, which is more convenient than memorizing all of the unique timings that every different batch of leaves deserves. This version of the tea timer allows control over the exact timer for every infusion step. Since each timer step can be manually set and is not restricted to linearly increasing an initial timer, you can vary the differential of each step, thereby expanding its use to more than just gongfu brewing. I use it for cleaning tools, for example, but you can use it for drip-brew coffee or HIIT--you can use it for anything that can make use of a short interval timer.
## Using This App
Upon opening the app for the first time, a list of pre-made "default" timers will be displayed. These can be deleted or restored via the Settings button in the upper right-hand corner of the list screen. Each element of the list can be clicked on to start the timer, or it can be long-held to edit the timer. The vertical More dots in each row will open a drop-down menu, which allows you to Edit or Delete that configuration.

<img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss00_load.png" width="500">

If you click on a timer from the Load screen, it will bring you to a screen where you can run the timer. Below is an example of one of the default timers: Black Large Leaf. The lower right-hand button is for Go/Pause, and the lower left-hand button is for Restarting the current step or moving to the next step if you are at 0 seconds remaining. The `<` and `>` buttons near the center can control the step number that you are on, which is helpful if, say, you accidentally terminate the app or come back later, or maybe you want to skip around for whatever reason. The Pencil button in the upper right-hand corner can be used to Edit the timer.

<img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss09_run_timer.png" width="500">

From the list screen, you may add a new timer using the `+` Floating Action Button in the bottom center of the screen, which will take you to an edit screen like so:

<img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss02_save_new.png" width="500">

All fields have default values as you can see. You can change the name of a timer by entering text where "Unnamed Timer" is. The icon's style and color can be changed by clicking the icon just below the name. The timer configuration can be marked as a Favorite timer by clicking the star to the right of the icon selection button, which will cause the timer configuration to sort to the top of the list. The values of a timer step can be modified by 1 second by using the `-` or `+` buttons surrounding the timer value, or it can be changed by opening a number pad dialog box by clicking the timer step's text (so, in the image depicted below, by clicking on the "00:01" text). The large `+` button at the bottom of the screen will add another step to the timer, which will automatically increment by some default amount that can be adjusted in the Settings. The lower left contains two Floating Action Buttons: a save button and a delete button (the latter of which is hidden if the timer does not exist in your saved configuration database). The lower right corner contains a Floating Action Button that runs this timer. As far as errors the user can cause, the only thing that they need to worry about is using a non-empty Name for the timer.

From the Load screen, if you long-hold a timer or select Edit from the view More vertical buttons, you will be able to edit the timer.

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss01_save_existing.png" width="500">
  <figcaption>Editing an existing timer. Clay theme.</figcaption>
</figure>
