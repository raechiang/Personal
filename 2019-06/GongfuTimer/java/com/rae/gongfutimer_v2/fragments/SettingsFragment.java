package com.rae.gongfutimer_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.activities.LoadConfigActivity;
import com.rae.gongfutimer_v2.preferences.DeleteDialogPreference;

/**
 * This functions as the settings/preference screen. The settings are as follows: the default timer
 * increment amount can be adjusted; the application theme can be changed; the option to allow hex
 * color input for the icon can be enabled; there will be a feedback button; the default timers can
 * be restored; the default timers can be removed; and all timers can be deleted.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    /**
     * This supplies the prefernces for this fragment. It inflates the xml resource to the
     * preference hierarchy and sets listeners.
     * @param savedInstanceState
     * @param rootKey - The preference key to use as the root of the preference hierarchy
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();

        // Change theme
        Preference themePref = findPreference(getString(R.string.settings_theme_list_key));
        String value = sharedPref.getString(themePref.getKey(), "");
        setPreferenceSummary(themePref, value);
        themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    Intent intent = new Intent(getContext(), LoadConfigActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
        );

        // Change default increment
        Preference defaultIncPref = findPreference(getString(R.string.settings_default_increment_key));
        defaultIncPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                Toast error = Toast.makeText(getContext(), "Please input an integer within 1 to 300 (up to 5 minutes).", Toast.LENGTH_LONG);

                String defaultIncKey = getString(R.string.settings_default_increment_key);
                if (preference.getKey().equals(defaultIncKey))
                {
                    String incAmountString = (String) newValue;
                    try {
                        int increment = Integer.parseInt(incAmountString);
                        // Also don't want the value to be too large and can't be negative.
                        if (increment <= 0 || increment > 300)
                        {
                            error.show();
                            return false;
                        }
                    } catch (NumberFormatException nfe) {
                        error.show();
                        return false;
                    }
                }
                return true;
            }
        });
        String defIncVal = sharedPref.getString(defaultIncPref.getKey(), "");
        setPreferenceSummary(defaultIncPref, defIncVal);

        //  TODO: feedback button
        //  Restore Defaults
        Preference restoreDefaultsPref = findPreference(getString(R.string.settings_restore_defaults_key));
        restoreDefaultsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference preference)
            {
                Intent intent = new Intent(getContext(), LoadConfigActivity.class);
                intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.Action.RESTORE_DEFAULTS);
                startActivity(intent);
                return true;
            }
        });
        //  Remove Defaults
        Preference removeDefaultsPref = findPreference(getString(R.string.settings_remove_defaults_key));
        removeDefaultsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference preference)
            {
                Intent intent = new Intent(getContext(), LoadConfigActivity.class);
                intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.Action.REMOVE_DEFAULTS);
                startActivity(intent);
                return true;
            }
        });
    }

    /**
     * This handles displaying a dialog. This will show a dialog to confirm deletion of all of the
     * timer configurations.
     * @param preference
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment fragment;
        if (preference instanceof DeleteDialogPreference)
        {
            fragment = DeleteDialogPreferenceFragmentCompat.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), "androidx.preference.PreferenceDialogFragmentCompat.DIALOG");
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    /**
     * This creates the fragment.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is called when the fragment is no longer in use.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * When certain preferences have been modified, these changes need to be saved--namely, the
     * default increment amount, theme type, and hex input boolean.
     * @param sharedPreferences - The app preferences.
     * @param key - The identifier for the modified preference.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference preference = findPreference(key);
        if (preference != null)
        {
            if (key.equals(getString(R.string.settings_default_increment_key)))
            {
                String value = sharedPreferences.getString(preference.getKey(), "");

                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.settings_default_increment_key), value);
                editor.apply();

                setPreferenceSummary(preference, value);
            }
            else if (key.equals(getString(R.string.settings_theme_list_key)))
            {
                String value = sharedPreferences.getString(preference.getKey(), "");

                // want to save preference to file so other activities can access
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.settings_theme_list_key), value);
                editor.apply();

                setPreferenceSummary(preference, value);
            }
            else if (key.equals(getString(R.string.settings_hex_input_key)))
            {
                boolean wantHex = sharedPreferences.getBoolean(key, false);
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.settings_hex_input_key), wantHex);
                editor.apply();
            }
        }
    }

    /**
     * This will update the preference summary, which are the subtitle descriptions under each
     * preference.
     * @param preference - The preference whose summary needs to be changed.
     * @param value - Data that needed to update the summary.
     */
    private void setPreferenceSummary(Preference preference, String value)
    {
        if (preference instanceof ListPreference) // (theme)
        {
            if (value != null && !(value.isEmpty()))
            {
                ListPreference listPref = (ListPreference) preference;
                int index = listPref.findIndexOfValue(value);
                if (index >= 0)
                {
                    preference.setSummary(listPref.getEntries()[index]);
                }
            }
        }
        else if (preference instanceof EditTextPreference) // (increment amount)
        {
            if (value != null)
            {
                EditTextPreference editTextPref = (EditTextPreference) preference;
                preference.setSummary(value + " seconds");
            }
        }
    }
}
