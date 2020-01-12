package com.rae.gongfutimer_v1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


import com.rae.gongfutimer_v1.R;

import java.util.Locale;

public class BlankInputButtonsFragment extends Fragment implements View.OnClickListener
{
    private OnButtonClickedListener callback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate layout for this fragment
        View view = inflater.inflate(R.layout.blank_number_fragment, container, false);
        setButtons(view);
        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void setButtons(View view)
    {
        int[] numButtonIds = {R.id.blank_n0_button,
                R.id.blank_n1_button, R.id.blank_n2_button, R.id.blank_n3_button,
                R.id.blank_n4_button, R.id.blank_n5_button, R.id.blank_n6_button,
                R.id.blank_n7_button, R.id.blank_n8_button, R.id.blank_n9_button};
        for (int i = 0; i < numButtonIds.length; ++i)
        {
            Button numButton = (Button) view.findViewById(numButtonIds[i]);
            numButton.setText(String.format(Locale.US, "%d", i));
            numButton.setOnClickListener(this);
        }

        Button prevButton = (Button) view.findViewById(R.id.blank_prev_button);
        prevButton.setOnClickListener(this);
        Button nextButton = (Button) view.findViewById(R.id.blank_next_button);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        prevButton.setEnabled(false);

        Button clearButton = (Button) view.findViewById(R.id.blank_clear_button);
        clearButton.setOnClickListener(this);
        ImageButton backspaceButton = (ImageButton) view.findViewById(R.id.blank_backspace_button);
        backspaceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.blank_prev_button || v.getId() == R.id.blank_next_button)
        {
            callback.onNavClicked(v.getId());
        }
        else if (v.getId() == R.id.blank_n1_button || v.getId() == R.id.blank_n2_button || v.getId() == R.id.blank_n3_button
        || v.getId() == R.id.blank_n4_button || v.getId() == R.id.blank_n5_button || v.getId() == R.id.blank_n6_button
        || v.getId() == R.id.blank_n7_button || v.getId() == R.id.blank_n8_button || v.getId() == R.id.blank_n9_button
        || v.getId() == R.id.blank_n0_button)
        {
            // numerical button
            Button button = (Button) v.findViewById(v.getId());
            String value = button.getText().toString();

            callback.onNumberClicked(value.charAt(0));
        }
        else if (v.getId() == R.id.blank_clear_button || v.getId() == R.id.blank_backspace_button)
        {
            callback.onClearBackspaceClicked(v.getId());
        }
    }

    public void setOnButtonClickedListener(OnButtonClickedListener callback)
    {
        this.callback = callback;
    }

    public interface OnButtonClickedListener
    {
        public void onNumberClicked(char value);
        public void onNavClicked(int id);
        public void onClearBackspaceClicked(int id);
    }
}
