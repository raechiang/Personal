package com.rae.gongfutimer_v2.preferences;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.activities.LoadConfigActivity;

/**
 * This is a dialog for the option that deletes all of the timers in the preferences/settings. The
 * application only prompts for this option because this one may have irreversible effects, as it
 * permanently deletes customized timers.
 */
public class DeleteDialogPreference extends DialogPreference
{
    /**
     * The constructor.
     * @param context
     * @param attributeSet
     */
    public DeleteDialogPreference(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    /**
     * This will send the command to the LoadConfigActivity to delete all timers.
     */
    public void makeDeleteAllLoadConfig()
    {
        Intent intent = new Intent(getContext(), LoadConfigActivity.class);
        intent.putExtra(getContext().getString(R.string.load_action_extra), LoadConfigActivity.Action.DELETE_ALL);
        getContext().startActivity(intent);
    }
}
