package com.rae.gongfutimer_v2.activities;

/**
 * This abstract Activity outlines the methods shared across the timer activities in this
 * application and extends ThemedActivity, which all relate to setting up the activities.
 */
public abstract class TimerConfigActivity extends ThemedActivity
{
    /**
     * This method should instantiate fields and set up the UI.
     */
    protected abstract void init();

    /**
     * This method should set up the activity's action bar.
     */
    protected abstract void setActionBar();

    /**
     * This method should set listeners for views as needed.
     */
    protected abstract void setListeners();

    /**
     * This method should get information intents. It processes received intents.
     */
    protected abstract void processIntent();
}
