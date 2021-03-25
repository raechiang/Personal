package com.rae.gongfutimer_v2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.adapters.TimerConfigAdapter;
import com.rae.gongfutimer_v2.fragments.DeleteConfirmationDialogFragment;
import com.rae.gongfutimer_v2.utils.DefaultConfigsParser;
import com.rae.gongfutimer_v2.utils.TimerConfig;
import com.rae.gongfutimer_v2.utils.TimerConfigDataSet;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays a list of previously saved timer configurations using RecyclerView. From
 * this activity, the user can create a new timer, edit an existing timer, or delete an existing
 * timer. Additionally, settings can be accessed in the toolbar.
 */
public class LoadConfigActivity extends TimerConfigActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener
{
    /**
     * These fields correspond to the different actions that can be taken. They are to be used in
     * Intents that are passed to this Activity, especially those that may modify the data set of
     * timer configurations. The specific actions are as follows:
     * <ul>
     *     <li>NONE: No action</li>
     *     <li>ADD_ONE: Add one config</li>
     *     <li>RESTORE_DEFAULTS: Restore default configs</li>
     *     <li>EDIT_ONE: Edit one config at position</li>
     *     <li>DELETE_ONE: Delete one config at position</li>
     *     <li>REMOVE_DEFAULTS: remove default configs</li>
     *     <li>DELETE_ALL: delete all configs</li>
     * </ul>
     */
    public enum Action
    {
        // no action
        NONE,
        // add actions
        ADD_ONE, RESTORE_DEFAULTS,
        // modifying action
        EDIT_ONE,
        // delete actions
        DELETE_ONE, REMOVE_DEFAULTS, DELETE_ALL
    }

    /**
     * This contains the saved time configurations.
     */
    TimerConfigDataSet timerConfigDataSet = new TimerConfigDataSet();

    /**
     * The adapter provides data to the views.
     */
    private TimerConfigAdapter tcAdapter;

    /**
     * This boolean indicates whether a change has been done to the data set, so that the data set
     * can be updated in {@link #updateSavedDataSet()}.
     */
    private boolean changedConfigs;

    /**
     * This integer corresponds to the index of the time configuration that may have been selected
     * for deletion from the popup menu. It is assigned in {@link #showPopupMenu(View, int)}, and it
     * is eventually passed to {@link #onDeleteDialogResponseClick(boolean)}, which will call for its
     * deletion.
     */
    private int positionFocused;

    /**
     * This holds access to the RecyclerView. It is set up in {@link #init()}.
     */
    private RecyclerView recyclerView;

    /**
     * This initializes the activity. It sets up the UI and will grab the Intent to process.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        applyTheme();

        setContentView(R.layout.activity_load_config);

        init();
        // processIntent();
    }

    /**
     * If the user stops interacting with the activity, a few changes will need to be saved--in this
     * case, the timers.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        updateSavedDataSet();
    }

    /**
     * Called after onPause after resuming the activity or onStart after beginning the activity.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (!(isThemeCurrent()))
        {
            applyTheme();
            recreate();
        }
    }

    /**
     * Sets up the intent, which dictates what actions the user wants carried out by the Activity.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        processIntent();
    }

    // User Input Processing

    /**
     * This retrieves the intent, pulls the relevant data, and acts upon the data by calling
     * {@link #executeCommand(Action, int, TimerConfig)}.
     */
    @Override
    protected void processIntent()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            // retrieve data
            Action action = (Action) intent.getSerializableExtra(getString(R.string.load_action_extra));
            int receivedPosition = intent.getIntExtra(getString(R.string.load_position_extra), timerConfigDataSet.size());
            TimerConfig receivedConfig = (TimerConfig) intent.getSerializableExtra(getString(R.string.load_timer_extra));
            // act on data
            if (action != null)
            {
                executeCommand(action, receivedPosition, receivedConfig);
            }
        }
    }

    /**
     * If an action was found within the Intent, the Activity will carry it out. There are seven
     * possible actions: NONE, ADD_ONE, RESTORE_DEFAULTS, EDIT_ONE, DELETE_ONE, REMOVE_DEFAULTS, and
     * DELETE_ALL. NONE does nothing. ADD_ONE will add a new timer to the saved configs; this timer
     * must have been passed as a receivedConfig. RESTORE_DEFAULTS will restore default timers,
     * which are the timers that the application begins with. These are simply suggestions for tea
     * timers. EDIT_ONE will edit the timer at the receivedPosition. DELETE_ONE will permanently
     * delete the timer at the receivedPosition. REMOVE_DEFAULTS will remove the defaults timers.
     * DELETE_ALL will permanently delete all of the timers.
     * @param action - The action to be performed, one of NONE, ADD_ONE, RESTORE_DEFAULTS, EDIT_ONE
     *               DELETE_ONE, REMOVE_DEFAULTS, or DELETE_ALL.
     * @param receivedPosition - The position of the timer configuration in the dataset.
     * @param receivedConfig - The config to add, edit, or delete.
     */
    private void executeCommand(Action action, int receivedPosition, TimerConfig receivedConfig)
    {
        // NONE, ADD_ONE, RESTORE_DEFAULTS, EDIT_ONE, DELETE_ONE, REMOVE_DEFAULTS, DELETE_ALL
        switch (action)
        {
            case NONE:
                break;
            case ADD_ONE:
                // add timer
                reportChanges(timerConfigDataSet.addNewTimerConfig(receivedConfig), "A new timer has been added.");
                int position = timerConfigDataSet.indexOf(receivedConfig);
                if (position != -1)
                {
                    recyclerView.scrollToPosition(position);
                }
                break;
            case RESTORE_DEFAULTS:
                // restore default configs
                reportChanges(restoreDefaultTimers(), "Default timers have been restored.");
                break;
            case EDIT_ONE:
                // edit config at position
                if (receivedPosition >= 0 && receivedPosition < timerConfigDataSet.size())
                {
                    // check bounds, edit one
                    reportChanges(timerConfigDataSet.editTimerConfig(receivedPosition, receivedConfig), "A timer has been modified.");
                }
                else
                {
                    Toast.makeText(this, "Could not make edit to the timer config.", Toast.LENGTH_SHORT).show();
                }
                break;
            case DELETE_ONE:
                // delete config at position
                if (receivedPosition >= 0 && receivedPosition < timerConfigDataSet.size())
                {
                    // check bounds, delete one
                    reportChanges(timerConfigDataSet.deleteTimerConfig(receivedPosition, receivedConfig), "A timer has been deleted.");
                }
                else
                {
                    Toast.makeText(this, "Could not delete the timer config.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REMOVE_DEFAULTS:
                // remove default configs
                reportChanges(timerConfigDataSet.deleteDefaultTimerConfigs(), "Default timers have been deleted.");
                break;
            case DELETE_ALL:
                // delete all configs
                reportChanges(timerConfigDataSet.deleteAllTimerConfigs(), "All timers have been deleted.");
                break;
            default:
                Log.i("LOADCONFIGACTIVITY", "Unexpected action received.");
                break;
        }
    }

    /**
     * If a change to the dataset was successful, then a confirmation message is displayed and the
     * dataset needs to be updated.
     * @param success - Whether the change was successful or not.
     * @param message - The message that needs to be reported, which corresponds to the action
     *                taken.
     */
    private void reportChanges(boolean success, String message)
    {
        if (success)
        {
            Toast.makeText(LoadConfigActivity.this, message, Toast.LENGTH_SHORT).show();
            changedConfigs = true;
            updateSavedDataSet();
        }
    }

    // Specific Data Set Methods
    /**
     * This method restores the default timers, which the application began with.
     * @return - true if this was successful, false if not. Specifically, if the defaultConfigs
     *         could not be found, it will fail.
     */
    private boolean restoreDefaultTimers()
    {
        List<TimerConfig> defaultConfigs = parseDefaultConfigs();
        if (defaultConfigs != null)
        {
            // restore them
            return timerConfigDataSet.addDefaultTimerConfigs(defaultConfigs);
        }
        return false;
    }

    // General Data Set Methods (involve resources)
    /**
     * This parses the default timer configurations from a file.
     * @return - A list of TimerConfigs.
     */
    private List<TimerConfig> parseDefaultConfigs()
    {
        DefaultConfigsParser p = new DefaultConfigsParser();
        try
        {
            XmlResourceParser par = getResources().getXml(R.xml.default_configs);
            return p.parse(par);
        } catch (XmlPullParserException xppe)
        {
            xppe.printStackTrace();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return null;
    }
    /**
     * This takes data out from a file and adds them to the dataset which is displayed in this
     * activity.
     */
    private void initializeTimerConfigData()
    {
        FileInOut fileInOut = new FileInOut();
        ArrayList<TimerConfig> tempSet = fileInOut.deserialize();
        if (tempSet != null)
        {
            Log.i("LOADCONFIGACTIVITY", "Set found in file.ser");
            timerConfigDataSet.addAll(tempSet);
        }
        else
        {
            // has not been initialized before
            if (timerConfigDataSet.isEmpty())
            {
                Log.i("LOADCONFIGACTIVITY", "Empty set");
                List<TimerConfig> defaultSet = parseDefaultConfigs();
                if (defaultSet != null)
                {
                    changedConfigs = timerConfigDataSet.addAll(defaultSet);
                    updateSavedDataSet();
                }
            }
        }
        tcAdapter.notifyDataSetChanged();
    }

    /**
     * This is called when there has been a change to the dataset. It will save this change to the
     * file itself.
     */
    private void updateSavedDataSet()
    {
        if (changedConfigs)
        {
            // write to file
            FileInOut fileInOut = new FileInOut();
            if (fileInOut.serialize(timerConfigDataSet.getTimerConfigList()))
            {
                Log.i("LOADCONFIGACTIVITY", "Saved list.");
                changedConfigs = false;
            }
            // rebuilds list
            tcAdapter.notifyDataSetChanged();

            // checks empty
            switchView();
        }
        else
        {
            Log.i("LOADCONFIGACTIVITY", "No changes to list.");
        }
    }

    // View Initializations
    /**
     * This is called when the Activity view is being initialized. It sets up the Adapter,
     * RecyclerView, timer configuration data, and calls to process the intent.
     */
    @Override
    protected void init()
    {
        changedConfigs = false;
        positionFocused = -1;

        tcAdapter = new TimerConfigAdapter(timerConfigDataSet);

        recyclerView = (RecyclerView) findViewById(R.id.load_recycler_view);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rvLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tcAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        setActionBar();
        setListeners();

        initializeTimerConfigData();
        switchView();

        processIntent();
    }
    /**
     * This just shows the popup menu attached to each timer configuration in the list.
     * @param v - The anchor of the popup menu.
     * @param position - The configuration's position in the list.
     */
    private void showPopupMenu(View v, final int position)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.load_menu_edit_config:
                        startSelectedEditActivity(position);
                        return true;
                    case R.id.load_menu_delete_config:
                        positionFocused = position;
                        showDeleteConfirmationDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.load_actions, popup.getMenu());
        popup.show();
    }
    /**
     * This sets up the action bar at the top.
     */
    @Override
    protected void setActionBar()
    {
        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }
    /**
     * This sets up the listeners for the list. When clicking the dropdown menu at the end of each
     * timer configuration, it will show a popup menu. If the configuration element itself is
     * clicked, it will open up the timer to run. If the element is held down to click, it will
     * open up an activity to edit the timer. There is also a floating button to save a new timer
     * configuration.
     */
    @Override
    protected void setListeners()
    {

        tcAdapter.setListener(new TimerConfigAdapter.OnEditClickedListener()
        {
            @Override
            public void onMoreClicked(View v, int position)
            {
                showPopupMenu(v, position);
            }
            @Override
            public void onTimerElementClicked(View v, int position)
            {
                startSelectedTimerActivity(timerConfigDataSet.get(position), position);
            }
            @Override
            public boolean onTimerElementLongClicked(View v, int position)
            {
                startSelectedEditActivity(position);
                return true;
            }
        });

        FloatingActionButton makeNewButton = (FloatingActionButton) findViewById(R.id.load_make_new_floating_action_button);
        makeNewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startNewSaveConfigActivity(v);
            }
        });
    }
    /**
     * This simply changes the view if the dataset of timers is empty.
     */
    private void switchView()
    {
        TextView emptyDataSetTextView = (TextView) findViewById(R.id.load_empty_content);
        if (timerConfigDataSet.isEmpty())
        {
            Log.i("LOADCONFIGACTIVITY", "Number of elements: " + timerConfigDataSet.size());
            recyclerView.setVisibility(View.GONE);
            emptyDataSetTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.i("LOADCONFIGACTIVITY", "Number of elements: " + timerConfigDataSet.size());
            recyclerView.setVisibility(View.VISIBLE);
            emptyDataSetTextView.setVisibility(View.GONE);
        }
    }

    // Methods That Start New Activities
    /**
     * This will start an activity that will allow the user to save a new timer configuration. This
     * occurs when the user clicks on the floating action button to create a new timer.
     * @param view
     */
    public void startNewSaveConfigActivity(View view)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), new TimerConfig());
        intent.putExtra(getString(R.string.load_position_extra), SaveConfigActivity.NEW_UNSAVED_TIMER);

        startActivity(intent);
        finish();
    }
    /**
     * This will start a timer activity using the timer configuration that the user clicked.
     * @param config - The selected timer.
     * @param position - The position of the selected timer in the dataset.
     */
    private void startSelectedTimerActivity(TimerConfig config, int position)
    {
        Intent intent = new Intent(this, TimerActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), config);
        intent.putExtra(getString(R.string.load_position_extra), position);

        startActivity(intent);

        Log.i("LOADCONFIGACTIVITY", "Start Timer at " + position + ": " + config.toString());
    }
    /**
     * This will start an activity that will let the user edit an existing timer configuration. To
     * use it, the user must have long-pressed the element or selected Edit from the popup menu of
     * the element.
     * @param position - The position of the selected timer in the dataset.
     */
    private void startSelectedEditActivity(int position)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), timerConfigDataSet.get(position));
        intent.putExtra(getString(R.string.load_position_extra), position);

        startActivity(intent);

        Log.i("LOADCONFIGACTIVITY", "Edit Timer at " + position + ": " + timerConfigDataSet.get(position).toString());
    }
    /**
     * This will open the settings, which is accessed by clicking the settings cog in the toolbar.
     */
    private void startSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    // Delete Confirmation Dialog Methods
    /**
     * This asks the user once if they would like to delete a given timer.
     */
    private void showDeleteConfirmationDialog()
    {
        DialogFragment dialog = new DeleteConfirmationDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteConfirmationDialogFragment");
    }
    /**
     * This will apply the deletion and update the data if the user confirmed deletion.
     * @param clickedPositive - True if the user confirmed the deletion, false if not.
     */
    @Override
    public void onDeleteDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            reportChanges(timerConfigDataSet.deleteTimerConfig(positionFocused, timerConfigDataSet.get(positionFocused)), "A timer has been deleted");
            positionFocused = -1;
        }
    }

    // Toolbar Methods
    /**
     * Inflates the menu to the toolbar.
     * @param menu
     * @return - returns true
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.app_actions, menu);
        return true;
    }
    /**
     * This just starts up the Settings when the user clicks the cog.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                // user picked settings
                startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // File In/Out Methods
    /**
     * This class handles the file that the timer configurations are saved to.
     */
    class FileInOut
    {
        /**
         * This writes the list of timer configurations into the file.
         * @param configsList - The list of timers to write into the file.
         * @return - True if the write was successful, false if not.
         */
        boolean serialize(ArrayList<TimerConfig> configsList)
        {
            try {
                FileOutputStream fos = openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(configsList);

                oos.close();
                fos.close();
                return true;
            } catch (FileNotFoundException fnfe)
            {
                fnfe.printStackTrace();
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            return false;
        }

        /**
         * This reads the list of timer configurations from the file and saves it to an ArrayList.
         * @return - An ArrayList of TimerConfigs, the timers read from the file.
         */
        ArrayList<TimerConfig> deserialize()
        {
            try
            {
                FileInputStream fis = openFileInput(getString(R.string.file_name));
                ObjectInputStream ois = new ObjectInputStream(fis);

                ArrayList<TimerConfig> configsList = (ArrayList<TimerConfig>) ois.readObject();

                ois.close();
                fis.close();
                return configsList;
            } catch (FileNotFoundException fnfe)
            {
                fnfe.printStackTrace();
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe)
            {
                cnfe.printStackTrace();
            }
            return null;
        }
    }
}
