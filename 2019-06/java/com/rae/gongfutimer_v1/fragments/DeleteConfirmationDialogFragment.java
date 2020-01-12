package com.rae.gongfutimer_v1.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.rae.gongfutimer_v1.R;

public class DeleteConfirmationDialogFragment extends DialogFragment
{
    public interface DeleteConfirmationDialogListener
    {
        public void onDialogResponseClick(boolean clickedPositive);
    }

    DeleteConfirmationDialogListener listener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // Verify host activity implements callback interface
        try
        {
            // instantiate listener so we can send events to host
            listener = (DeleteConfirmationDialogListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Must implement DeleteConfirmationDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // instantiate an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // chain together setter methods for dialog characteristics
        builder.setMessage(R.string.delete_confirmation_dialog)
                .setPositiveButton(R.string.delete_config, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // delete
                        //listener.onDialogPositiveClick(DeleteConfirmationDialogFragment.this);
                        listener.onDialogResponseClick(true);
                    }
                })
                .setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // cancel
                        //listener.onDialogNegativeClick(DeleteConfirmationDialogFragment.this);
                        listener.onDialogResponseClick(false);
                    }
                });
        // build it using create()
        return builder.create();
    }
}
