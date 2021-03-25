package com.rae.gongfutimer_v2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.utils.TimerConfig;
import com.rae.gongfutimer_v2.utils.TimerConfigDataSet;

import java.util.Locale;

/**
 * This is the adapter that makes ViewHolders and binds the timer configuration data to the
 * ViewHolders for the RecyclerView. Each timer element should display the configuration's name,
 * initial timer, total number of steps (infusions), and icon. Interacting with the element should
 * let the user run, edit, or delete the selected timer.
 */
public class TimerConfigAdapter extends RecyclerView.Adapter<TimerConfigAdapter.ConfigViewHolder>
{
    /**
     * This is the data set that contains the timerConfigs that will be shown in the RecyclerView.
     */
    private TimerConfigDataSet timerConfigDataSet;

    /**
     * This is the listener for an individual timer configuration element.
     */
    private OnEditClickedListener listener;

    /**
     * This nested class is the ViewHolder associated with this adapter.
     */
    public class ConfigViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * This TextView displays the name of the timer configuration.
         */
        private final TextView nameTextView;
        /**
         * This TextView displays the total number of infusions or steps for the timer.
         */
        private final TextView infusionsTextView;
        /**
         * This TextView displays the first step of the timer.
         */
        private final TextView timerInitialTextView;
        /**
         * This ImageView displays the timer's icon.
         */
        private final ImageView icon;

        /**
         * The constructor creates a new ConfigViewHolder. It connects Views with their respective
         * elements and sets up the views' listeners.
         * @param view - The View which is wrapped by the Holder.
         * @param viewType - The type of the View.
         */
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
            icon = (ImageView) view.findViewById(R.id.load_timer_image_view);
        }
    }

    /**
     * This constructor makes the TimerConfigAdapter which contains the data set that is to be
     * displayed by a RecyclerView.
     * @param timerConfigDataSet - The data to display.
     */
    public TimerConfigAdapter(TimerConfigDataSet timerConfigDataSet)
    {
        this.timerConfigDataSet = timerConfigDataSet;
    }

    /**
     * This method sets the listener of the Adapter so that the RecyclerView element can be
     * interacted with.
     * @param listener
     */
    public void setListener(OnEditClickedListener listener)
    {
        this.listener = listener;
    }

    /**
     * This method is called by a RecyclerView to make a new ViewHolder, which will wrap around a
     * new View.
     * @param parent - The ViewGroup that the new View is added to.
     * @param viewType - The type of the new View.
     * @return - A ConfigViewHolder associated with the new View.
     */
    @Override
    @NonNull
    public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.load_recycler_item_config, parent, false);
        return new ConfigViewHolder(itemView, viewType);
    }

    /**
     * This method is called by a RecyclerView to bind data to a ConfigViewHolder. Based off of the
     * adapter's {@link #timerConfigDataSet}, it will attach relevant data to the views. The holder
     * needs the timer name, the initial timer to countdown from, the total number of steps
     * (infusions), and information on how to display the timer's icon.
     * @param holder - The ConfigViewHolder whose data needs to be updated.
     * @param position - The position of the item in the data set.
     */
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
                holder.icon.setImageResource(timerConfig.getIconStyle());
                holder.icon.setColorFilter(timerConfig.getIconColor());
            }
        }
    }

    /**
     * This method simply constructs a String consisting of the timer's name and a timer value,
     * converted from seconds into a mm:ss format.
     * @param timeValue - A timer in seconds.
     * @param name - The name of the timer configuration.
     * @return - A String of the name and the passed timer value in mm:ss form.
     */
    private String getMSString(long timeValue, String name)
    {
        long min = timeValue / 60;
        long sec = timeValue % 60;
        return String.format(Locale.US, "%s\n%02d:%02d", name, min, sec);
    }

    /**
     * This method retrieves the size of the {@link #timerConfigDataSet}.
     * @return - The size of the data set.
     */
    @Override
    public int getItemCount()
    {
        if (timerConfigDataSet != null)
        {
            return timerConfigDataSet.size();
        }
        return 0;
    }

    /**
     * This interface functions as a listener for each individual element of the RecyclerView. In
     * this case, there needs to be three interactions: when the user clicks the "More" button, a
     * small menu must pop up; when the use clicks on the timer element itself, it must run the
     * selected timer; and when the user long-clicks the timer element, it should open a new
     * Activity that will let the user make edits to the selected timer.
     */
    public interface OnEditClickedListener
    {
        /**
         * This method will occur when the user clicks on the "More" button. It should open a menu
         * with the options to Edit or Delete the selected timer configuration.
         * @param v
         * @param position - The position of the timer configuration in the data set.
         */
        public void onMoreClicked(View v, int position);
        /**
         * This method will occur when the user clicks on the timer element itself. It should run
         * the selected timer configuration.
         * @param v
         * @param position - The position of the timer configuration in the data set.
         */
        public void onTimerElementClicked(View v, int position);
        /**
         * This method will occur when the user long-clicks on the timer element itself. It should
         * allow the user to edit the selected timer configuration.
         * @param v
         * @param position - The position of the timer configuration in the data set.
         * @return - It returns true if the timer element was long clicked.
         */
        public boolean onTimerElementLongClicked(View v, int position);
    }
}
