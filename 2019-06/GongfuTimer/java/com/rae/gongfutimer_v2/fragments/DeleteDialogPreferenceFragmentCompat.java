package com.rae.gongfutimer_v2.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.rae.gongfutimer_v2.preferences.DeleteDialogPreference;

public class DeleteDialogPreferenceFragmentCompat extends PreferenceDialogFragmentCompat
{
    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        Log.i("DDFC", "dialog closed " + positiveResult);

        if (positiveResult)
        {
            DialogPreference preference = getPreference();
            if (preference instanceof DeleteDialogPreference)
            {
                ((DeleteDialogPreference) preference).makeDeleteAllLoadConfig();
            }
        }
    }

    public static DeleteDialogPreferenceFragmentCompat newInstance(Preference preference)
    {
        final DeleteDialogPreferenceFragmentCompat fragment = new DeleteDialogPreferenceFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}
