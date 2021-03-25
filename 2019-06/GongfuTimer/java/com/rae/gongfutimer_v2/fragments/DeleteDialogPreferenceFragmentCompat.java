package com.rae.gongfutimer_v2.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.rae.gongfutimer_v2.preferences.DeleteDialogPreference;

/**
 * This class is the dialog that will pop up when the user is in the Settings and requests to delete
 * all timer configurations.
 */
public class DeleteDialogPreferenceFragmentCompat extends PreferenceDialogFragmentCompat
{
    /**
     * This method checks the user's confirmation status upon closing the dialog. If they confirmed
     * to delete the configurations, then the preference will proceed to carry out the deletion.
     * @param positiveResult - True if the user confirmed to delete, false otherwise.
     */
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

    /**
     * This method creates and returns a new instance of this dialog fragment.
     * @param preference
     * @return
     */
    public static DeleteDialogPreferenceFragmentCompat newInstance(Preference preference)
    {
        final DeleteDialogPreferenceFragmentCompat fragment = new DeleteDialogPreferenceFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}
