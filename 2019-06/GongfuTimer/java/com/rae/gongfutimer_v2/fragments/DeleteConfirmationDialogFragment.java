package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

/**
 * This class is the dialog that will pop up when the application needs to prompt the user to
 * confirm that they would like to delete a timer configuration.
 */
public class DeleteConfirmationDialogFragment extends DialogFragment
{
    /**
     * This interface functions as a listener for the dialog fragment. No changes should be made
     * unless the user positively responded to the delete confirmation.
     */
    public interface DeleteConfirmationDialogListener
    {
        /**
         * This method should occur when the user clicks to confirm to delete a timer.
         * @param clickedPositive - The boolean that says whether they clicked positive or not.
         */
        public void onDeleteDialogResponseClick(boolean clickedPositive);
    }

    /**
     * This is the listener for the dialog.
     */
    DeleteConfirmationDialogListener listener;

    /**
     * This method is called when the fragment is attached to the context. It instantiates the
     * listener.
     * @param context
     */
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

    /**
     * This method builds the dialog container. It builds an alert dialog with its message and the
     * confirm and cancel buttons and returns this as a Dialog.
     * @param savedInstanceState
     * @return - A new alert dialog.
     */
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
