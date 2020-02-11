package com.rae.gongfutimer_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.rae.gongfutimer_v2.fragments.SaveNumpadDialogFragment;
import com.rae.gongfutimer_v2.fragments.UnsavedDialogFragment;
import com.rae.gongfutimer_v2.utils.TimerConfig;
import com.rae.gongfutimer_v2.utils.TwoDigitTimer;
import com.rae.gongfutimer_v2.utils.TwoDigitTimerDataSet;

import java.util.ArrayList;

public class SaveConfigActivity extends AppCompatActivity implements DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener,
        UnsavedDialogFragment.UnsavedDialogListener, SaveNumpadDialogFragment.SaveNumpadDialogListener
{
    public final static int NEW_UNSAVED_TIMER = -1;

    private RecyclerView recyclerView;
    private TwoDigitTimerDataSet timerDataSet = new TwoDigitTimerDataSet();
    private TwoDigitTimerAdapter tAdapter;

    private int loadPosition;
    private TimerConfig timerConfig;

    private boolean isChanged;

    @Override
    public void onCreate(Bundle savedInstanceState)
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

        init();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

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

    private void init()
    {
        Intent intent = getIntent();
        timerConfig = (TimerConfig) intent.getSerializableExtra(getString(R.string.load_timer_extra));
        loadPosition = intent.getIntExtra(getString(R.string.load_position_extra), NEW_UNSAVED_TIMER);
        isChanged = false;

        Log.i("SAVECONFIGACTIVITY", "Received position: " + loadPosition + " with " + timerConfig.getTotalInfusions());

        long[] timersArray = timerConfig.getTimerSeconds();
        for (long timer : timersArray)
        {
            timerDataSet.add(new TwoDigitTimer(timer));
        }
        tAdapter = new TwoDigitTimerAdapter(timerDataSet, makeRowListener());

        recyclerView = (RecyclerView) findViewById(R.id.save_recycler_view);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rvLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        EditText nameTextView = (EditText) findViewById(R.id.save_name_edit_text);
        nameTextView.setText(timerConfig.getName());

        setButtons();
    }

    private void setButtons()
    {
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

        ImageButton addNewRowButton = (ImageButton) findViewById(R.id.save_add_new_timer_row_button);
        addNewRowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("SAVECONFIGACTIVITY", "New add new row button clicked");
                int lastPosition = timerDataSet.size() - 1;
                long lastTimer = timerDataSet.get(lastPosition).getTimerInSecs();
                timerDataSet.add(new TwoDigitTimer(lastTimer + 5)); // TODO: make default addValue
                tAdapter.notifyItemInserted(lastPosition + 1);
                tAdapter.notifyItemRangeChanged(lastPosition + 1, tAdapter.getItemCount());
                isChanged = true;
                recyclerView.scrollToPosition(tAdapter.getItemCount() - 1);
            }
        });

        FloatingActionButton saveFAB = (FloatingActionButton) findViewById(R.id.save_save_config_floating_action_button);
        FloatingActionButton deleteFAB = (FloatingActionButton) findViewById(R.id.save_delete_config_floating_action_button);
        if (loadPosition == -1)
        {
            // unsaved
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

        FloatingActionButton runTimerFAB = (FloatingActionButton) findViewById(R.id.save_start_unsaved_config_floating_action_button);
        runTimerFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (loadPosition == NEW_UNSAVED_TIMER || isChanged)
                {
                    // unsaved new
                    //showUnsavedRunConfirmationDialog();
                    showUnsavedConfirmationDialog(R.string.run_unsaved_dialog);
                }
                else
                {
                    startRunTimerActivity();
                }
            }
        });
    }

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
    private void startDeleteConfigLoadActivity()
    {
        startActivity(makeLoadConfigIntent(LoadConfigActivity.Action.DELETE_ONE));
        finish();
    }
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
    private void displayErrors(int status)
    {
        // TODO: make error popups... Since most of the inputs will be controlled from the start, is only Name necessary?
    }

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

        // Stuff that doesn't need error-checking and hasn't been updated onClick
        // icon
        // TODO: icon...?

        return status;
    }

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
    private void showUnsavedConfirmationDialog(int messageId)
    {
        DialogFragment dialog = new UnsavedDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.unsaved_string_id_key), messageId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "UnsavedDialogFragment");
    }
    @Override
    public void onUnsavedRunDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();
            startRunTimerActivity();
        }
    }
    @Override
    public void onUnsavedReturnDialogResponseClick(boolean clickedPositive)
    {
        if (clickedPositive)
        {
            Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();

            isChanged = false;
            Intent intent = new Intent(this, LoadConfigActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Delete Confirmation Dialog
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
            Toast.makeText(SaveConfigActivity.this, "User confirmed action", Toast.LENGTH_LONG).show();
            startDeleteConfigLoadActivity();
        }
    }

    // Row Click Listener
    private TwoDigitTimerAdapter.OnRowClickedListener makeRowListener()
    {
        return new TwoDigitTimerAdapter.OnRowClickedListener()
        {
            @Override
            public void onDecrementClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": [-] clicked");
                long oldSecs = timerDataSet.get(position).getTimerInSecs();
                if (oldSecs != 1)
                {
                    timerDataSet.edit(position, (timerDataSet.get(position).getTimerInSecs() - 1));
                    tAdapter.notifyItemChanged(position);
                    // TODO: I don't really like the animation
                    isChanged = true;
                }
            }

            @Override
            public void onIncrementClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": [+] clicked");
                long oldSecs = timerDataSet.get(position).getTimerInSecs();
                if (oldSecs != 5999)
                {
                    timerDataSet.edit(position, (timerDataSet.get(position).getTimerInSecs() + 1));
                    tAdapter.notifyItemChanged(position);
                    // TODO: I don't really like the animation
                    isChanged = true;
                }
            }

            @Override
            public void onTimerInputClicked(View v, int position)
            {
                Log.i("SAVECONFIGACTIVITY", (position + 1) + ": Timer input clicked");
                openNumpadInputDialog(position, timerDataSet.get(position));
            }

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
    private void openNumpadInputDialog(int position, TwoDigitTimer timer)
    {
        DialogFragment dialog = new SaveNumpadDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharArray(getString(R.string.save_numpad_timer_key), timer.toCharArray());
        bundle.putInt(getString(R.string.save_numpad_position_key), position);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SaveNumpadDialogFragment");
    }
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
                isChanged = true;
            }
            else
            {
                Log.i("SAVECONFIGACTIVITY", "No changes to timer at " + (position + 1));
            }
        }
    }
    private long getSecondsLong(char[] timerData)
    {
        long timerCharLong = Long.parseLong(new String(timerData));
        long min = timerCharLong / 100;
        long sec = timerCharLong % 100;
        return (60 * min) + sec;
    }
}
