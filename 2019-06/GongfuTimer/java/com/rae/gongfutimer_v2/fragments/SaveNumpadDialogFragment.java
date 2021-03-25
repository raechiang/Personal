package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.activities.SaveConfigActivity;

import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * This DialogFragment displays a numpad that the user can use to manually input a two-digit timer
 * (as mm:ss).
 */
public class SaveNumpadDialogFragment extends DialogFragment
{
    /**
     * This shows the minutes for the timer.
     */
    private TextView timerMinsTextView;
    /**
     * This shows the seconds for the timer.
     */
    private TextView timerSecsTextView;
    /**
     * The listener for this fragment.
     * @see SaveNumpadDialogListener
     */
    private SaveNumpadDialogListener listener;
    /**
     * The step number of this timer that is being edited.
     */
    private int position;

    /**
     * This initializes the fragment.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * This is called after the fragment is associated with the SaveConfigActivity. It will try to
     * set this class's listener.
     * @param context - The activity that the fragment is associated with.
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            listener = (SaveNumpadDialogListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Must implement SaveNumpadDialogListener");
        }
    }

    /**
     * This creates and returns the view. The "save numpad dialog" will be inflated, which has the
     * TextViews to display the minutes and seconds and a numpad for input.
     * @param inflater - Inflates views in the fragment.
     * @param container - The parent view for the fragment UI to attach to.
     * @param savedInstanceState
     * @return - The fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.save_numpad_dialog, container, false);

        return view;
    }

    /**
     * This sets up the UI of the Views, filling them with initial information.
     * @param view - The View from onCreateView.
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Minutes and seconds text views
        timerMinsTextView = (TextView) view.findViewById(R.id.save_numpad_timer_min_text_view);
        timerSecsTextView = (TextView) view.findViewById(R.id.save_numpad_timer_sec_text_view);
        try {
            char[] timerData = this.getArguments().getCharArray(getString(R.string.save_numpad_timer_key));
            setTimerTextViews(timerData);
            position = this.getArguments().getInt(getString(R.string.save_numpad_position_key));
        } catch (NullPointerException npe) {
            Log.e("SAVENUMPADDFRAG", "Bundle did not contain timer data for instantiation");
            timerMinsTextView.setText(String.format(Locale.US, "%02d", 0));
            timerSecsTextView.setText(String.format(Locale.US, "%02d", 0));
            position = -1;
        }

        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.save_numpad_action);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (item.getItemId() == R.id.save_numpad_apply_action)
                {
                    char[] timerData = getTimerChars();
                    if (isAllZero(timerData))
                    {
                        setErrorResponse(view);
                        Toast.makeText(getActivity(), "A nonzero timer value must be provided.", Toast.LENGTH_LONG).show();
                    }
                    else if (isNotInRange(timerData))
                    {
                        setErrorResponse(view);
                        Toast.makeText(getActivity(), "The timer value cannot exceed 99 minutes and 59 seconds.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        listener.onApplyClick(getTimerChars(), position);
                        dismiss();
                    }
                }
                return false;
            }
        });

        // Theme
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String themeName = sharedPref.getString(getString(R.string.settings_theme_list_key), "");
        if (themeName.equals(getString(R.string.pref_theme_value_clay)) || themeName.equals(getString(R.string.pref_theme_value_dark)))
        {
            // needs to be light
            toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        }
        else
        {
            // needs to be dark
            toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        // Buttons
        setButtons(view);
    }

    /**
     * This sets up the listeners for the number, clear, and backspace buttons.
     * @param view
     */
    private void setButtons(View view)
    {
        int[] numButtonIds = {R.id.save_numpad_n0_button,
                R.id.save_numpad_n1_button, R.id.save_numpad_n2_button, R.id.save_numpad_n3_button,
                R.id.save_numpad_n4_button, R.id.save_numpad_n5_button, R.id.save_numpad_n6_button,
                R.id.save_numpad_n7_button, R.id.save_numpad_n8_button, R.id.save_numpad_n9_button};
        for (int id : numButtonIds)
        {
            Button numButton = (Button) view.findViewById(id);
            numButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (view instanceof Button)
                    {
                        Button button = (Button) view;

                        shiftTimerLeftBy(button.getText().charAt(0));
                    }
                }
            });
        }

        Button clearButton = view.findViewById(R.id.save_numpad_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clearTimer();
            }
        });
        ImageButton backspaceButton = view.findViewById(R.id.save_numpad_backspace_button);
        backspaceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shiftTimerRight();
            }
        });
    }

    /**
     * This shifts the timer display left by one and appends the input character to the right end.
     * For example, ABCD would become BCDi.
     * @param input - The number button input.
     */
    private void shiftTimerLeftBy(char input)
    {
        char[] timerData = getTimerChars();
        int size = timerData.length;
        System.arraycopy(timerData, 1, timerData, 0, size - 1);
        timerData[size - 1] = input;
        setTimerTextViews(timerData);
    }

    /**
     * This zeroes the timer display. For example, ABCD would become 0000.
     */
    private void clearTimer()
    {
        timerMinsTextView.setText(String.format(Locale.US, "%02d", 0));
        timerSecsTextView.setText(String.format(Locale.US, "%02d", 0));
    }

    /**
     * This shifts the timer display to the right by one and appends a zero to the left end. For
     * example, ABCD becomes 0ABC.
     */
    private void shiftTimerRight()
    {
        char[] timerData = getTimerChars();
        int size = timerData.length;
        System.arraycopy(timerData, 0, timerData, 1, size - 1);
        timerData[0] = '0';
        setTimerTextViews(timerData);
    }

    /**
     * This converts the TextView strings to an array of characters.
     * @return - An array of characters of the minutes and seconds of the timer.
     */
    private char[] getTimerChars()
    {
        return (timerMinsTextView.getText().toString()
                + timerSecsTextView.getText().toString()).toCharArray();
    }

    /**
     * This changes the minutes and seconds displays to the characters from timerData.
     * @param timerData - The minutes and seconds of the timer as characters (mmss).
     */
    private void setTimerTextViews(char[] timerData)
    {
        int size = timerData.length;
        timerMinsTextView.setText(timerData, 0, size / 2);
        timerSecsTextView.setText(timerData, size / 2, size / 2);
    }

    /**
     * This simply checks if all of the timer contains only zeroes.
     * @param timerData - The minutes and seconds of the timer as characters (mmss).
     * @return - True if the timer contains all zeroes, false if not.
     */
    private boolean isAllZero(char[] timerData)
    {
        for (char c : timerData)
        {
            if (c != '0')
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks the upper bound of the timer. The timer cannot exceed 5999 seconds.
     * @param timerData - The minutes and seconds of the timer as characters (mmss).
     * @return - True if the timer is greater than 5999 seconds.
     */
    private boolean isNotInRange(char[] timerData)
    {
        long timerCharLong = Long.parseLong(new String(timerData));
        long min = timerCharLong / 100;
        long sec = timerCharLong % 100;
        return ((60 * min) + sec > 5999);
    }

    /**
     * This shows an error if there was an issue with the timer. To be exact, it shows an ImageView
     * of a circled exclamation mark that can be clicked to display more information about the bad
     * input.
     * @param view
     */
    private void setErrorResponse(View view)
    {
        ImageView errorImage = view.findViewById(R.id.save_numpad_timer_error_image_view);
        errorImage.setVisibility(View.VISIBLE);

        errorImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.save_numpad_error_popup, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAsDropDown(view, 0, 0);
                popupView.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent event)
                    {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
    }

    /**
     * This starts the dialog and sets up its dimensions.
     */
    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    /**
     * This listener is for the dialog when the Apply button is clicked so that changes can be made
     * to the timer.
     */
    public interface SaveNumpadDialogListener
    {
        /**
         * This is called when the user chooses to Apply changes.
         * @param timerData - The minutes and seconds of the timer as characters (mmss).
         * @param position - The step number of this timer that is being edited.
         */
        public void onApplyClick(char[] timerData, int position);
    }
}
