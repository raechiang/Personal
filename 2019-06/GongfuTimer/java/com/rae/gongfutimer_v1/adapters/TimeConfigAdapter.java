package com.rae.gongfutimer_v1.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rae.gongfutimer_v1.R;
import com.rae.gongfutimer_v1.utils.TimeConfig;
import com.rae.gongfutimer_v1.utils.TimeConfigDataSet;

import java.util.Locale;

public class TimeConfigAdapter extends RecyclerView.Adapter<TimeConfigAdapter.ConfigViewHolder>
{
    private TimeConfigDataSet timeConfigDataSet;
    private OnEditClickedListener listener;

    public class ConfigViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView titleTextView;
        private final TextView repetitionsTextView;
        private final TextView timerInitialTextView;
        private final TextView timerIncrementTextView;

        public ConfigViewHolder(View view, int viewType)
        {
            super(view);

            titleTextView = (TextView) view.findViewById(R.id.timer_name_text_view);
            repetitionsTextView = (TextView) view.findViewById(R.id.load_repetitions_text_view);
            timerInitialTextView = (TextView) view.findViewById(R.id.load_timer_initial_text_view);
            timerIncrementTextView = (TextView) view.findViewById(R.id.load_timer_increment_text_view);

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

    public TimeConfigAdapter(TimeConfigDataSet timeConfigDataSet, OnEditClickedListener listener)
    {
        this.timeConfigDataSet = timeConfigDataSet;
        this.listener = listener;
    }

    @Override
    public ConfigViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_config, parent, false);
        return new ConfigViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(ConfigViewHolder holder, int position)
    {
        if (timeConfigDataSet != null)
        {
            if (!(timeConfigDataSet.isEmpty()))
            {
                TimeConfig tConfig = timeConfigDataSet.get(position);
                holder.titleTextView.setText(tConfig.getName());
                holder.timerInitialTextView.setText(getMSString(tConfig.getTimerInitial(), "Start"));
                // TODO: want multi-increments view? Maybe just append a ", ..."
                holder.timerIncrementTextView.setText(getMSString(tConfig.getTimerIncrement(0), "Incr"));
                holder.repetitionsTextView.setText(String.format(Locale.US, "%s\n%d", "Times", tConfig.getTotalInfusions()));
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
        if (timeConfigDataSet != null)
        {
            return timeConfigDataSet.size();
        }
        else return 0;
    }

    public interface OnEditClickedListener
    {
        public void onMoreClicked(View v, int position);
        public void onTimerElementClicked(View v, int position);
        public boolean onTimerElementLongClicked(View v, int position);
    }
}
