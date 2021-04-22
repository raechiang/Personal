# Gongfu Timer (WIP)

<p>This is a timer app that has the ability to store many configurations of potentially multi-step timers with some visual customization. The app is not complete, but it is useable; mainly the design/visual parts that remain unfinished.</p>
<p>This timer is originally made for tea-making, in the eastern style of *gongfu*, which involves frequent and short steeps with high leaf-to-water ratios in small vessels. Later steeps are done over longer durations. If you are into tea-brewing, you probably also have a large arsenal of different leaves, so this app also saves multiple custom timer configurations, which is more convenient than memorizing all of the unique timings that every different batch of leaves deserves. This version of the tea timer allows control over the exact timer for every infusion step. Since each timer step can be manually set and is not restricted to linearly increasing an initial timer, you can vary the differential of each step, thereby expanding its use to more than just gongfu brewing. I use it for cleaning tools, for example, but you can use it for drip-brew coffee or HIIT--you can use it for anything that can make use of a short interval timer.</p>

## Using This App

### List of Timers

<p>Upon opening the app for the first time, a list of pre-made "default" timers will be displayed. These can be deleted or restored via the Settings button in the upper right-hand corner of the list screen. Each element of the list can be clicked on to start the timer, or it can be long-held to edit the timer. The vertical More dots in each row will open a drop-down menu, which allows you to Edit or Delete that configuration.</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss00_load.png" width="400">
  <figcaption><p>(The list of timer configurations. Dark theme.)</p></figcaption>
</figure>

### Running Timers

<p>If you click on a timer from the load screen, it will bring you to a screen where you can run the timer. Below is an example of one of the default timers: Black Large Leaf. The lower right-hand button is for Go/Pause, and the lower left-hand button is for Restarting the current step or moving to the next step if you are at 0 seconds remaining. The `<` and `>` buttons near the center can control the step number that you are on, which is helpful if, say, you accidentally terminate the app or come back later, or maybe you want to skip around for whatever reason. The Pencil button in the upper right-hand corner can be used to Edit the timer.</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss09_run_timer.png" width="400">
  <figcaption><p>(Running the Black Large Leaf timer. Clay theme.)</p></figcaption>
</figure>

### Editing Timers

<p>From the list screen, you may add a new timer using the `+` Floating Action Button in the bottom center of the screen, which will take you to an edit screen like so:</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss02_save_new.png" width="400">
  <figcaption><p>(Making a new "blank" unsaved timer. All fields have default values. Dark theme.)</p></figcaption>
</figure>

<p>From the list screen, if you long-hold a timer or select Edit from the view More vertical buttons, you will be able to edit the timer.</p>

<p><figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss01_save_existing.png" width="400">
  <figcaption>(Editing an existing timer. Light theme.)</figcaption>
</figure></p>

<p>You can change the name of a timer by entering text where the timer configuration's name or "Unnamed Timer" is. The icon's style and color can be changed by clicking the icon just below the name. The timer configuration can be marked as a Favorite timer by clicking the star to the right of the icon selection button, which will cause the timer configuration to sort to the top of the list. The values of a timer step can be modified by 1 second by using the `-` or `+` buttons surrounding the timer value, or it can be changed by opening a number pad dialog box by clicking the timer step's text (so, in the image depicted below, by clicking on the "00:01" text). The large `+` button at the bottom of the screen will add another step to the timer, which will automatically increment by some default amount that can be adjusted in the Settings. The `x` button at the right end of the timer step's row can delete that timer step. The lower left contains two Floating Action Buttons: a save button and a delete button (the latter of which is hidden if the timer does not exist in your saved configuration database). The lower right corner contains a Floating Action Button that runs this timer. As far as errors the user can cause, the only thing that they need to worry about is using a non-empty Name for the timer.</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss05_save_existing_icon_select.png" width="400">
  <figcaption><p>(Editing a timer configuration's icon. Hex input disabled. Dark theme.)</p></figcaption>
</figure>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss06_save_existing_icon_select_hex.png" width="400">
  <figcaption><p>(Editing a timer configuration's icon. Hex input enabled. Clay theme.)</p></figcaption>
</figure>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss03_save_existing_numpad.png" width="400">
  <figcaption><p>(Editing a timer step's value using the number pad. Light theme.)</p></figcaption>
</figure>

<p>Numbers for this are appended to the right end and shifts the previously-inputted values to the left. This input will prevent the user from trying to input a timer value outside of the range.</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss04_save_existing_numpad_err.png" width="400">
  <figcaption><p>(Editing a timer step's value using the number pad. Message toast appears and red exclamation appears, which can be clicked to display more information. Dark theme.)</p></figcaption>
</figure>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss07_save_existing_unsaved.png" width="400">
  <figcaption><p>(Attempting to navigate elsewhere without saving the timer configuration. Dark theme.)</p></figcaption>
</figure>

### Settings

<p>From the list screen, clicking the cog in the upper right in the toolbar opens the Settings screen. The increment amount mentioned earlier can be changed. The theme can be changed. Hex input for icon style can be enabled or disabled. There is a feedback button. Timer configurations can be changed in three ways here: default timers can be restored, default timers can be removed, or all timers can be deleted.</p>

<figure>
  <img src="https://github.com/raechiang/Personal/blob/master/2019-06/GongfuTimer/Screenshots/ss07_save_existing_unsaved.png" width="400">
  <figcaption><p>(Deleting all timers, which opens a confirmation dialog. Dark theme.)</p></figcaption>
</figure>

## Known Issues & Further Development

<p>Some basic functions still need to be implemented: alarms for the completion of a timer and the feedback button.</p>

<p>Visuals/design could also benefit from some uplifts.</p>
