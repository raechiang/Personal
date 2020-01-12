package com.rae.gongfutimer_v1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.adapters.TimeConfigAdapter;
import com.rae.gongfutimer_v1.fragments.DeleteConfirmationDialogFragment;
import com.rae.gongfutimer_v1.utils.DefaultConfigsParser;
import com.rae.gongfutimer_v1.utils.TimeConfig;
import com.rae.gongfutimer_v1.utils.TimeConfigDataSet;

public class LoadConfigActivity extends AppCompatActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener
{
    // Constants for user actions
    public static final int ACTION_ADD = -1;
    public static final int ACTION_DELETE = -2;
    public static final int ACTION_EDIT = -3;
    public static final int ACTION_NONE = -4;

    TimeConfigDataSet timeConfigDataSet = new TimeConfigDataSet();
    private TimeConfigAdapter tcAdapter;
    private boolean changedConfigs;
    private int positionFocused;
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

        Log.i("LOADCONFIGACTIVITY", "On new intent");
        processExtraData();
    }

    // User Input Processing
    private void processExtraData()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            // Retrieve data
            int actionCommand = intent.getIntExtra(getString(R.string.load_action_extra), ACTION_NONE);
            int receivedPosition = intent.getIntExtra(getString(R.string.position_extra), timeConfigDataSet.size());
            TimeConfig receivedConfig = (TimeConfig) intent.getSerializableExtra(getString(R.string.timer_extra));
            // Act on Data
            executeCommand(actionCommand, receivedPosition, receivedConfig);
        }
    }
    private void executeCommand(int actionCommand, int receivedPosition, TimeConfig receivedConfig)
    {
        switch (actionCommand)
        {
            case ACTION_NONE:
                break;
            case ACTION_ADD:
                // add timer
                reportChanges(timeConfigDataSet.addNewTimeConfig(receivedConfig), "A new timer has been added.");
                break;
            case ACTION_DELETE:
                if (receivedPosition >= 0 && receivedPosition < timeConfigDataSet.size())
                {
                    // delete one
                    reportChanges(timeConfigDataSet.deleteTimeConfig(receivedPosition, receivedConfig), "A timer has been deleted.");
                }
                else
                {
                    // check if want delete default or delete all
                    if (receivedPosition == ACTION_EDIT)
                    {
                        reportChanges(timeConfigDataSet.deleteDefaultTimeConfigs(), "Default timers have been deleted.");
                    }
                    else if (receivedPosition == ACTION_DELETE)
                    {
                        reportChanges(timeConfigDataSet.deleteAllTimeConfigs(), "All timers have been deleted.");
                    }
                }
                break;
            case ACTION_EDIT:
                if (receivedPosition >= 0 && receivedPosition < timeConfigDataSet.size())
                {
                    // check bounds
                    reportChanges(timeConfigDataSet.editTimeConfig(receivedConfig, receivedPosition), "A timer has been modified.");
                }
                else if (receivedPosition == ACTION_ADD)
                {
                    // check if want add defaults
                    reportChanges(restoreDefaultTimers(), "Default timers have been restored.");
                }
                break;
            default:
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
        List<TimeConfig> defaultConfigs = parseDefaultConfigs();
        if (defaultConfigs != null)
        {
            // restore them
            return timeConfigDataSet.addDefaultTimeConfigs(defaultConfigs);
        }
        return false;
    }

    // General Data Set Methods (involve resources)
    private List<TimeConfig> parseDefaultConfigs()
    {
        DefaultConfigsParser p = new DefaultConfigsParser();
        try
        {
            XmlResourceParser par = getResources().getXml(R.xml.default_configs);
            return p.parse(par);
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private void initializeTimeConfigData()
    {
        FileInOut fileInOut = new FileInOut();
        ArrayList<TimeConfig> tempSet = fileInOut.deserialize();
        if (tempSet != null)
        {
            // successful deserialization
            timeConfigDataSet.addAll(tempSet);
        }
        else
        {
            // has not been initialized before
            if (timeConfigDataSet.isEmpty())
            {
                List<TimeConfig> defaultSet = parseDefaultConfigs();
                if (defaultSet != null)
                {
                    changedConfigs = timeConfigDataSet.addAll(defaultSet);
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
            // writes to file
            FileInOut fileInOut = new FileInOut();
            if (fileInOut.serialize(timeConfigDataSet.getTimeConfigList()))
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

    // Methods That Start New Activities
    public void startBlankConfigActivity(View view)
    {
        Intent intent = new Intent(this, BlankConfigActivity.class);

        startActivity(intent);
    }
    private void startSelectedTimerActivity(TimeConfig config, int position)
    {
        Intent intent = new Intent(this, TimerActivity.class);

        intent.putExtra(getString(R.string.timer_extra), config);
        intent.putExtra(getString(R.string.position_extra), position);

        startActivity(intent);
    }
    public void startNewSaveConfigActivity(View view)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        startActivity(intent);
        finish();
    }
    private void startSelectedEditActivity(int position)
    {
        Intent intent = new Intent(this, SaveConfigActivity.class);

        intent.putExtra(getString(R.string.timer_extra), timeConfigDataSet.get(position));
        intent.putExtra(getString(R.string.position_extra), position);

        startActivity(intent);
    }
    private void startSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    // View Initializations
    private void init()
    {
        changedConfigs = false;
        positionFocused = -1;

        tcAdapter = new TimeConfigAdapter(timeConfigDataSet, new TimeConfigAdapter.OnEditClickedListener()
        {
            @Override
            public void onMoreClicked(View v, int position)
            {
                showPopupMenu(v, position);
            }
            @Override
            public void onTimerElementClicked(View v, int position)
            {
                startSelectedTimerActivity(timeConfigDataSet.get(position), position);
            }
            @Override
            public boolean onTimerElementLongClicked(View v, int position)
            {
                startSelectedEditActivity(position);
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rvLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tcAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        initializeTimeConfigData();
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
                    case R.id.edit_config:
                        startSelectedEditActivity(position);
                        return true;
                    case R.id.delete_config:
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
        if (timeConfigDataSet.isEmpty())
        {
            Log.i("LOADCONFIGACTIVITY", "Number of elements: " + timeConfigDataSet.size());
            recyclerView.setVisibility(View.GONE);
            emptyDataSetTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.i("LOADCONFIGACTIVITY", "Number of elements: " + timeConfigDataSet.size());
            recyclerView.setVisibility(View.VISIBLE);
            emptyDataSetTextView.setVisibility(View.GONE);
        }
    }

    // Delete Confirmation Dialog Methods
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
            reportChanges(timeConfigDataSet.deleteTimeConfig(positionFocused, timeConfigDataSet.get(positionFocused)), "A timer has been deleted.");
            positionFocused = -1;
        }
    }

    // Toolbar Methods
    @Override
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
        boolean serialize(ArrayList<TimeConfig> configsList)
        {
            try
            {
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

        ArrayList<TimeConfig> deserialize()
        {
            try
            {
                FileInputStream fis = openFileInput(getString(R.string.file_name));
                ObjectInputStream ois = new ObjectInputStream(fis);

                ArrayList<TimeConfig> configs = (ArrayList<TimeConfig>) ois.readObject();

                ois.close();
                fis.close();

                return configs;
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
