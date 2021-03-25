package com.rae.gongfutimer_v2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.adapters.TwoDigitTimerAdapter;
import com.rae.gongfutimer_v2.fragments.DeleteConfirmationDialogFragment;
import com.rae.gongfutimer_v2.fragments.SaveIconDialogFragment;
import com.rae.gongfutimer_v2.fragments.SaveNumpadDialogFragment;
import com.rae.gongfutimer_v2.fragments.UnsavedDialogFragment;
import com.rae.gongfutimer_v2.utils.TimerConfig;
import com.rae.gongfutimer_v2.utils.TwoDigitTimer;
import com.rae.gongfutimer_v2.utils.TwoDigitTimerDataSet;

import java.util.ArrayList;

/**
 * This activity allows a user to set up a new timer or edit an existing timer. The data that can be
 * changed include the name, the icon, and the steps of the timer. A timer can also be deleted, and
 * a new timer can be started directly from here without saving it.
 */
public class SaveConfigActivity extends TimerConfigActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener,
        UnsavedDialogFragment.UnsavedDialogListener, SaveNumpadDialogFragment.SaveNumpadDialogListener, SaveIconDialogFragment.SaveIconDialogListener
{
    /**
     * This indicates that the timer has never been saved before.
     */
    public final static int NEW_UNSAVED_TIMER = -1;

    /**
     * The recycler view displays the list of timer steps.
     */
    private RecyclerView recyclerView;
    /**
     * The data set that contains the timer steps.
     */
    private TwoDigitTimerDataSet timerDataSet = new TwoDigitTimerDataSet();
    /**
     * The recyclerView's corresponding adapter.
     */
    private TwoDigitTimerAdapter tAdapter;
    /**
     * The view that displays the icon that is saved to the timer configuration. Users can change
     * the timer configuration's icon through this.
     */
    private ImageView iconImage;

    /**
     * The position of the timerConfig in the data set.
     */
    private int loadPosition;
    /**
     * The TimerConfig to make amendments to.
     */
    private TimerConfig timerConfig;

    /**
     * This indicates whether or not a change has been made to the timerConfig. This is so that the
     * application can prompt the user to save changes if any have been made or so that it can warn
     * about unsaved changes.
     */
    private boolean isChanged;

    /**
     * When adding a new timer step, the number of seconds added to the new step is determined by
     * this value. This value is set by default to five seconds, but the default value can be
     * changed in the settings.
     */
    private int secondsIncrement;

    /**
     * This initializes the activity. It sets up the UI and will process the intent.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        applyTheme();

        setContentView(R.layout.activity_save_config);

        init();
    }

    /**
     * This sets up the action bar at the top.
     */
    @Override
    protected void setActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * This method overrides the normal back navigation because it needs to be able to prompt for
     * confirmation in case the user has not saved the timer configuration.
     * @return - Always true.
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    /**
     * This method overrides the normal back pressed because it needs to prompt for an unsaved
     * timer configuration. Otherwise, it opens back up to the LoadConfigActivity.
     */
    @Override
    public void onBackPressed()
    {
        if (isChanged)
        {
            //showUnsavedReturnConfirmationDialog();
            showUnsavedConfirmationDialog(R.string.return_unsaved_dialog);
        }
        else
        {
            Intent intent = new Intent(this, LoadConfigActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * This method sets up the toolbar, processes intent, and populates the UI.
     */
    @Override
    protected void init()
    {
        // Toolbar setup
        setActionBar();

        processIntent();

        // field initializations
        isChanged = false;
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defIncVal = sharedPref.getString(getString(R.string.settings_default_increment_key), "5");
        // the default value is grabbed from SharedPreferences
        // it determines the increment of each timer step
        try {
            secondsIncrement = Integer.parseInt(defIncVal);
        } catch (NumberFormatException nfe) {
            secondsIncrement = 5;
        }

        Log.i("SAVECONFIGACTIVITY", "Received position: " + loadPosition + " with " + timerConfig.getTotalInfusions());

        long[] timersArray = timerConfig.getTimerSeconds();
        for (long timer : timersArray)
        {
            // populates the timerDataSet
            timerDataSet.add(new TwoDigitTimer(timer));
        }
        tAdapter = new TwoDigitTimerAdapter(timerDataSet);

        recyclerView = (RecyclerView) findViewById(R.id.save_recycler_view);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rvLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(tAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        EditText nameTextView = (EditText) findViewById(R.id.save_name_edit_text);
        nameTextView.setText(timerConfig.getName());

        iconImage = (ImageView) findViewById(R.id.save_icon_image_view);
        iconImage.setImageResource(timerConfig.getIconStyle());
        iconImage.setColorFilter(timerConfig.getIconColor());

        setListeners();
    }

    /**
     * This method grabs the information needed when this Activity is launched. This Activity needs
     * a TimerConfig and the position in the data set of this TimerConfig if it existed. If not, it
     * will default as a new unsaved timer.
     */
    @Override
    protected void processIntent()
    {
        Intent intent = getIntent();
        timerConfig = (TimerConfig) intent.getSerializableExtra(getString(R.string.load_timer_extra));
        loadPosition = intent.getIntExtra(getString(R.string.load_position_extra), NEW_UNSAVED_TIMER);
    }

    /**
     * This method sets up the listeners. The timer step adapter needs a listener, which will allow
     * the user to make changes to individual steps. There are also buttons that need listeners: one
     * sets this timer as a Favorite, one adds a new step to the timer steps, one to allow the user
     * to change the timer's icon, one to save the timer configuration, one to delete the timer
     * configuration, and one to run the timer configuration.
     */
    @Override
    protected void setListeners()
    {
        // listener for the timer steps
        tAdapter.setListener(makeRowListener());

        // sets the timer configuration as a favorite, which will put the timer at the top in Load
        ImageButton favoriteButton = (ImageButton) findViewById(R.id.save_favorite_button);
        switchFavoriteButtonImage();
        favoriteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (timerConfig.getIsFavorite())
                {
                    timerConfig.setFavorite(false);
                }
                else
                {
                    timerConfig.setFavorite(true);
                }
                switchFavoriteButtonImage();
                isChanged = true;
            }
        });

        // listener to add a new timer step
        ImageButton addNewRowButton = (ImageButton) findViewById(R.id.save_add_new_timer_row_button);
        addNewRowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("SAVECONFIGACTIVITY", "New add new row button clicked");
                int lastPosition = timerDataSet.size() - 1;
                long lastTimer = timerDataSet.get(lastPosition).getTimerInSecs();
                long newTimer = lastTimer + secondsIncrement;
                // Don't want to exceed max timer value
                if (newTimer > 5999)
                {
                    timerDataSet.add(new TwoDigitTimer(5999));
                }
                else
                {
                    timerDataSet.add(new TwoDigitTimer(newTimer));
                }
                tAdapter.notifyItemInserted(lastPosition + 1);
                tAdapter.notifyItemRangeChanged(lastPosition + 1, tAdapter.getItemCount());
                isChanged = true;
                recyclerView.scrollToPosition(tAdapter.getItemCount() - 1);
            }
        });

        // for saving the timer configuration
        FloatingActionButton saveFAB = (FloatingActionButton) findViewById(R.id.save_save_config_floating_action_button);
        // for deleting the timer configuration
        FloatingActionButton deleteFAB = (FloatingActionButton) findViewById(R.id.save_delete_config_floating_action_button);
        if (loadPosition == NEW_UNSAVED_TIMER)
        {
            // unsaved: cannot delete a timer that doesn't exist
            deleteFAB.hide();
        }
        else
        {
            deleteFAB.show();
        }
        saveFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startSaveConfigLoadActivity();
            }
        });
        deleteFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDeleteConfirmationDialog();
            }
        });

        // for running a timer using this configuration
        FloatingActionButton runTimerFAB = (FloatingActionButton) findViewById(R.id.save_start_unsaved_config_floating_action_button);
        runTimerFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (loadPosition == NEW_UNSAVED_TIMER || isChanged)
                {
                    // unsaved new
                    showUnsavedConfirmationDialog(R.string.run_unsaved_dialog);
                }
                else
                {
                    startRunTimerActivity();
                }
            }
        });

        // for changing icon
        ImageView icon = findViewById(R.id.save_icon_image_view);
        icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openIconPickDialog();
            }
        });
    }

    // Methods to start a different Activity
    /**
     * This method concerns constructive actions to take in LoadConfigActivity. First it checks for
     * input errors, and then it will tell the Load Activity to either save this new timer or edit
     * an existing timer. If the error check fails, it will not start a new activity and instead
     * report to the user what is wrong.
     */
    private void startSaveConfigLoadActivity()
    {
        int status = findErrorsAndUpdate();
        if (status == 0)
        {
            // no errors found, proceed
            if (loadPosition == NEW_UNSAVED_TIMER)
            {
                // new, unsaved timer
                startActivity(makeLoadConfigIntent(LoadConfigActivity.Action.ADD_ONE));
            }
            else
            {
                // timer already exists
                startActivity(makeLoadConfigIntent(LoadConfigActivity.Action.EDIT_ONE));
            }
            finish();
        }
        else
        {
            displayErrors(status);
        }
    }
    /**
     * This method will indicate to the LoadConfigActivity that the user has chosen to delete the
     * active timer configuration.
     */
    private void startDeleteConfigLoadActivity()
    {
        startActivity(makeLoadConfigIntent(LoadConfigActivity.Action.DELETE_ONE));
        finish();
    }
    /**
     * This method starts the TimerActivity using the currently loaded timer configuration. This
     * will also check the inputs for errors and report them instead if needed.
     */
    private void startRunTimerActivity()
    {
        int status = findErrorsAndUpdate();
        if (status == 0)
        {
            isChanged = false;
            // no errors found, proceed
            Intent intent = new Intent(this, TimerActivity.class);

            intent.putExtra(getString(R.string.load_position_extra), loadPosition);
            intent.putExtra(getString(R.string.load_timer_extra), timerConfig);

            startActivity(intent);
            finish();
        }
        else
        {
            displayErrors(status);
        }
    }
    /**
     * This will display the errors that were found by {@link #findErrorsAndUpdate()}.
     * @param status - An integer that indicates the bad inputs.
     */
    private void displayErrors(int status)
    {
        // TODO: Much of this is still from an old way of how this Activity functioned.
        //  This should be simplified because of the new way that the inputs are handled.
        /*
        0 name
        00 reps
        000 bad timer
         */
        int bitCheck = 1;
        if ((bitCheck & status) == bitCheck)
        {
            // name is wrong
            // TODO: highlight? animation? blink red? something to catch attention
            Toast.makeText(SaveConfigActivity.this, "A name must be provided.", Toast.LENGTH_LONG).show();
            EditText nameEditText = (EditText) findViewById(R.id.save_name_edit_text);
            nameEditText.setBackgroundColor(Color.RED);
        }

        bitCheck <<= 1;
        if ((bitCheck & status) == bitCheck)
        {
            // amount of reps is bad, but shouldn't be able to get here
            Log.i("SAVECONFIGACTIVITY", "The number of repetitions is invalid. Must be 1-99.");
        }

        bitCheck <<= 1;
        if ((bitCheck & status) == bitCheck)
        {
            // timer is bad, but shouldn't be able to get here
            Log.i("SAVECONFIGACTIVITY", "At least one timer value is invalid. Must be 1-5999.");
        }
    }

    /**
     * This method constructs an Intent for the LoadConfigActivity and is used by
     * {@link #startSaveConfigLoadActivity()} and {@link #startDeleteConfigLoadActivity()} because
     * they essentially build the same Intent, just with a different Action.
     * @param action - Indicates what kind of action should be taken in LoadConfigActivity.
     * @return - The intent to send back to LoadConfigActivity.
     */
    private Intent makeLoadConfigIntent(LoadConfigActivity.Action action)
    {
        Intent intent = new Intent(this, LoadConfigActivity.class);

        // put action
        intent.putExtra(getString(R.string.load_action_extra), action);
        // put position
        intent.putExtra(getString(R.string.load_position_extra), loadPosition);
        // put timerConfig
        intent.putExtra(getString(R.string.load_timer_extra), timerConfig);

        return intent;
    }

    /**
     * This checks the user's inputs to see if they are valid. In particular, the name just cannot
     * be empty, and there needs to be at least one timer steps (though this should not happen
     * because the user cannot delete the first row of the steps.
     * @return - A representation of the errors that were found.
     */
    private int findErrorsAndUpdate()
    {
        int status = 0;
        int bitFlagSetter = 1;

        // get name
        EditText nameEditText = (EditText) findViewById(R.id.save_name_edit_text);
        String name = nameEditText.getText().toString();

        if (name.isEmpty())
        {
            // bad
            status |= bitFlagSetter;
        }
        else
        {
            // good
            timerConfig.setName(name);
        }
        bitFlagSetter <<= 1;
        if (TwoDigitTimer.checkInfusionBounds(timerDataSet.size()))
        {
            // good reps
            bitFlagSetter <<= 1;
            long[] timers = timerDataSet.getTimerLongArray();
            ArrayList<Integer> invalidPositions = new ArrayList<>();
            for (int i = 0; i < timers.length; ++i)
            {
                // check all timers
                if (!(TwoDigitTimer.checkTimerBounds(timers[i])))
                {
                    // mark if bad
                    status |= bitFlagSetter;
                    invalidPositions.add(i);
                }
                // ignore if good
            }
            if ((status & bitFlagSetter) == 0)
            {
                // want 0X
                // can change timerConfig
                timerConfig.setTimerSeconds(timers);
            }
        }
        else
        {
            // bad reps
            status |= bitFlagSetter;
        }

        return status;
    }

    /**
     * This method toggles the Favorite image button.
     */
    private void switchFavoriteButtonImage()
    {
        ImageButton favoriteButton = (ImageButton) findViewById(R.id.save_favorite_button);
        if (timerConfig.getIsFavorite())
        {
            favoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
        }
        else
        {
            favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    // Unsaved Timer Dialog Fragment
    /**
     * This method shows a dialog if the user did not save before trying to navigate away from this
     * Activity.
     * @param messageId - The integer ID that corresponds to the message that should be displayed.
     */
    private void showUnsavedConfirmationDialog(int messageId)
    {
        DialogFragment dialog = new UnsavedDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.unsaved_string_id_key), messageId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "UnsavedDialogFragment");
    }
    /**
     * This method will occur if the user confirmed to continue the action despite the timer being
     * unsaved. This in particular will run the current timer.
     * @param clickedPositive - True when the user confirms the dialog.
     */
    @Override
    public void onUnsavedRunDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            //Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();
            startRunTimerActivity();
        }
    }
    /**
     * This method will occur if the user confirmed to continue the action despite the timer being
     * unsaved. This in particular will return to the LoadConfigActivity without making changes to
     * the data set.
     * @param clickedPositive - True when teh user confirms the dialog.
     */
    @Override
    public void onUnsavedReturnDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            //Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();

            isChanged = false;
            Intent intent = new Intent(this, LoadConfigActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Delete Confirmation Dialog
    /**
     * This method will display a dialog if the user chose to delete the current configuration,
     * asking for deletion confirmation.
     */
    private void showDeleteConfirmationDialog()
    {
        DialogFragment dialog = new DeleteConfirmationDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteConfirmationDialogFragment");
    }
    /**
     * This method will occur if the user responded positively to deletion. It will return to the
     * LoadConfigActivity, telling the application to delete the current timer configuration.
     * @param clickedPositive
     */
    @Override
    public void onDeleteDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            //Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();
            startDeleteConfigLoadActivity();
        }
    }

    // Row Click Listener
    /**
     * This sets up listeners for each component within the row of the timer steps (infusions) data
     * set.
     * @return - The row listener for each element of the timer steps list.
     */
    private TwoDigitTimerAdapter.OnRowClickedListener makeRowListener()
    {
        return new TwoDigitTimerAdapter.OnRowClickedListener()
        {
            /**
             * This is a listener for the decrement button. It reduces the number of seconds in a
             * row by one second.
             * @param v - The current view.
             * @param position - The position of the row in the data set.
             */
            @Override
            public void onDecrementClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": [-] clicked");
                long oldSecs = timerDataSet.get(position).getTimerInSecs();
                if (oldSecs != 1)
                {
                    long newSecs = timerDataSet.get(position).getTimerInSecs() - 1;
                    timerDataSet.edit(position, newSecs);
                    tAdapter.notifyItemChanged(position);
                    isChanged = true;
                }
            }

            /**
             * This is a listener for the increment button. It increases the number of seconds in a
             * row by one second.
             * @param v - The current view.
             * @param position - The position of the row in the data set.
             */
            @Override
            public void onIncrementClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": [+] clicked");
                long oldSecs = timerDataSet.get(position).getTimerInSecs();
                if (oldSecs != 5999)
                {
                    timerDataSet.edit(position, (timerDataSet.get(position).getTimerInSecs() + 1));
                    tAdapter.notifyItemChanged(position);
                    isChanged = true;
                }
            }

            /**
             * This is a listener for the entire row (excluding the buttons). When a timer step is
             * clicked, it will open up a dialog for the user to manually input exact numbers for
             * the step. This is a way to setting a timer step alternative to incrementing and
             * decrementing by one second.
             * @param v - The current view.
             * @param position - The position of the row in the data set.
             */
            @Override
            public void onTimerInputClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": Timer input clicked");
                openNumpadInputDialog(position, timerDataSet.get(position));
            }

            /**
             * This is a listener to delete a row (the timer step).
             * @param v - The current view.
             * @param position - The position of the row in the data set.
             */
            @Override
            public void onRemoveTimerClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": Remove row clicked");
                timerDataSet.remove(position);
                tAdapter.notifyItemRemoved(position);
                tAdapter.notifyItemRangeChanged(position, tAdapter.getItemCount());
                isChanged = true;
            }
        };
    }

    // Number Pad Input
    /**
     * This method is called when the user clicks on the row of a timer step. It allows for exact
     * manual input using a numpad. It is an alternative to incrementing and decrementing the
     * seconds by one.
     * @param position - The position of the row in the data set.
     * @param timer - The two-digit timer displayed in the row.
     */
    private void openNumpadInputDialog(int position, TwoDigitTimer timer)
    {
        DialogFragment dialog = new SaveNumpadDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharArray(getString(R.string.save_numpad_timer_key), timer.toCharArray());
        bundle.putInt(getString(R.string.save_numpad_position_key), position);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SaveNumpadDialogFragment");
    }
    /**
     * This method applies the changes to the row made through the numpad dialog.
     * @param timerData - The characters corresponding to the numpad input.
     * @param position - The position of the row in the data set.
     */
    @Override
    public void onApplyClick(char[] timerData, int position)
    {
        if (position != -1)
        {
            long timerInSecs = getSecondsLong(timerData);
            if (timerDataSet.get(position).getTimerInSecs() != timerInSecs)
            {
                // if not the same, update
                timerDataSet.get(position).setTimerInSecs(timerInSecs);
                tAdapter.notifyItemChanged(position);
                TwoDigitTimerAdapter.TimerViewHolder holder = (TwoDigitTimerAdapter.TimerViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                if (holder != null)
                {
                    holder.notifyTimerChanged(timerInSecs);
                }
                isChanged = true;
            }
            else
            {
                Log.i("SAVECONFIGACTIVITY", "No changes to timer at " + (position + 1));
            }
        }
    }
    /**
     * This method simply converts characters to a long.
     * @param timerData - The character array to convert to a long.
     * @return - The long that was parsed from the character array.
     */
    private long getSecondsLong(char[] timerData)
    {
        long timerCharLong = Long.parseLong(new String(timerData));
        Log.i("SAVECONFIGACTIVITY", "received " + timerCharLong);
        long min = timerCharLong / 100;
        long sec = timerCharLong % 100;
        return (60 * min) + sec;
    }

    // Icon Dialog
    /**
     * This method opens the dialog which allows the user to change the icon of the timer.
     */
    private void openIconPickDialog()
    {
        DialogFragment dialog = new SaveIconDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.save_icon_name_key), timerConfig.getIconStyle());
        bundle.putInt(getString(R.string.save_icon_color_key), timerConfig.getIconColor());
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SaveIconDialogFragment");
    }
    /**
     * This method applies the changes made to the icon from the icon picker dialog.
     * @param iconId - The icon type to use.
     * @param color - The color of the icon type.
     */
    @Override
    public void onApplyIconClick(int iconId, int color)
    {
        iconImage.setColorFilter(color);
        timerConfig.setIconColor(color);
        iconImage.setImageResource(iconId);
        timerConfig.setIconStyle(iconId);
        isChanged = true;
    }
}
