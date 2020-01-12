package com.rae.gongfutimer_v1.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.utils.CountDownTimer;
import com.rae.gongfutimer_v1.utils.TimeConfig;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity
{
    private CountDownTimer timer;
    private TextView timeRemainingMinTextView;
    private TextView timeRemainingSecTextView;
    private TextView incrementTextView;
    private TextView iterationOfTextView;
    private FloatingActionButton pauseplayButton;
    private int receivedPosition;
    private boolean timerStarted;
    private long t0;
    private long td;
    private int r;
    private int i;

    private final static long COUNTDOWN_INTERVAL = 10;

    private TimeConfig timeConfig;

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

        timeConfig = (TimeConfig) intent.getSerializableExtra(getString(R.string.timer_extra));
        t0 = timeConfig.getTimerInitial();
        td = timeConfig.getTimerIncrement();
        r = timeConfig.getTotalInfusions();

        i = 0;
        timerStarted = false;

        TextView nameTextView = (TextView) findViewById(R.id.timer_name_text_view);
        timeRemainingMinTextView = (TextView) findViewById(R.id.timer_remaining_min_text_view);
        timeRemainingSecTextView = (TextView) findViewById(R.id.timer_remaining_sec_text_view);
        incrementTextView = (TextView) findViewById(R.id.timer_increment_text_view);
        iterationOfTextView = (TextView) findViewById(R.id.timer_iteration_of_text_view);
        pauseplayButton = (FloatingActionButton) findViewById(R.id.timer_pause_play_button);
        ImageButton plusIterationImageButton = (ImageButton) findViewById(R.id.timer_iteration_plus);
        ImageButton minusIterationImageButton = (ImageButton) findViewById(R.id.timer_iteration_minus);
        if (timeConfig.getTotalInfusions() <= 0)
        {
            pauseplayButton.setEnabled(false);
        }
        plusIterationImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // increase i
                if (i < r - 1)
                {
                    ++i;
                    // adjust timer
                    resetButtonClick(v);
                }
            }
        });
        minusIterationImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // decrease i
                if (i != 0)
                {
                    --i;
                    // adjust timer
                    resetButtonClick(v);
                }
            }
        });

        nameTextView.setText(timeConfig.getName());
        updateNumberTextViews(t0, i, td);

        TextView notesTextView = (TextView) findViewById(R.id.timer_notes_text_view);
        String notes = timeConfig.getNotes();
        if (notes != null)
        {
            notesTextView.setText(notes);
            if (notes.length() == 0)
            {
                notesTextView.setText(getString(R.string.text_no_notes));
            }
        }
        else
        {
            notesTextView.setText(getString(R.string.text_no_notes));
        }
        notesTextView.setVisibility(View.GONE);

        // -1 if no position given; this means that the config passed has not been saved
        receivedPosition = intent.getIntExtra(getString(R.string.position_extra), SaveConfigActivity.NEW_UNSAVED_TIMER);
        //Log.i("TIMERACTIVITY", "Received position: " + receivedPosition);

        ImageView timerImageView = (ImageView) findViewById(R.id.timer_image_view);
        timerImageView.setImageResource(R.drawable.ic_circle_test);
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

    private void updateNumberTextViews(long t0, int i, long td)
    {
        long ti = t0 + (i * td);
        updateTimeRemainingTextView(ti);
        char[] tdChars = generateMMSSChars(td);
        incrementTextView.setText(tdChars, 0, tdChars.length);
        iterationOfTextView.setText(String.format(Locale.US, "%d / %d", i + 1, r));
    }

    private void updateTimeRemainingTextView(long timeInSecs)
    {
        char[] tiChars = generateMMSSChars(timeInSecs);
        //char[] tiChars = generateMMSSChars(6100); // testing for 101:40

        // MM is variable length but SS always last two, colon is 3 from end
        timeRemainingMinTextView.setText(tiChars, 0, tiChars.length - 3);
        timeRemainingSecTextView.setText(tiChars, tiChars.length - 2, 2);
    }

    public void toggleNotes(View view)
    {
        Button toggleShow = (Button) findViewById(R.id.timer_toggle_notes_button);
        TextView notesTextView = (TextView) findViewById(R.id.timer_notes_text_view);
        if (notesTextView.getVisibility() == View.GONE)
        {
            // if gone, show
            notesTextView.setVisibility(View.VISIBLE);
            toggleShow.setText(getString(R.string.hide_notes));
        }
        else if (notesTextView.getVisibility() == View.VISIBLE)
        {
            // if visible, hide
            notesTextView.setVisibility(View.GONE);
            toggleShow.setText(getString(R.string.show_notes));
        }
    }

    private char[] generateMMSSChars(long timeInSecs)
    {
        long min = timeInSecs / 60;
        long sec = timeInSecs % 60;
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(min / 10);
        sBuilder.append(min % 10);
        sBuilder.append(':');
        sBuilder.append(sec / 10);
        sBuilder.append(sec % 10);
        return sBuilder.toString().toCharArray();
    }

    public void pauseplayButtonClick(View view)
    {
        if (!(timerStarted))
        {
            updateNumberTextViews(t0, i, td);
            long millisInFuture = timeConfig.getTimerInitial() + (i * timeConfig.getTimerIncrement());
            makeTimer(millisInFuture * 1000);
            timer.start();
            timerStarted = true;
            switchPausePlayImage(R.drawable.ic_pause_black_24dp);
        }
        else
        {
            pauseResume();
        }
    }

    private void switchPausePlayImage(int id)
    {
        pauseplayButton.setImageResource(id);
    }

    private void makeTimer(long millisInFuture)
    {
        timer = new CountDownTimer(millisInFuture, COUNTDOWN_INTERVAL) {
            public void onTick(long millisUntilFinished)
            {
                // if remaining is (0,1) s, integer division would make 0,
                // but it is not yet truly 0. So timer should be Math.ceil. Most important value in
                // a countdown is when it exactly reaches 0
                double untilFinished = millisUntilFinished / 1000.0;
                updateTimeRemainingTextView((long) (Math.ceil(untilFinished)));
            }
            public void onFinish()
            {
                updateTimeRemainingTextView(0);
                ++i;
                timerStarted = false;
                switchPausePlayImage(R.drawable.ic_play_arrow_black_24dp);
                if (i == r)
                {
                    pauseplayButton.setEnabled(false);
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
            switchPausePlayImage(R.drawable.ic_pause_black_24dp);
        }
        else
        {
            // if not paused
            timer.pause();
            //blinkTimeRemaining();
            switchPausePlayImage(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    public void resetButtonClick(View view)
    {
        switchPausePlayImage(R.drawable.ic_play_arrow_black_24dp);
        if (timerStarted)
        {
            // have to cancel and restart
            timer.cancel();
            updateNumberTextViews(t0, i, td);
            timerStarted = false;
        }
        else
        {
            if (i != r)
            {
                updateNumberTextViews(t0, i, td);
            }
            else
            {
                // full reset
                i = 0;
                pauseplayButton.setEnabled(true);
                updateNumberTextViews(t0, i, td);
            }
        }
    }

    private void blinkTimeRemaining()
    {
        // TODO: This needs a lot of work still! I don't know if this is the best way to animate it either.
        final Handler handler = new Handler();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int timeToBlink = 700; // Change this value; higher is slower
                try {
                    Thread.sleep(timeToBlink);
                } catch (InterruptedException ie) {
                    // stuff
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (timeRemainingMinTextView.getVisibility() == View.VISIBLE)
                        {
                            timeRemainingMinTextView.setVisibility(View.INVISIBLE);
                            timeRemainingSecTextView.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            timeRemainingMinTextView.setVisibility(View.VISIBLE);
                            timeRemainingSecTextView.setVisibility(View.VISIBLE);
                        }
                        blinkTimeRemaining();
                    }
                });
            }
        }).start();
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
                // wants to save or edit timer
                selectSaveEditTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectSaveEditTimer()
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.timer_extra), timeConfig);
        intent.putExtra(getString(R.string.position_extra), receivedPosition);

        startActivity(intent);

        finish();
    }
}
