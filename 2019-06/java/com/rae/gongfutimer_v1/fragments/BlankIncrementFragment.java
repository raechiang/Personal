package com.rae.gongfutimer_v1.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.interfaces.QuickTimerString;

public class BlankIncrementFragment extends BlankDisplayFragment
{
    private TextView minutesTextView;
    private TextView secondsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate layout for this fragment
        View view = inflater.inflate(R.layout.blank_increment_fragment, container, false);

        minutesTextView = (TextView) view.findViewById(R.id.blank_td_minutes_text_view);
        secondsTextView = (TextView) view.findViewById(R.id.blank_td_seconds_text_view);
        char[] data = this.getArguments().getCharArray(getString(R.string.blank_increment_key));
        updateTextViews(data, QuickTimerString.PLACE_VALUES);

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void updateTextViews(char[] data, int placeValues)
    {
        try
        {
            minutesTextView.setText(data, 0, placeValues);
            secondsTextView.setText(data, placeValues, placeValues);
        } catch (NullPointerException npe)
        {
            Log.e("BLANKINCREMENTFRAG", "Could not find text views.");
        }

    }
}
