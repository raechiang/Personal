package com.rae.gongfutimer_v1.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.SwitchPreference;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.activities.LoadConfigActivity;
import com.rae.gongfutimer_v1.preferences.DeleteDialogPreference;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        Preference restoreDefaultsPref = findPreference(getString(R.string.restore_defaults_key));
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
        Preference removeDefaultsPref = findPreference(getString(R.string.remove_defaults_key));
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
        final SwitchPreferenceCompat themeSwitch = (SwitchPreferenceCompat) findPreference("temp_theme_select");
        themeSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent intent = new Intent(getContext(), LoadConfigActivity.class);
                intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.Action.NONE);
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (themeSwitch.isChecked())
                {
                    Log.i("SETTINGSFRAGMENT", "DARK on");
                    editor.putString(getString(R.string.theme_selection_key), getString(R.string.dark_theme_name));
                }
                else
                {
                    Log.i("SETTINGSFRAGMENT", "DEFAULT on");
                    editor.putString(getString(R.string.theme_selection_key), getString(R.string.default_theme_name));
                }
                editor.commit();
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment fragment;

        if(preference instanceof DeleteDialogPreference)
        {
            fragment = DeleteDialogFragmentCompat.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
