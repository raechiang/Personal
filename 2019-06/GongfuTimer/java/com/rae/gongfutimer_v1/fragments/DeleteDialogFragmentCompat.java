package com.rae.gongfutimer_v1.fragments;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.Log;

import com.rae.gongfutimer_v1.preferences.DeleteDialogPreference;

public class DeleteDialogFragmentCompat extends PreferenceDialogFragmentCompat
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

    public static DeleteDialogFragmentCompat newInstance(Preference preference)
    {
        final DeleteDialogFragmentCompat fragment = new DeleteDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}