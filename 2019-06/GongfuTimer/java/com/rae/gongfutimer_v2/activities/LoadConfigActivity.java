package com.rae.gongfutimer_v2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

public class LoadConfigActivity extends AppCompatActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_config);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        init();

        processExtraData();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        updateSavedDataSet();
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        processExtraData();
    }

    // User Input Processing
    private void processExtraData()
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
                    // TODO: something bad happened
                    Log.i("LOADCONFIGACTIVITY", "EDIT_ONE: Unexpected position provided.");
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
                    // TODO: something bad happened
                    Log.i("LOADCONFIGACTIVITY", "DELETE_ONE: Unexpected position provided.");
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
    private void init()
    {
        changedConfigs = false;
        positionFocused = -1;

        tcAdapter = new TimerConfigAdapter(timerConfigDataSet, new TimerConfigAdapter.OnEditClickedListener()
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

        recyclerView = (RecyclerView) findViewById(R.id.load_recycler_view);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rvLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tcAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        initializeTimerConfigData();
        switchView();
    }
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
    public void startNewSaveConfigActivity(View view)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), new TimerConfig());
        intent.putExtra(getString(R.string.load_position_extra), SaveConfigActivity.NEW_UNSAVED_TIMER);

        startActivity(intent);
    }
    private void startSelectedTimerActivity(TimerConfig config, int position)
    {
        Intent intent = new Intent(this, TimerActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), config);
        intent.putExtra(getString(R.string.load_position_extra), position);

        startActivity(intent);

        Log.i("LOADCONFIGACTIVITY", "Start Timer at " + position + ": " + config.toString());
    }
    private void startSelectedEditActivity(int position)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.load_timer_extra), timerConfigDataSet.get(position));
        intent.putExtra(getString(R.string.load_position_extra), position);

        startActivity(intent);

        Log.i("LOADCONFIGACTIVITY", "Edit Timer at " + position + ": " + timerConfigDataSet.get(position).toString());
    }
    private void startSettings()
    {

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);

        Log.i("LOADCONFIGACTIVITY", "Start Settings");
    }

    // Delete Confirmation Dialog Methods
    private void showDeleteConfirmationDialog()
    {
        DialogFragment dialog = new DeleteConfirmationDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteConfirmationDialogFragment");
    }
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.app_actions, menu);
        return true;
    }
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
    class FileInOut
    {
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
