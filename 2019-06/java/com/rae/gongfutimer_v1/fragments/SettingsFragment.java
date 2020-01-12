package com.rae.gongfutimer_v1.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.preferences.DeleteDialogPreference;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
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
