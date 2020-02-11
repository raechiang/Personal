package com.rae.gongfutimer_v2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.rae.gongfutimer_v2.R;

import java.util.Locale;

public class SaveNumpadInputView extends RelativeLayout
{
    public SaveNumpadInputView(Context context)
    {
        super(context);
    }

    public SaveNumpadInputView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_save_numpad_input, this, true);

        setButtons();
    }

    private void setButtons()
    {
        int[] numButtonIds = {R.id.save_numpad_n0_button,
                R.id.save_numpad_n1_button, R.id.save_numpad_n2_button, R.id.save_numpad_n3_button,
                R.id.save_numpad_n4_button, R.id.save_numpad_n5_button, R.id.save_numpad_n6_button,
                R.id.save_numpad_n7_button, R.id.save_numpad_n8_button, R.id.save_numpad_n9_button};

        for (int i = 0; i < numButtonIds.length; ++i)
        {
            Button numButton = (Button) findViewById(numButtonIds[i]);
            numButton.setText(String.format(Locale.US, "%d", i));
        }

        Button clearButton = (Button) findViewById(R.id.save_numpad_clear_button);
        ImageButton backspaceButton = (ImageButton) findViewById(R.id.save_numpad_backspace_button);
    }
}
