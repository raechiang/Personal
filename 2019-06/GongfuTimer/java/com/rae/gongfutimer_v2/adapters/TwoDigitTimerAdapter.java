package com.rae.gongfutimer_v2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.ui.TimerView;
import com.rae.gongfutimer_v2.utils.TwoDigitTimer;
import com.rae.gongfutimer_v2.utils.TwoDigitTimerDataSet;

public class TwoDigitTimerAdapter extends RecyclerView.Adapter<TwoDigitTimerAdapter.TimerViewHolder>
{
    private TwoDigitTimerDataSet timerDataSet;
    private OnRowClickedListener listener;

    public class TimerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView rowNumberText; // TODO: want consistent, double-digit row number size?
        private TimerView timerText;
        private ImageButton removeRowButton;

        public TimerViewHolder(View view, int viewType)
        {
            super(view);

            rowNumberText = (TextView) view.findViewById(R.id.save_recycler_timer_row_number_text_view);
            timerText = (TimerView) view.findViewById(R.id.save_timer_min_sec_timer_view);
            removeRowButton = (ImageButton) view.findViewById(R.id.save_recycler_timer_row_remove_button);

            //RelativeLayout timerInputLayout = (RelativeLayout) view.findViewById(R.id.save_recycler_timer_input_layout);
            ImageButton decrementButton = (ImageButton) view.findViewById(R.id.save_timer_decrement_image_button);
            ImageButton incrementButton = (ImageButton) view.findViewById(R.id.save_timer_increment_image_button);

            timerText.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onTimerInputClicked(v, getAdapterPosition());
                }
            });
            decrementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onDecrementClicked(v, getAdapterPosition());
                }
            });
            incrementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onIncrementClicked(v, getAdapterPosition());
                }
            });
            removeRowButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onRemoveTimerClicked(v, getAdapterPosition());
                }
            });
        }
    }

    public TwoDigitTimerAdapter(TwoDigitTimerDataSet timerDataSet, OnRowClickedListener listener)
    {
        this.timerDataSet = timerDataSet;
        this.listener = listener;
    }

    @Override
    @NonNull
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_recycler_item_timer_row, parent, false);
        return new TimerViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position)
    {
        if (timerDataSet != null)
        {
            if (!(timerDataSet.isEmpty()))
            {
                TwoDigitTimer timer = timerDataSet.get(position);
                holder.rowNumberText.setText(Integer.toString(position + 1));
                //char[] timerChars = timer.toCharArray();
                holder.timerText.setTimerTextViews(timer.getMinCharArray(), timer.getSecCharArray());
                if (position == 0)
                {
                    holder.removeRowButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // position is not 0
                    if (holder.removeRowButton.getVisibility() == View.INVISIBLE)
                    {
                        holder.removeRowButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if (timerDataSet != null)
        {
            return timerDataSet.size();
        }
        return 0;
    }

    public interface OnRowClickedListener
    {
        public void onDecrementClicked(View v, int position);
        public void onIncrementClicked(View v, int position);
        public void onTimerInputClicked(View v, int position);
        public void onRemoveTimerClicked(View v, int position);
    }
}
