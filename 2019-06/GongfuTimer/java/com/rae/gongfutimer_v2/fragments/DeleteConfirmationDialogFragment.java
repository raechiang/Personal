package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

public class DeleteConfirmationDialogFragment extends DialogFragment
{
    public interface DeleteConfirmationDialogListener
    {
        public void onDeleteDialogResponseClick(boolean clickedPositive);
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
                        listener.onDeleteDialogResponseClick(true);
                    }
                })
                .setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // cancel
                        //listener.onDialogNegativeClick(DeleteConfirmationDialogFragment.this);
                        listener.onDeleteDialogResponseClick(false);
                    }
                });
        // build it using create()
        return builder.create();
    }
}
