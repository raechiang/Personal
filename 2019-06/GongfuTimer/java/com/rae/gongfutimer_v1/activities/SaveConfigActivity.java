package com.rae.gongfutimer_v1.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.fragments.DeleteConfirmationDialogFragment;
import com.rae.gongfutimer_v1.interfaces.QuickTimerString;
import com.rae.gongfutimer_v1.utils.MinSecQuickTimerString;
import com.rae.gongfutimer_v1.utils.TimeConfig;

import java.util.Locale;

import static android.view.View.FOCUS_DOWN;

public class SaveConfigActivity extends AppCompatActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener
{
    public final static int NEW_UNSAVED_TIMER = -1;

    private int position;
    private TimeConfig timeConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_config);

        // Toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();

        timeConfig = (TimeConfig) intent.getSerializableExtra(getString(R.string.timer_extra));
        position = intent.getIntExtra(getString(R.string.position_extra), NEW_UNSAVED_TIMER); // default is -1. Maybe helpful for error checking

        Log.i("SAVECONFIGACTIVITY", "Received position: " + position);

        init();
    }

    /**
     * Starts the LoadConfigActivity and finishes this one when the Action Bar's arrow is pressed.
     * @return boolean true
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    private void init()
    {
        setButtons();
        setVariableFields();
    }

    private void setButtons()
    {
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        if (position == -1)
        {
            // came from BlankConfigActivity; unsaved
            deleteButton.setVisibility(View.GONE);
        }
        else
        {
            // came from LoadConfigActivity; saved before
            deleteButton.setVisibility(View.VISIBLE);
        }

        Button showMoreButton = (Button) findViewById(R.id.show_more_save_fields_button);
        showMoreButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout saveMoreFieldsLayout = (LinearLayout) findViewById(R.id.save_more_fields);
                if (v instanceof Button)
                {
                    Button currentButton = (Button) v;
                    if (saveMoreFieldsLayout.getVisibility() == View.GONE)
                    {
                        // If we cannot see the extra fields, show the fields and change button
                        // string to "hide"
                        saveMoreFieldsLayout.setVisibility(View.VISIBLE);
                        currentButton.setText(getString(R.string.hide_more_button));
                    }
                    else
                    {
                        // If we can see the extra fields, hide the fields and change button string
                        // to "show"
                        currentButton.setText(getString(R.string.show_more_button));
                        saveMoreFieldsLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setVariableFields()
    {
        EditText t0MinEditText = (EditText) findViewById(R.id.save_t0_min_edit_text);
        EditText t0SecEditText = (EditText) findViewById(R.id.save_t0_sec_edit_text);
        EditText tdMinEditText = (EditText) findViewById(R.id.save_td_min_edit_text);
        EditText tdSecEditText = (EditText) findViewById(R.id.save_td_sec_edit_text);
        EditText repsEditText = (EditText) findViewById(R.id.save_reps_edit_text);

        addTwoInputTextChangedListener(t0MinEditText);
        addTwoInputTextChangedListener(t0SecEditText);
        addTwoInputTextChangedListener(tdMinEditText);
        addTwoInputTextChangedListener(tdSecEditText);
        addTwoInputTextChangedListener(repsEditText);

        if (timeConfig == null)
        {
            // make new
            timeConfig = new TimeConfig();
            // set favorite
            Switch favSwitch = (Switch) findViewById(R.id.save_fav_switch);
            favSwitch.setChecked(timeConfig.getIsFavorite());
            setFocus(t0MinEditText);
        }
        else
        {
            // load fields
            // set start timer
            char[] startTimerChars = MinSecQuickTimerString.getTimerString(timeConfig.getTimerInitial());
            t0MinEditText.setText(startTimerChars, 0, QuickTimerString.PLACE_VALUES);
            t0SecEditText.setText(startTimerChars, QuickTimerString.PLACE_VALUES, QuickTimerString.PLACE_VALUES);
            // set increment time
            char[] incrementTimeChars = MinSecQuickTimerString.getTimerString(timeConfig.getTimerIncrement());
            tdMinEditText.setText(incrementTimeChars, 0, QuickTimerString.PLACE_VALUES);
            tdSecEditText.setText(incrementTimeChars, QuickTimerString.PLACE_VALUES, QuickTimerString.PLACE_VALUES);
            // set repetitions
            repsEditText.setText(String.format(Locale.US, "%02d", timeConfig.getTotalInfusions()));
            // set name
            EditText nameEditText = (EditText) findViewById(R.id.save_name_edit_text);
            nameEditText.setText(timeConfig.getName());
            // set favorite
            Switch favSwitch = (Switch) findViewById(R.id.save_fav_switch);
            favSwitch.setChecked(timeConfig.getIsFavorite());
            // set notes
            EditText notesEditText = (EditText) findViewById(R.id.save_notes_edit_text);
            String notes = timeConfig.getNotes();
            if (notes != null)
            {
                notesEditText.setText(notes);
            }

            if (position == -1)
            {
                // it is an unsaved config from BlankConfigActivity
                setFocus(nameEditText);
            }
            else
            {
                // from another one
                setFocus(t0MinEditText);
            }
        }

    }

    private void setFocus(EditText focusTarget)
    {
        // Set focus depending on where it came from
        focusTarget.requestFocus();
        focusTarget.selectAll();
    }

    private void addTwoInputTextChangedListener(final EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                editText.setSelectAllOnFocus(true);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (editText.getText().toString().trim().length() == 2)
                {
                    Log.i("SAVECONFIGACTIVITY", "Current Edit Text has 2 inputs");
                    editText.clearFocus();
                    View nextView = editText.focusSearch(FOCUS_DOWN);
                    if (nextView instanceof EditText)
                    {
                        EditText nextEditText = (EditText) nextView;
                        nextEditText.requestFocus();
                    }
                }
            }
        });
    }

    public void saveConfigActivity(View view)
    {
        // checks fields' entries
        int status = findErrors();

        if (status == 0)
        {
            // no problems found with fields
            makeConstructiveLoadIntent();
        }
        else
        {
            displayError(status);
        }
    }

    private void makeConstructiveLoadIntent()
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        if (position == -1)
        {
            // add new
            intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.ACTION_ADD);
        }
        else
        {
            // change existing
            intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.ACTION_EDIT);
        }

        Switch favSwitch = (Switch) findViewById(R.id.save_fav_switch);
        timeConfig.setFavorite(favSwitch.isChecked());

        intent.putExtra(getString(R.string.timer_extra), timeConfig);
        intent.putExtra(getString(R.string.position_extra), position);

        startActivity(intent);
        finish();
    }

    private void makeDestructiveLoadIntent()
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        intent.putExtra(getString(R.string.load_action_extra), LoadConfigActivity.ACTION_DELETE);
        intent.putExtra(getString(R.string.timer_extra), timeConfig);
        intent.putExtra(getString(R.string.position_extra), position);

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        startActivity(intent);
        finish();
    }

    private int findErrors()
    {
        int status = 0;
        int bitFlagSetter = 1;

        // Required fields: String name, long initial, long increment, int repetitions
        // get name
        EditText nameEditText = (EditText) findViewById(R.id.save_name_edit_text);
        String name = nameEditText.getText().toString();
        // get start timer
        EditText t0MinEditText = (EditText) findViewById(R.id.save_t0_min_edit_text);
        EditText t0SecEditText = (EditText) findViewById(R.id.save_t0_sec_edit_text);
        String t0MinString = t0MinEditText.getText().toString();
        String t0SecString = t0SecEditText.getText().toString();
        // get increment time
        EditText tdMinEditText = (EditText) findViewById(R.id.save_td_min_edit_text);
        EditText tdSecEditText = (EditText) findViewById(R.id.save_td_sec_edit_text);
        String tdMinString = tdMinEditText.getText().toString();
        String tdSecString = tdSecEditText.getText().toString();
        // get repetitions/total infusions
        EditText repsEditText = (EditText) findViewById(R.id.save_reps_edit_text);
        String repsString = repsEditText.getText().toString();
        // get notes
        EditText notesEditText = (EditText) findViewById(R.id.save_notes_edit_text);
        String notes = notesEditText.getText().toString();

        if (name.isEmpty())
        {
            status |= bitFlagSetter;
        }
        else
        {
            timeConfig.setName(name);
        }
        bitFlagSetter = bitFlagSetter << 1;
        if (t0MinString.isEmpty() && t0SecString.isEmpty())
        {
            status |= bitFlagSetter;
        }
        else
        {
            long t0MinLong = 0;
            long t0SecLong = 0;
            if (!(t0MinString.isEmpty()))
            {
                // if nonempty, parse
                t0MinLong = Long.parseLong(t0MinString);
            }
            if (!(t0SecString.isEmpty()))
            {
                // if nonempty, parse
                t0SecLong = Long.parseLong(t0SecString);
            }
            // convert to total seconds
            long t0Timer = (t0MinLong * 60) + t0SecLong;
            // consider error again
            if (t0Timer <= 0 || t0Timer >= 6000)
            {
                // start timer must be (0,6000)
                status |= bitFlagSetter;
            }
            else
            {
                timeConfig.setTimerInitial(t0Timer);
            }
        }
        bitFlagSetter = bitFlagSetter << 1;
        if (tdMinString.isEmpty() && tdSecString.isEmpty())
        {
            //status |= bitFlagSetter;
            // if this one is all empty, it will default to zero
            timeConfig.setTimerIncrement(0);
        }
        else
        {
            long tdMinLong = 0;
            long tdSecLong = 0;
            if (!(tdMinString.isEmpty()))
            {
                // if nonempty, parse
                tdMinLong = Long.parseLong(tdMinString);
            }
            if (!(tdSecString.isEmpty()))
            {
                // if nonempty, parse
                tdSecLong = Long.parseLong(tdSecString);
            }
            // convert to total seconds
            long tdTime = (tdMinLong * 60) + tdSecLong;
            // consider error again
            if (tdTime < 0 || tdTime >= 6000)
            {
                // increment time must be [0,6000)
                status |= bitFlagSetter;
            }
            else
            {
                timeConfig.setTimerIncrement(tdTime);
            }
        }
        bitFlagSetter = bitFlagSetter << 1;
        if (repsString.isEmpty())
        {
            status |= bitFlagSetter;
        }
        else
        {
            // can parse
            int repetitions = Integer.parseInt(repsString);
            if (repetitions <= 0 || repetitions > 99)
            {
                // repetitions must be (0,99]
                status |= bitFlagSetter;
            }
            else
            {
                timeConfig.setTotalInfusions(repetitions);
            }
        }
        timeConfig.setNotes(notes);

        Log.i("SAVECONFIGACTIVITY", "Status: " + status);

        return status;
    }

    private void displayError(int status)
    {
        // Name 1=1, t0 10=2, td 100=4, r 1000=8

        int[] errorButtonIdArray = {R.id.save_name_error_button, // 0
                R.id.save_start_time_error_button, // 1
                R.id.save_increment_error_button, // 2
                R.id.save_total_infusions_error_button // 3
        };
        String[] errorMessageArray = {
                "A name must be provided.",
                "Start timer must be greater than 0 seconds and less than 6000 seconds.",
                "Increment amount must be at least 0 seconds and less than 6000 seconds.",
                "Total number of infusions must be greater than 0 times and no more than 99 seconds."
        };
        int bitCheck = 1;
        for (int i = 0; i < errorButtonIdArray.length; ++i)
        {
            if ((bitCheck & status) == bitCheck)
            {
                setErrorResponse((ImageButton) findViewById(errorButtonIdArray[i]), errorMessageArray[i]);
            }
            bitCheck <<= 1;
        }
    }

    private void setErrorResponse(ImageButton imageButton, final String message)
    {
        imageButton.setVisibility(View.VISIBLE);
        imageButton.setColorFilter(Color.argb(255, 255, 105, 97));
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.save_error_popup, null); // TODO: not sure
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAsDropDown(v, 0, 0); // TODO: can customize
                TextView popupTextView = popupView.findViewById(R.id.save_error_popup_text_view);
                popupTextView.setText(message);

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
    }

    public void clickDelete(View view)
    {
        showDeleteConfirmationDialog();
    }

    // Delete Confirmation Dialog
    private void showDeleteConfirmationDialog()
    {
        DialogFragment dialog = new DeleteConfirmationDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteConfirmationDialogFragment");
    }

    @Override
    public void onDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            Toast.makeText(SaveConfigActivity.this, "User clicked positive", Toast.LENGTH_LONG).show();
            makeDestructiveLoadIntent();
        }
        else
        {
            Toast.makeText(SaveConfigActivity.this, "User clicked negative", Toast.LENGTH_LONG).show();
        }
    }
}
