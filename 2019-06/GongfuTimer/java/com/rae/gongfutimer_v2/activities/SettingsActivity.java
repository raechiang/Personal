package com.rae.gongfutimer_v2.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.fragments.SettingsFragment;

/**
 * This is the settings activity, which is split between two categories: settings for the general
 * application and settings for the timer configuration data set. Users can change the default
 * increment amount. They can change the theme to Dark, Light, or Clay. They can toggle an option
 * to allow for hex value input for the icon's color. They can restore default timers, remove
 * default timers, or delete all timers.
 */
public class SettingsActivity extends ThemedActivity
{
    /**
     * This initializes the activity. It sets up the UI.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        applyTheme();

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        SettingsFragment settingsFragment = new SettingsFragment();

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.settings_content, settingsFragment)
            .commit();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
