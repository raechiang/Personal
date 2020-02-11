package com.rae.gongfutimer_v2.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.utils.TimerConfig;
import com.rae.gongfutimer_v2.utils.TimerConfigDataSet;

import java.util.Locale;

public class TimerConfigAdapter extends RecyclerView.Adapter<TimerConfigAdapter.ConfigViewHolder>
{
    private TimerConfigDataSet timerConfigDataSet;
    private OnEditClickedListener listener;

    public class ConfigViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView nameTextView;
        private final TextView infusionsTextView;
        private final TextView timerInitialTextView;

        public ConfigViewHolder(View view, int viewType)
        {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.load_timer_name_text_view);
            timerInitialTextView = (TextView) view.findViewById(R.id.load_timer_initial_text_view);
            infusionsTextView = (TextView) view.findViewById(R.id.load_infusions_text_view);

            ImageButton loadEditMore = (ImageButton) view.findViewById(R.id.load_edit_delete_button);
            loadEditMore.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onMoreClicked(v, getAdapterPosition());
                }
            });
            RelativeLayout timerElementInformation = (RelativeLayout) view.findViewById(R.id.load_timer_element_information);
            timerElementInformation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onTimerElementClicked(v, getAdapterPosition());
                }
            });
            timerElementInformation.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    return listener.onTimerElementLongClicked(v, getAdapterPosition());
                }
            });
        }
    }

    public TimerConfigAdapter(TimerConfigDataSet timerConfigDataSet, OnEditClickedListener listener)
    {
        this.timerConfigDataSet = timerConfigDataSet;
        this.listener = listener;
    }

    @Override
    @NonNull
    public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.load_recycler_item_config, parent, false);
        return new ConfigViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigViewHolder holder, int position)
    {
        if (timerConfigDataSet != null)
        {
            if (!(timerConfigDataSet.isEmpty()))
            {
                TimerConfig timerConfig = timerConfigDataSet.get(position);
                holder.nameTextView.setText(timerConfig.getName());
                holder.timerInitialTextView.setText(getMSString(timerConfig.getTimerSeconds()[0], "Start"));
                holder.infusionsTextView.setText(String.format(Locale.US, "%s\n%d", "Times", timerConfig.getTotalInfusions()));
            }
        }
    }

    private String getMSString(long timeValue, String name)
    {
        long min = timeValue / 60;
        long sec = timeValue % 60;
        return String.format(Locale.US, "%s\n%02d:%02d", name, min, sec);
    }

    @Override
    public int getItemCount()
    {
        if (timerConfigDataSet != null)
        {
            return timerConfigDataSet.size();
        }
        return 0;
    }

    public interface OnEditClickedListener
    {
        public void onMoreClicked(View v, int position);
        public void onTimerElementClicked(View v, int position);
        public boolean onTimerElementLongClicked(View v, int position);
    }
}
