package com.rae.gongfutimer_v1.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        SettingsFragment settingsFragment = new SettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_content, settingsFragment)
                .commit();
    }
}
