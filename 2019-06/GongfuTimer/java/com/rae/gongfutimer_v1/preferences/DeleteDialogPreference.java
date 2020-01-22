package com.rae.gongfutimer_v1.preferences;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.activities.LoadConfigActivity;
import com.rae.gongfutimer_v1.utils.TimeConfig;

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
