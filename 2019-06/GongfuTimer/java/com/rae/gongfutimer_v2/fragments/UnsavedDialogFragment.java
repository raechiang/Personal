package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

public class UnsavedDialogFragment extends DialogFragment
{
    private UnsavedDialogListener listener;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // instantiate an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // chain together setter methods for dialog characteristics
        int messageStringId = this.getArguments().getInt(getString(R.string.unsaved_string_id_key));
        if (messageStringId == R.string.run_unsaved_dialog)
        {
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
        /*String message = this.getArguments().getString(getString(R.string.unsaved_string_key));
        if (message.equals(getString(R.string.run_unsaved_dialog)))
        {
            builder.setMessage(message)
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
        else if (message.equals(getString(R.string.return_unsaved_dialog)))
        {
            builder.setMessage(message)
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
        */
        // build it using create()
        return builder.create();
    }

    public interface UnsavedDialogListener
    {
        public void onUnsavedRunDialogResponseClick(boolean clickedPositive);
        public void onUnsavedReturnDialogResponseClick(boolean clickedPositive);
    }
}
