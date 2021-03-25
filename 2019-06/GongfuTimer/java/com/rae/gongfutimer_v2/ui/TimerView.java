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

/**
 * This View is a timer display in the form of mm:ss with leading zeroes.
 */
public class TimerView extends RelativeLayout
{
    /**
     * The TextView that displays the minutes of the timer.
     */
    private TextView minTextView;
    /**
     * The TextView that displays the seconds of the timer.
     */
    private TextView secTextView;

    /**
     * The constructor.
     * @param context
     */
    public TimerView(Context context)
    {
        super(context);
    }

    /**
     * The constructor. It inflates the layout and sets up the initial values and text attributes.
     * @param context
     * @param attrs
     */
    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerView, 0, 0);
        int minValue = a.getInt(R.styleable.TimerView_valueMin, 0);
        int secValue = a.getInt(R.styleable.TimerView_valueSec, 0);
        int textSize = a.getInt(R.styleable.TimerView_timerTextSize, 20);
        int color = a.getColor(R.styleable.TimerView_timerTextColor, 0);
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
        minTextView.setTextColor(color);
        colonTextView.setTextColor(color);
        secTextView.setTextColor(color);
    }

    /**
     * This forces an integer value to always have leading zeroes for two place values and converts
     * it to a character array.
     * @param value - The two-digit value to make a character array with leading zeroes.
     * @return - A character array with two digits and leading zeroes.
     */
    private char[] toLeftZeroValueChars(int value)
    {
        return (String.format(Locale.US, "%02d", value)).toCharArray();
    }

    /**
     * This sets the text of the TextViews for mm:ss given two integer parameters.
     * @param minValue - The minutes of the timer.
     * @param secValue - The seconds of the timer.
     */
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

    /**
     * This sets the text of the TextViews for mm:ss given two character arrays.
     * @param mins - The minutes of the timer.
     * @param secs - The seconds of the timer.
     */
    public void setTimerTextViews(char[] mins, char[] secs)
    {
        minTextView.setText(mins, 0, 2);
        secTextView.setText(secs, 0, 2);
    }
}
