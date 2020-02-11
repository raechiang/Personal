package com.rae.gongfutimer_v2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rae.gongfutimer_v2.R;

import java.util.Locale;

public class TimerView extends RelativeLayout
{
    private TextView minTextView;
    private TextView secTextView;

    public TimerView(Context context)
    {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerView, 0, 0);
        int minValue = a.getInt(R.styleable.TimerView_valueMin, 0);
        int secValue = a.getInt(R.styleable.TimerView_valueSec, 0);
        //int textSize = a.getDimensionPixelSize(R.styleable.TimerView_timerTextSize, 20);
        int textSize = a.getInt(R.styleable.TimerView_timerTextSize, 20);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_timer, this, true);

        // Instantiation
        // min
        minTextView = (TextView) getChildAt(0);
        // colon
        TextView colonTextView = (TextView) getChildAt(1);
        // sec
        secTextView = (TextView) getChildAt(2);

        // Set
        setTimerTextViews(minValue, secValue);
        minTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        colonTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        secTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private char[] toLeftZeroValueChars(int value)
    {
        return (String.format(Locale.US, "%02d", value)).toCharArray();
    }

    private void setTimerTextViews(int minValue, int secValue)
    {
        // set min
        if (minValue >= 0)
        {
            minTextView.setText(toLeftZeroValueChars(minValue), 0, 2);
        }
        // set sec
        if (secValue >= 0)
        {
            if (secValue < 60)
            {
                secTextView.setText(toLeftZeroValueChars(secValue), 0, 2);
            }
        }
    }

    public void setTimerTextViews(char[] mins, char[] secs)
    {
        minTextView.setText(mins, 0, 2);
        secTextView.setText(secs, 0, 2);
    }
}
