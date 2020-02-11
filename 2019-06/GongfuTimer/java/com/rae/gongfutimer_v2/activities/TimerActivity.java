package com.rae.gongfutimer_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.ui.TimerView;
import com.rae.gongfutimer_v2.utils.CountDownTimer;
import com.rae.gongfutimer_v2.utils.TimerConfig;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity
{
    // Constant
    private final static long COUNTDOWN_INTERVAL = 10;
    private final static long SEC_TO_MIL_CONVERSION = 1000;

    // Timer Fields
    private CountDownTimer timer;
    private int receivedPosition;
    private TimerConfig timerConfig;
    private long[] timersInSecs;
    private int iteration;
    private int totalIterations;
    private boolean timerStarted;

    // Views
    private TimerView timerView;
    private TextView iterationOfTextView;
    private FloatingActionButton timerButton;
    private FloatingActionButton restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        init();
    }

    private void init()
    {
        Intent intent = getIntent();
        receivedPosition = intent.getIntExtra(getString(R.string.load_position_extra), SaveConfigActivity.NEW_UNSAVED_TIMER);
        timerConfig = (TimerConfig) intent.getSerializableExtra(getString(R.string.load_timer_extra));
        timerStarted = false;
        if (timerConfig != null)
        {
            // get needed views
            TextView nameTextView = (TextView) findViewById(R.id.timer_name_text_view);
            timerView = (TimerView) findViewById(R.id.timer_remaining_layout);
            iterationOfTextView = (TextView) findViewById(R.id.timer_iteration_current_text_view);
            TextView totalReps = (TextView) findViewById(R.id.timer_iteration_total_text_view);
            timerButton = (FloatingActionButton) findViewById(R.id.timer_timer_floating_action_button);
            ImageButton prevIterationButton = (ImageButton) findViewById(R.id.timer_prev_image_button);
            ImageButton nextIterationButton = (ImageButton) findViewById(R.id.timer_next_image_button);
            restartButton = (FloatingActionButton) findViewById(R.id.timer_restart_floating_action_button);

            // Text Setting
            // set name
            nameTextView.setText(timerConfig.getName());
            // set iterations
            iteration = 0;
            totalIterations = timerConfig.getTotalInfusions();
            //iterationOfTextView.setText(iteration + 1);
            totalReps.setText(String.format(Locale.US, "%d", totalIterations));
            // set current timer
            timersInSecs = timerConfig.getTimerSeconds();
            //updateTimerTextView(timersInSecs[iteration]);
            updateNumberTextViews();

            // TODO: set image and anything else that might be added
            //ImageView timerImageTest = (ImageView) findViewById(R.id.timer_image_view_placeholder);
            //timerImageTest.setColorFilter(getResources().getColor(R.color.colorGreenTest));
            //^That does indeed change the color of the vector

            // Button Setting
            if (timerConfig.getTotalInfusions() <= 0)
            {
                timerButton.setEnabled(false);
            }
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
        else
        {
            // just gonna go back because no timer was found, somehow
            onSupportNavigateUp();
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        startActivity(intent);
        finish();
        return true;
    }

    private void updateTimerTextView(long currentTimerInSecs)
    {
        long minutes = currentTimerInSecs / 60;
        long seconds = currentTimerInSecs % 60;
        timerView.setTimerTextViews(String.format(Locale.US, "%02d", minutes).toCharArray(),
                String.format(Locale.US, "%02d", seconds).toCharArray());
    }

    private void updateNumberTextViews()
    {
        iterationOfTextView.setText(String.format(Locale.US, "%d", iteration + 1));
        updateTimerTextView(timersInSecs[iteration]);
    }

    private void changeTimerButtonImage(int id)
    {
        timerButton.setImageResource(id);
    }

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
                    changeTimerButtonImage(R.drawable.ic_timer_black_24dp);
                    timerButton.setEnabled(false);
                }
                else
                {
                    changeTimerButtonImage(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        };
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.timer_actions, menu);
        return true;
    }

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

    private void selectSaveEditTimer()
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), timerConfig);
        intent.putExtra(getString(R.string.load_position_extra), receivedPosition);

        startActivity(intent);

        finish();
    }
}
