package com.rae.gongfutimer_v2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

import java.util.Locale;

public class SaveNumpadDialogFragment extends DialogFragment
{
    private TextView timerMinsTextView;
    private TextView timerSecsTextView;
    private SaveNumpadDialogListener listener;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.save_numpad_dialog, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // setNavigationOnClickListener is too high?
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

        Toolbar toolbar = view.findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.save_numpad_action);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

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
                        // TODO: error popup
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

        setButtons(view);
    }

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

    private void shiftTimerLeftBy(char input)
    {
        char[] timerData = getTimerChars();
        int size = timerData.length;
        System.arraycopy(timerData, 1, timerData, 0, size - 1);
        timerData[size - 1] = input;
        setTimerTextViews(timerData);
    }
    private void clearTimer()
    {
        timerMinsTextView.setText(String.format(Locale.US, "%02d", 0));
        timerSecsTextView.setText(String.format(Locale.US, "%02d", 0));
    }
    private void shiftTimerRight()
    {
        char[] timerData = getTimerChars();
        int size = timerData.length;
        System.arraycopy(timerData, 0, timerData, 1, size - 1);
        timerData[0] = '0';
        setTimerTextViews(timerData);
    }
    private char[] getTimerChars()
    {
        return (timerMinsTextView.getText().toString()
                + timerSecsTextView.getText().toString()).toCharArray();
    }
    private void setTimerTextViews(char[] timerData)
    {
        int size = timerData.length;
        timerMinsTextView.setText(timerData, 0, size / 2);
        timerSecsTextView.setText(timerData, size / 2, size / 2);
    }

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

    public interface SaveNumpadDialogListener
    {
        public void onApplyClick(char[] timerData, int position);
    }
}
