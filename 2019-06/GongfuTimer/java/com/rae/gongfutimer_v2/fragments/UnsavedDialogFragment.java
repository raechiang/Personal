package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

/**
 * This dialog is displayed to notify the user that the timer configuration being edited has not
 * been saved when trying to navigate away from the edit screen.
 */
public class UnsavedDialogFragment extends DialogFragment
{
    /**
     * The listener for this fragment.
     * @see UnsavedDialogListener
     */
    private UnsavedDialogListener listener;

    /**
     * This is called after the fragment is associated with the SaveConfigActivity. It will try to
     * set this class's listener.
     * @param context - The activity that the fragment is associated with.
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (UnsavedDialogListener) context;
        } catch (ClassCastException cce)
        {
            throw new ClassCastException("Must implement UnsavedDialogListener");
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
        int messageStringId = this.getArguments().getInt(getString(R.string.unsaved_string_id_key));
        if (messageStringId == R.string.run_unsaved_dialog)
        {
            // user attempted to start the timer without saving modifications
            builder.setMessage(getString(messageStringId))
                    .setPositiveButton(R.string.run_unsaved_positive, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // delete
                            listener.onUnsavedRunDialogResponseClick(true);
                        }
                    })
                    .setNegativeButton(R.string.run_unsaved_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // cancel
                            listener.onUnsavedRunDialogResponseClick(false);
                        }
                    });
        }
        else if (messageStringId == R.string.return_unsaved_dialog)
        {
            // user attempted to go to the LoadConfigActivity without saving
            builder.setMessage(getString(messageStringId))
                    .setPositiveButton(R.string.run_unsaved_positive, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            listener.onUnsavedReturnDialogResponseClick(true);
                        }
                    })
                    .setNegativeButton(R.string.run_unsaved_cancel, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            listener.onUnsavedReturnDialogResponseClick(false);
                        }
                    });
        }
        // build it using create()
        return builder.create();
    }

    /**
     * This listener must handle the two navigation cases: running the timer configuration and
     * returning to the load timer configuration screen.
     */
    public interface UnsavedDialogListener
    {
        /**
         * This is called when the user attempts to run the timer configuration without having
         * saved modifications made to it.
         * @param clickedPositive - True if the user confirms to run the timer without saving.
         */
        public void onUnsavedRunDialogResponseClick(boolean clickedPositive);

        /**
         * This is called when the user attempts to go to the LoadConfigActivity without having
         * saved modifications made to the timer configuration.
         * @param clickedPositive - True if the user confirms to run the timer without saving.
         */
        public void onUnsavedReturnDialogResponseClick(boolean clickedPositive);
    }
}
