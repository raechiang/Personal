package com.rae.gongfutimer_v1.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.fragments.BlankInputButtonsFragment;
import com.rae.gongfutimer_v1.fragments.BlankDisplayFragment;
import com.rae.gongfutimer_v1.fragments.BlankIncrementFragment;
import com.rae.gongfutimer_v1.fragments.BlankRepetitionsFragment;
import com.rae.gongfutimer_v1.fragments.BlankStartFragment;
import com.rae.gongfutimer_v1.interfaces.QuickTimerString;
import com.rae.gongfutimer_v1.utils.InfusionQuickTimerString;
import com.rae.gongfutimer_v1.utils.MinSecQuickTimerString;
import com.rae.gongfutimer_v1.utils.TimeConfig;

import java.util.UUID;

public class BlankConfigActivity extends AppCompatActivity
    implements BlankInputButtonsFragment.OnButtonClickedListener
{
    // IDs: integers that will be assigned to the three steps of the Quick Timer input
    /**
     * Used for {@link #fragments} and {@link #quickTimerValues} to correspond to the
     * BlankStartFragment and value for the Start Timer (the first/initial timer).
     */
    private static final int START_ID = 0;
    /**
     * Used for {@link #fragments} and {@link #quickTimerValues} to correspond to the
     * BlankIncrementFragment and value for the Increment Time (amount of time to add to the timer
     * at each iteration beyond the initial timer).
     */
    private static final int INCREMENT_ID = 1;
    /**
     * Used for {@link #fragments} and {@link #quickTimerValues} to correspond to the
     * BlankRepetitionsFragment and value for the Total Infusions (the total number of repetitions
     * to conduct the timer).
     */
    private static final int INFUSIONS_ID = 2;

    // Arrays: hold the BlankDisplayFragments and QuickTimerStrings
    /**
     * Holds the three BlankDisplayFragments. The first step, fragments[0], corresponds to
     * {@link #START_ID} and contains a BlankStartFragment. The second step, fragments[1],
     * corresponds to {@link #INCREMENT_ID} and contains a BlankIncrementFragment. The third step,
     * fragments[2], corresponds to {@link #INFUSIONS_ID} and contains a BlankRepetitionsFragment.
     */
    BlankDisplayFragment[] fragments;
    /**
     * Holds the three QuickTimerStrings. The first value, quickTimerValues[0], corresponds to
     * {@link #START_ID} and contains a MinSecQuickTimerString representing the Start Timer. The
     * second value, quickTimerValues[1], corresponds to {@link #INCREMENT_ID} and contains a
     * MinSecQuickTimerString representing the Increment Time. The third value, quickTimerValues[2],
     * corresponds to {@link #INFUSIONS_ID} and contains an InfusionQuickTimerString representing
     * the Total Number of Infusions.
     */
    QuickTimerString[] quickTimerValues;

    // Overrides
    /**
     * When the Activity is created, it will set up the views and the fields. Here the content view,
     * toolbar, class fields, and fragments are set.
     * @param savedInstanceState a bundle for setting up anything, passed from the activator.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_config);

        // Toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        init();

        // add BlankStartFragment
        fragments[START_ID].setArguments(getBundle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.blank_input_content, fragments[START_ID]);

        // add BlankInputButtonsFragment
        BlankInputButtonsFragment blankNumberInputFragment = new BlankInputButtonsFragment();
        fragmentTransaction.add(R.id.blank_num_input_content, blankNumberInputFragment);

        // Commit
        fragmentTransaction.commit();
    }

    /**
     * Starts the LoadConfigActivity and finishes this one when the Action Bar's arrow is pressed.
     * @return boolean true
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        startActivity(intent);
        finish();
        return true;
    }

    /**
     * Sets up Listeners.
     * @param fragment the fragment that is being attached
     */
    @Override
    public void onAttachFragment(Fragment fragment)
    {
        if (fragment instanceof BlankInputButtonsFragment)
        {
            BlankInputButtonsFragment blankNumberInputFragment = (BlankInputButtonsFragment) fragment;
            blankNumberInputFragment.setOnButtonClickedListener(this);
        }
    }

    /**
     * Pops through the Back Stack of Fragments or finishes this activity and returns to the
     * LoadConfigActivity. The behavior depends on the current active Fragment. If the active
     * Fragment is the BlankStartFragment, it will leave the activity. Otherwise, it will travel
     * through the Back Stack, updating other Views accordingly.
     */
    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.blank_input_content);

        if (!(fragment instanceof BlankStartFragment))
        {
            if (fragment instanceof BlankDisplayFragment)
            {
                // Goes through the Fragment BackStack
                super.onBackPressed();
                // Since this changes the Active Fragment, it needs to update the Views accordingly
                BlankDisplayFragment blankDisplayFragment = (BlankDisplayFragment) fragment;
                int fragmentId = getFragmentIdentifier(blankDisplayFragment);
                updateProgressViews(fragmentId - 1);
                updateNextButtonText(getString(R.string.next_button));
                updateNextButtonEnabled( fragmentId - 1);
                updatePrevButtonEnabled(fragmentId - 1);
            }
        }
        else
        {
            // fragment is BlankStartFragment
            // Go home...
            Intent intent = new Intent(this, LoadConfigActivity.class);

            startActivity(intent);
            finish();
        }
    }

    /**
     * Starts the TimerActivity. Called when the Start Button is pressed, which can be seen in
     * {@link #onNavClicked(int)}.
     * @param view the current view
     */
    public void startTimerActivity(View view)
    {
        if (!(quickTimerValues[START_ID].isAllZero()) && !(quickTimerValues[INFUSIONS_ID].isAllZero()))
        {
            Intent intent = new Intent(this, TimerActivity.class);
            long startTimerInSeconds = ((MinSecQuickTimerString) quickTimerValues[START_ID]).getSecondsLong();
            long incrementTimeInSeconds = ((MinSecQuickTimerString) quickTimerValues[INCREMENT_ID]).getSecondsLong();
            int totalInfusions = ((InfusionQuickTimerString) quickTimerValues[INFUSIONS_ID]).getInfusionsInt();
            //if (startTimerInSeconds > 0 && startTimerInSeconds < 6000 && totalInfusions > 0 && totalInfusions <= 99)
            if (checkRange(startTimerInSeconds, incrementTimeInSeconds, totalInfusions))
            {
                TimeConfig timer = new TimeConfig(UUID.randomUUID(),
                        "Unsaved Quick Timer",
                        startTimerInSeconds,
                        incrementTimeInSeconds,
                        totalInfusions,
                        "");
                intent.putExtra(getString(R.string.timer_extra), timer);
                startActivity(intent);
            }
        }
        else
        {
            Log.e("BLANKCONFIGACTIVITY", "Start timer and total number of infusions (repetitions) cannot be zero.");
        }
    }

    // Implementations for the Interface BlankInputButtonsFragment.OnButtonClickedListener
    /**
     * Modifies a value when a numerical button has been pressed. It will first determine the ID of
     * the fragment that is currently viewed by {@link #getFragmentIdentifier(BlankDisplayFragment)}.
     * Then, it will shift the corresponding {@link #quickTimerValues} with
     * {@link #updateValues(int, char)}. Then, the {@link #fragments} will be updated as well,
     * including the buttons (if necessary) by calling {@link #updateFragmentViews(int)}. Finally,
     * it will update the top progress bar with {@link #updateProgressViews(int)}.
     * @param value the value of the number corresponding to the button pressed
     */
    @Override
    public void onNumberClicked(char value)
    {
        // A number was clicked
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_input_content);
        if (fragment != null)
        {
            if (fragment instanceof BlankDisplayFragment)
            {
                BlankDisplayFragment blank = (BlankDisplayFragment) fragment;
                // retrieve the ID of the fragment
                int fragmentId = getFragmentIdentifier(blank);
                if (fragmentId != -1)
                {
                    // update the timer values
                    updateValues(fragmentId, value);
                    // update the views
                    updateFragmentViews(fragmentId);
                    updateProgressViews(fragmentId);
                }
            }
        }
    }

    /**
     * Changes which BlankDisplayFragment of {@link #fragments} is currently being displayed, or
     * starts the TimerActivity with {@link #startTimerActivity(View)}}. There are two navigation
     * buttons: a Previous Button and a Next (Start) Button. The Previous Button acts the same way
     * as an {@link #onBackPressed()} if the active Fracment is not a BlankStartFragment. The
     * BlankStartFragment's back does not work since it is the first step.
     * @param id the ID of the button that was clicked
     */
    @Override
    public void onNavClicked(int id)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.blank_input_content);

        if (id == R.id.blank_prev_button)
        {
            if (!(fragment instanceof BlankStartFragment))
            {
                onBackPressed();
            }
        }
        else if (id == R.id.blank_next_button)
        {
            // Go forward
            if (fragment instanceof BlankDisplayFragment)
            {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                BlankDisplayFragment blankFragment = (BlankDisplayFragment) fragment;
                int fragmentId = getFragmentIdentifier(blankFragment);
                // Clicked next, so we want fragmentId + 1 to be active
                // EXCEPT if we are already at the end fragmentId = 2--instead, start Timer
                if (fragmentId < INFUSIONS_ID)
                {
                    // 0=start, 1=increment
                    // give it bundle of current values for its Views to set
                    fragments[fragmentId + 1].setArguments(getBundle());
                    // change fragment
                    fragmentTransaction.replace(R.id.blank_input_content, fragments[fragmentId + 1]);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    // update Progress area's Views
                    updateProgressViews(fragmentId + 1);
                    if (fragmentId + 1 == INFUSIONS_ID)
                    {
                        // change Next Button text if headed to step 3 of 3
                        updateNextButtonText(getString(R.string.start_button));
                    }
                    // make sure the Next Button is set properly
                    updateNextButtonEnabled(fragmentId + 1);
                    updatePrevButtonEnabled(fragmentId + 1);
                }
                else if (fragmentId == INFUSIONS_ID)
                {
                    // start TimerActivity
                    startTimerActivity(getCurrentFocus());
                }
            }
        }
    }

    /**
     * Modifies a value when the Clear Button or Backspace Button is pressed. It first determines
     * the active fragment's ID with {@link #getFragmentIdentifier(BlankDisplayFragment)}. Then, it
     * will {@link #updateValues(int, int)}, passing in the active fragment ID and its button ID.
     * Then the Views will be updated as well: {@link #updateFragmentViews(int)} and
     * {@link #updateProgressViews(int)}.
     * @param buttonId
     */
    @Override
    public void onClearBackspaceClicked(int buttonId)
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_input_content);

        if (fragment != null)
        {
            if (fragment instanceof BlankDisplayFragment)
            {
                BlankDisplayFragment blank = (BlankDisplayFragment) fragment;
                int fragmentId = getFragmentIdentifier(blank);
                if (fragmentId != -1)
                {
                    // update the values
                    updateValues(fragmentId, buttonId);
                    updateFragmentViews(fragmentId);
                    updateProgressViews(fragmentId);
                }
            }
        }
    }

    /**
     * Initializes the class's fields, {@link #fragments} and {@link #quickTimerValues}.
     */
    private void init()
    {
        fragments = new BlankDisplayFragment[3];
        quickTimerValues = new QuickTimerString[3];

        fragments[START_ID] = new BlankStartFragment();
        fragments[INCREMENT_ID] = new BlankIncrementFragment();
        fragments[INFUSIONS_ID] = new BlankRepetitionsFragment();
        quickTimerValues[START_ID] = new MinSecQuickTimerString();
        quickTimerValues[INCREMENT_ID] = new MinSecQuickTimerString();
        quickTimerValues[INFUSIONS_ID] = new InfusionQuickTimerString();
    }

    // Functions that update Values and Views
    /**
     * Shifts a value indicated by an ID in {@link #quickTimerValues} forward by the given input.
     * @param fragmentId the ID for the fragment whose value is being modified
     * @param input the input that append itself to the right after shifting current values left
     */
    private void updateValues(int fragmentId, char input)
    {
        quickTimerValues[fragmentId].shiftForwardBy(input);
    }
    /**
     * Backspaces or clears a value indicated by an ID in {@link #quickTimerValues}.
     * @param fragmentId the ID for the fragment whose value is being modified
     * @param buttonId the ID of either the clear button or the backspace button
     */
    private void updateValues(int fragmentId, int buttonId)
    {
        if (buttonId == R.id.blank_clear_button)
        {
            // Makes a value all zero
            quickTimerValues[fragmentId].zeroAll();
        }
        else if (buttonId == R.id.blank_backspace_button)
        {
            // Shifts everything backwards (to the right) once and places a zero at the beginning
            quickTimerValues[fragmentId].shiftBackwardOnce();
        }
    }
    /**
     * Updates views of {@link #fragments} to correspond with stored {@link #quickTimerValues}.
     * Calls {@link #updateNextButtonEnabled(int)} to ensure that the Next button is only clickable
     * depending on the current state of values.
     * @param fragmentId the ID for the fragment whose value is being modified
     */
    private void updateFragmentViews(int fragmentId)
    {
        // update BlankDisplayFragment TextViews
        fragments[fragmentId].updateTextViews(
                quickTimerValues[fragmentId].getCharArray(),
                QuickTimerString.PLACE_VALUES);

        // update buttons from BlankInputButtonsFragment
        updateNextButtonEnabled(fragmentId);
    }
    /**
     * Enables or disables the Next Button. It will only be disabled when the following conditions
     * are met: (1) the fragment is for the Start Timer and the Start Timer value is 0, meaning that
     * quickTimerValues[START_ID]=0000, or (2) the fragment is for the Number of Infusions and the
     * Number of Infusions value is 0, meaning that quickTimerValues[INFUSIONS_ID]=00. Otherwise,
     * the Next Button is enabled. This is to prevent the user from making a timer with no time to
     * count down and with 0 repetitions.
     * @param fragmentId the ID for the current fragment
     */
    private void updateNextButtonEnabled(int fragmentId)
    {
        Button nextButton = findViewById(R.id.blank_next_button);
        if (fragmentId == START_ID || fragmentId == INFUSIONS_ID)
        {
            // Next Button only disabled if user is inputting Start Timer and Total Infusions
            if (quickTimerValues[fragmentId].isAllZero())
            {
                // Their values cannot be zero because it's pointless to count down from zero and
                // you can't start a timer 0 times
                nextButton.setEnabled(false);
            }
            else
            {
                // Nonzero is good to go!
                nextButton.setEnabled(true);
            }
        }
        else if (fragmentId == INCREMENT_ID)
        {
            // User may increment timer by 0, so can always navigate to the Next
            nextButton.setEnabled(true);
        }
    }

    /**
     * Enables or disables the Previous Button. It should always be enabled except when the Start
     * timer input fragment is active.
     * @param fragmentId
     */
    private void updatePrevButtonEnabled(int fragmentId)
    {
        Button backButton = findViewById(R.id.blank_prev_button);
        if (fragmentId == START_ID)
        {
            backButton.setEnabled(false);
        }
        else
        {
            backButton.setEnabled(true);
        }
    }
    /**
     * Sets the text of the Next Button to the given String. This is meant to provide clarity that
     * the final input step (Total Infusions) will "start" the timer, whereas the prior two steps
     * will only navigate you to their "next" steps.
     * @param text the string to set the Next Button with
     */
    private void updateNextButtonText(String text)
    {
        Button nextButton = (Button) findViewById(R.id.blank_next_button);
        nextButton.setText(text);
    }
    /**
     * Updates the progress view across the top of the screen with the current values. It will only
     * update the current fragment's values (as that is the only one that can be changed at the time
     * anyway) and the ProgressBar at the very top.
     * @param fragmentId the ID for the current fragment
     */
    private void updateProgressViews(int fragmentId)
    {
        // perhaps progress bar should be separated since it only really needs to be changed if the
        // user navigates forward or back.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.blank_progress_bar);
        int progress = 0;
        // data array will be used to set the TextViews
        char[] data = quickTimerValues[fragmentId].getCharArray();
        if (fragmentId == START_ID)
        {
            // Retrieve Start Timer TextViews
            TextView progT0MinTextView = (TextView) findViewById(R.id.blank_progress_t0_min);
            TextView progT0SecTextView = (TextView) findViewById(R.id.blank_progress_t0_sec);
            // Set Start Timer TextViews
            progT0MinTextView.setText(data,
                    0,
                    QuickTimerString.PLACE_VALUES);
            progT0SecTextView.setText(data,
                    QuickTimerString.PLACE_VALUES,
                    QuickTimerString.PLACE_VALUES);
            // ProgressBar should be set to 1/3 of the way
            progress = 33;
        }
        else if (fragmentId == INCREMENT_ID)
        {
            // Retrieve Increment Time TextViews
            TextView progTdMinTextView = (TextView) findViewById(R.id.blank_progress_td_min);
            TextView progTdSecTextView = (TextView) findViewById(R.id.blank_progress_td_sec);
            // Set Increment Time TextViews
            progTdMinTextView.setText(data,
                    0,
                    QuickTimerString.PLACE_VALUES);
            progTdSecTextView.setText(data,
                    QuickTimerString.PLACE_VALUES,
                    QuickTimerString.PLACE_VALUES);
            // ProgressBar should be set to 2/3 of the way
            progress = 66;
        }
        else if (fragmentId == INFUSIONS_ID)
        {
            // Retrieve Total Infusions (Repetitions) TextView
            TextView progRepsTextView = (TextView) findViewById(R.id.blank_progress_reps);
            // Set Total Infusions (Repetitions) TextView
            progRepsTextView.setText(data,
                    0,
                    QuickTimerString.PLACE_VALUES);
            // ProgressBar should be set to 3/3 of the way
            progress = 100;
        }
        // Set ProgressBar
        progressBar.setProgress(progress);
    }

    // Input Validity Check
    /**
     * Determines whether or not the values are within valid range, as follows:
     * <p>Start timer in (0,6000)</p>
     * <p>Increment amount in [0,6000)</p>
     * <p>Total infusions (repetitions) in (0,99]</p>
     * This is because there are two place values for inputs throughout, so mm:ss display must be
     * maintained. "99 minutes and 59 seconds" is the greatest possible value.
     * @param startInSeconds the start timer, in seconds
     * @param incInSeconds the increment amount, in seconds
     * @param totalReps the total number of infusions
     * @return true if it is in range, false if it is not
     */
    private boolean checkRange(long startInSeconds, long incInSeconds, int totalReps)
    {
        return ((startInSeconds > 0 && startInSeconds < 6000)
                && (incInSeconds >= 0 && incInSeconds < 6000)
                && (totalReps > 0 && totalReps <= 99));
    }

    // Get Methods
    /**
     * Determines which of the three IDs ought to be returned (of {@link #START_ID},
     * {@link #INCREMENT_ID}, and {@link #INFUSIONS_ID}), which is dependent on the type of
     * BlankDisplayFragment.
     * @param fragment the fragment that needs to be identified
     * @return the ID corresponding to the type of fragment
     */
    private int getFragmentIdentifier(BlankDisplayFragment fragment)
    {
        if (fragment instanceof BlankStartFragment)
        {
            return START_ID;
        }
        else if (fragment instanceof BlankIncrementFragment)
        {
            return INCREMENT_ID;
        }
        else if (fragment instanceof BlankRepetitionsFragment)
        {
            return INFUSIONS_ID;
        }
        return -1;
    }
    /**
     * Generates a Bundle for BlankDisplayFragments according to the currently stored values. The
     * Bundle is meant to be used in {@link com.rae.gongfutimer_v1.fragments.BlankDisplayFragment
     * #onCreateView(LayoutInflater, ViewGroup, Bundle)} to ensure the Views are up-to-date. Bundles
     * should be used whenever a fragment transaction occurs with the BlankDisplayFragments.
     * @return a bundle containing information about the three quick timer values
     */
    private Bundle getBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putCharArray(getString(R.string.blank_start_key),
                quickTimerValues[START_ID].getCharArray());
        bundle.putCharArray(getString(R.string.blank_increment_key),
                quickTimerValues[INCREMENT_ID].getCharArray());
        bundle.putCharArray(getString(R.string.blank_infusions_key),
                quickTimerValues[INFUSIONS_ID].getCharArray());
        return bundle;
    }
}
