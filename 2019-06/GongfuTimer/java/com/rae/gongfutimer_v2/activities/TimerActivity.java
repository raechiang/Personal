package com.rae.gongfutimer_v2.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.ui.TimerView;
import com.rae.gongfutimer_v2.utils.CountDownTimer;
import com.rae.gongfutimer_v2.utils.TimerConfig;

import java.util.Locale;

/**
 * This activity runs the timer configurations in seconds. Users can run, pause, or reset the timer,
 * and they can navigate through the steps if they want to. The toolbar has a button that allows the
 * user to edit the selected timer configuration. Pressing back from there, selecting delete, and
 * saving the timer will bring the user to the LoadConfigActivity.
 */
public class TimerActivity extends TimerConfigActivity
{
    // Constants
    /**
     * The interval to receive onTick callbacks.
     */
    private final static long COUNTDOWN_INTERVAL = 10;
    /**
     * For the conversion of seconds to milliseconds.
     */
    private final static long SEC_TO_MIL_CONVERSION = 1000;

    // Timer Fields
    /**
     * The timer itself.
     */
    private CountDownTimer timer;
    /**
     * The position of the timer configuration in the data set.
     */
    private int receivedPosition;
    /**
     * The active timer configuration.
     */
    private TimerConfig timerConfig;
    /**
     * An array containing all of the timer steps from the {@link #timerConfig}.
     */
    private long[] timersInSecs;
    /**
     * The current iteration (step) of the timer configuration.
     */
    private int iteration;
    /**
     * The total number of timer steps.
     */
    private int totalIterations;
    /**
     * A boolean indicating that the timer has started a countdown.
     */
    private boolean timerStarted;

    // Views
    /**
     * The view that displays the current timer step.
     */
    private TimerView timerView;
    /**
     * The text that displays the iteration number.
     */
    private TextView iterationOfTextView;
    /**
     * This button runs and pauses the timer.
     */
    private FloatingActionButton timerButton;
    /**
     * This button will restart the timer. If the timer is already running, it will restart and
     * pause the timer within the iteration. If the timer has reached the end of its final
     * iteration, it will restart the timer from the first step.
     */
    private FloatingActionButton restartButton;

    /**
     * This initializes the activity. It sets up the UI and will process the intent.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        applyTheme();

        setContentView(R.layout.activity_timer);

        init();
    }

    /**
     * This sets up the action bar at the top.
     */
    @Override
    protected void setActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * This method processes intent and populates the UI with relevant information.
     */
    @Override
    protected void init()
    {
        setActionBar();
        processIntent();
        timerStarted = false;

        if (timerConfig != null)
        {
            // get needed views
            TextView nameTextView = (TextView) findViewById(R.id.timer_name_text_view);
            timerView = (TimerView) findViewById(R.id.timer_remaining_layout);
            iterationOfTextView = (TextView) findViewById(R.id.timer_iteration_current_text_view);
            TextView totalReps = (TextView) findViewById(R.id.timer_iteration_total_text_view);
            timerButton = (FloatingActionButton) findViewById(R.id.timer_timer_floating_action_button);
            restartButton = (FloatingActionButton) findViewById(R.id.timer_restart_floating_action_button);

            ImageView imageView = (ImageView) findViewById(R.id.timer_image_view_placeholder);
            imageView.setImageResource(timerConfig.getIconStyle());
            imageView.setColorFilter(timerConfig.getIconColor());

            setListeners();

            // Text Setting
            // set name
            nameTextView.setText(timerConfig.getName());
            // set iterations
            iteration = 0;
            totalIterations = timerConfig.getTotalInfusions();
            totalReps.setText(String.format(Locale.US, "%d", totalIterations));
            // set current timer
            timersInSecs = timerConfig.getTimerSeconds();
            updateNumberTextViews();

            // Button Setting
            if (timerConfig.getTotalInfusions() <= 0)
            {
                timerButton.setEnabled(false);
            }
        }
        else
        {
            // just gonna go back because no timer was found, somehow
            onSupportNavigateUp();
        }
    }

    /**
     * This method grabs the information needed when this Activity is launched. This Activity needs
     * the timer configuration and its position in the data set.
     */
    @Override
    protected void processIntent()
    {
        Intent intent = getIntent();
        receivedPosition = intent.getIntExtra(getString(R.string.load_position_extra), SaveConfigActivity.NEW_UNSAVED_TIMER);
        timerConfig = (TimerConfig) intent.getSerializableExtra(getString(R.string.load_timer_extra));
    }

    /**
     * This method sets up the listeners for various Views (the timer, restart, next iteration, and
     * previous iteration buttons).
     */
    @Override
    protected void setListeners()
    {
        ImageButton prevIterationButton = (ImageButton) findViewById(R.id.timer_prev_image_button);
        ImageButton nextIterationButton = (ImageButton) findViewById(R.id.timer_next_image_button);

        timerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(timerStarted))
                {
                    updateNumberTextViews();
                    makeTimer(timersInSecs[iteration]);
                    timer.start();
                    timerStarted = true;
                    changeTimerButtonImage(R.drawable.ic_pause_black_24dp);
                }
                else
                {
                    pauseResume();
                }
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                restartButtonClick(v);
            }
        });
        prevIterationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // decrease iteration
                if (iteration != 0)
                {
                    --iteration;
                    // adjust timer
                    restartButtonClick(v);
                    timerButton.setEnabled(true);
                }
            }
        });
        nextIterationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // increase iteration
                if (iteration < totalIterations - 1)
                {
                    ++iteration;
                    // adjust timer
                    restartButtonClick(v);
                }
            }
        });
    }

    /**
     * This method starts a new LoadConfigActivity instead of navigating backwards and ends this
     * Activity.
     * @return - True.
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
     * This simply updates the {@link #timerView} as the countdown timer ticks.
     * @param currentTimerInSecs - The current countdown time in seconds.
     */
    private void updateTimerTextView(long currentTimerInSecs)
    {
        long minutes = currentTimerInSecs / 60;
        long seconds = currentTimerInSecs % 60;
        timerView.setTimerTextViews(String.format(Locale.US, "%02d", minutes).toCharArray(),
                String.format(Locale.US, "%02d", seconds).toCharArray());
    }

    /**
     * This method updates the {@link #iterationOfTextView} with the current iteration number and
     * the {@link #timerView} by calling {@link #updateTimerTextView(long)}.
     */
    private void updateNumberTextViews()
    {
        iterationOfTextView.setText(String.format(Locale.US, "%d", iteration + 1));
        updateTimerTextView(timersInSecs[iteration]);
    }

    /**
     * This method sets the image resource of the timer floating action button. It should display a
     * timer, play, or pause symbol.
     * @param id - The id of the resource.
     */
    private void changeTimerButtonImage(int id)
    {
        timerButton.setImageResource(id);
    }

    /**
     * This method makes a new CountDownTimer and assigns it to {@link #timer}.
     * @param secs - The current timer in seconds.
     */
    private void makeTimer(long secs)
    {
        timer = new CountDownTimer((secs * SEC_TO_MIL_CONVERSION), COUNTDOWN_INTERVAL)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                // if remaining is (0,1) s, integer division would make 0,
                // but it is not yet truly 0. So timer should be Math.ceil. Most important value in
                // a countdown is when it reaches 0
                double untilFinished = millisUntilFinished / 1000.0;
                updateTimerTextView((long) (Math.ceil(untilFinished)));
            }

            @Override
            public void onFinish()
            {
                updateTimerTextView(0);
                ++iteration;
                timerStarted = false;
                if (iteration == totalIterations)
                {
                    // done
                    changeTimerButtonImage(R.drawable.ic_timer_black_24dp);
                    timerButton.setEnabled(false);
                    // TODO: timer notification alarm
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
                    r.play();
                }
                else
                {
                    // not done
                    changeTimerButtonImage(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        };
    }

    /**
     * This method decides whether to pause or resume and carries out the opposite of the current
     * state.
     */
    private void pauseResume()
    {
        if (timer.getmPaused())
        {
            // if paused
            timer.resume();
            changeTimerButtonImage(R.drawable.ic_pause_black_24dp);
        }
        else
        {
            // if not paused
            timer.pause();
            changeTimerButtonImage(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    /**
     * This method handles restarting the current timer.
     * @param view
     */
    private void restartButtonClick(View view)
    {
        Log.i("TIMERACTIVITY", "reset wanted");
        if (timerStarted)
        {
            // cancel timer and restart
            timer.cancel();
            updateNumberTextViews();
            timerStarted = false;
        }
        else
        {
            if (iteration != totalIterations)
            {
                updateNumberTextViews();
            }
            else
            {
                // starting over from the beginning
                iteration = 0;
                timerButton.setEnabled(true);
                updateNumberTextViews();
            }
        }

        if (iteration == 0)
        {
            changeTimerButtonImage(R.drawable.ic_timer_black_24dp);
        }
        else
        {
            changeTimerButtonImage(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    /**
     * Inflates the menu to the action bar.
     * @param menu
     * @return - True.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.timer_actions, menu);
        return true;
    }
    /**
     * There is only one option in TimerActivity, which goes to a SaveConfigActivity with the
     * current timerConfig and allows the user to make changes to the timerConfig.
     * @param item - The selected item.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.saveOrEdit:
                // save or edit the timer
                selectSaveEditTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method is called when the edit button is selected from the action bar. It starts a new
     * SaveConfigActivity so the user can make changes to the current timerConfig.
     */
    private void selectSaveEditTimer()
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), timerConfig);
        intent.putExtra(getString(R.string.load_position_extra), receivedPosition);

        startActivity(intent);

        finish();
    }
}
