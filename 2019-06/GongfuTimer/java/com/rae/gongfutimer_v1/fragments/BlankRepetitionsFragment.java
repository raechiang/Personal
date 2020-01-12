package com.rae.gongfutimer_v1.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.interfaces.QuickTimerString;

public class BlankRepetitionsFragment extends BlankDisplayFragment
{
    private TextView totalInfusionsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate layout for this fragment
        View view = inflater.inflate(R.layout.blank_repetitions_fragment, container, false);

        totalInfusionsTextView = (TextView) view.findViewById(R.id.num_of_reps_blank_text_view);
        char[] data = this.getArguments().getCharArray(getString(R.string.blank_infusions_key));
        updateTextViews(data, QuickTimerString.PLACE_VALUES);

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void updateTextViews(char[] data, int placeValues) {
        try
        {
            totalInfusionsTextView.setText(data, 0, placeValues);
        } catch (NullPointerException npe)
        {
            Log.e("BLANKREPSFRAGMENT", "Could not find text views.");
        }
    }
}
