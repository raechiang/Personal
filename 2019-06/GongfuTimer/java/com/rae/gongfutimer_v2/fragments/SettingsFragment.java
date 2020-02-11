package com.rae.gongfutimer_v2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.activities.LoadConfigActivity;
import com.rae.gongfutimer_v2.preferences.DeleteDialogPreference;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        // Preference Listeners
        //  TODO: feedback button
        //  TODO: Maybe want to be able to customize automatic increment (default is +00:05)
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

    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment fragment;
        if (preference instanceof DeleteDialogPreference)
        {
            fragment = DeleteDialogPreferenceFragmentCompat.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), "androidx.preference.PreferenceDialogFragmentCompat.DIALOG"); // TODO: is this tag right
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
