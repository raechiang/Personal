package com.rae.gongfutimer_v2.preferences;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.activities.LoadConfigActivity;

public class DeleteDialogPreference extends DialogPreference
{
    public DeleteDialogPreference(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void makeDeleteAllLoadConfig()
    {
        Intent intent = new Intent(getContext(), LoadConfigActivity.class);
        intent.putExtra(getContext().getString(R.string.load_action_extra), LoadConfigActivity.Action.DELETE_ALL);
        getContext().startActivity(intent);
    }
}
