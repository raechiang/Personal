package com.rae.gongfutimer_v2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rae.gongfutimer_v2.R;

/**
 * This class is for activities that will have the application color theme applied.
 */
public abstract class ThemedActivity extends AppCompatActivity
{
    /**
     * This is the name of the theme.
     */
    private String currentTheme;

    /**
     * This method picks the appropriate theme to set the activity to.
     */
    protected void applyTheme()
    {
        String themeName = getSharedPrefTheme();

        if (!(getSharedPrefTheme().isEmpty()))
        {
            if (themeName.equals(getString(R.string.pref_theme_value_dark)))
            {
                setTheme(R.style.DarkTheme);
            }
            else if (themeName.equals(getString(R.string.pref_theme_value_light)))
            {
                setTheme(R.style.LightTheme);
            }
            else if (themeName.equals(getString(R.string.pref_theme_value_clay)))
            {
                setTheme(R.style.ClayTheme);
            }
            currentTheme = themeName;
        }
    }

    /**
     * This method checks the setting to get the theme that has been selected.
     * @return - The strong retrieved from the Preferences or an empty string.
     */
    private String getSharedPrefTheme()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.settings_theme_list_key), "");
    }

    /**
     * This method checks to see if the theme matches the theme selected in the Preferences.
     * @return - True if it matches the preferences, false if not.
     */
    protected boolean isThemeCurrent()
    {
        if (currentTheme != null)
        {
            if (currentTheme.equals(getSharedPrefTheme()))
            {
                return true;
            }
            return false;
        }
        return true;
    }
}
