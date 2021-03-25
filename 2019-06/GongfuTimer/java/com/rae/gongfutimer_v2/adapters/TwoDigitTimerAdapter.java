package com.rae.gongfutimer_v2.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rae.gongfutimer_v2.R;
import com.rae.gongfutimer_v2.ui.TimerView;
import com.rae.gongfutimer_v2.utils.TwoDigitTimer;
import com.rae.gongfutimer_v2.utils.TwoDigitTimerDataSet;

/**
 * This is the adapter that makes ViewHolders and binds the timer steps (infusions) data to the
 * ViewHolders for a RecyclerView.
 */
public class TwoDigitTimerAdapter extends RecyclerView.Adapter<TwoDigitTimerAdapter.TimerViewHolder>
{
    /**
     * This is the data set that holds all of the timers of a timer configuration.
     */
    private TwoDigitTimerDataSet timerDataSet;
    /**
     * This is the listener for a specific timer.
     */
    private OnRowClickedListener listener;

    /**
     * This nested class is the ViewHolder associated with this adapter.
     */
    public class TimerViewHolder extends RecyclerView.ViewHolder
    {
        /**
         * This TextView shows the row number of the step.
         */
        private TextView rowNumberText;
        /**
         * This TextView shows the two-digit timer of the step.
         */
        private TimerView timerText;
        /**
         * This ImageButton allows the user to delete a timer.
         */
        private ImageButton removeRowButton;
        /**
         * This ImageButton allows the user to decrease the step's timer by 1 second.
         */
        private ImageButton decrementButton;
        /**
         * This ImageButton allows the user to increase the step's timer by 1 second.
         */
        private ImageButton incrementButton;

        /**
         * The constructor makes a new TimerViewHolder. It connects Views with their respective
         * elements and sets up the views' listeners. This ViewHolder will display a timer's
         * row number (corresponding to its step number), the timer in mm:ss, and two to three
         * buttons to amend the timer step (remove the step, increase the timer amount, or decrease
         * the timer amount).
         * @param view - The View which is wrapped by the Holder.
         * @param viewType - The type of the View.
         */
        public TimerViewHolder(View view, int viewType)
        {
            super(view);

            rowNumberText = (TextView) view.findViewById(R.id.save_recycler_timer_row_number_text_view);
            timerText = (TimerView) view.findViewById(R.id.save_timer_min_sec_timer_view);
            removeRowButton = (ImageButton) view.findViewById(R.id.save_recycler_timer_row_remove_button);

            //RelativeLayout timerInputLayout = (RelativeLayout) view.findViewById(R.id.save_recycler_timer_input_layout);
            decrementButton = (ImageButton) view.findViewById(R.id.save_timer_decrement_image_button);
            incrementButton = (ImageButton) view.findViewById(R.id.save_timer_increment_image_button);

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
                    long newSecs = timerDataSet.get(getAdapterPosition()).getTimerInSecs();
                    if (newSecs == 1)
                    {
                        decrementButton.setVisibility(View.INVISIBLE);
                    }
                    else if (newSecs == 5998)
                    {
                        incrementButton.setVisibility(View.VISIBLE);
                    }
                    // hide if value is 1, show otherwise
                }
                // hide if value is 1/show otherwise
            });
            incrementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onIncrementClicked(v, getAdapterPosition());
                    long newSecs = timerDataSet.get(getAdapterPosition()).getTimerInSecs();
                    if (newSecs == 5999)
                    {
                        incrementButton.setVisibility(View.INVISIBLE);
                    }
                    else if (newSecs == 2)
                    {
                        decrementButton.setVisibility(View.VISIBLE);
                    }
                    // hide if value is 99:59, show otherwise
                }
                // hide if value is 99:59/show otherwise
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

        /**
         * If the timer has been changed, the button Views may need to be changed to prevent the
         * user from inputting badly.
         * @param timerInSecs - The timer of the step/row.
         */
        public void notifyTimerChanged(long timerInSecs)
        {
            if (timerInSecs <= 1)
            {
                // do not allow the user to decrease anymore
                // timers cannot count down from 0 or from negative numbers
                decrementButton.setVisibility(View.INVISIBLE);
                incrementButton.setVisibility(View.VISIBLE);
            }
            else if (timerInSecs >= 5999)
            {
                // do not allow the user to increase anymore
                // two-digit timer have a maximum to maintain mm:ss format
                decrementButton.setVisibility(View.VISIBLE);
                incrementButton.setVisibility(View.INVISIBLE);
            }
            else
            {
                // values in between are fine
                decrementButton.setVisibility(View.VISIBLE);
                incrementButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * This constructor makes the TwoDigitTimerAdapter which contains the data set that is to be
     * displayed by a RecyclerView.
     * @param timerDataSet - The data to display.
     */
    public TwoDigitTimerAdapter(TwoDigitTimerDataSet timerDataSet)
    {
        this.timerDataSet = timerDataSet;
    }

    /**
     * This method sets the listener of the Adapter so that the RecyclerView element can be
     * interacted with.
     * @param listener
     */
    public void setListener(OnRowClickedListener listener)
    {
        this.listener = listener;
    }

    /**
     * This method is called by a RecyclerView to make a new ViewHolder, which will wrap around a
     * new View.
     * @param parent - The ViewGroup that the new View is added to.
     * @param viewType - The type of the new View.
     * @return - A TimerViewHolder associated with the new View.
     */
    @Override
    @NonNull
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_recycler_item_timer_row, parent, false);
        return new TimerViewHolder(itemView, viewType);
    }

    /**
     * This method is called by a RecyclerView to bind data to a TimerViewHolder. Based off of the
     * adapter's {@link #timerDataSet}, it will attach relevant data to the views. The holder
     * needs the timer values at every step.
     * @param holder - The TimerViewHolder whose data needs to be updated.
     * @param position - The position of the item in the data set.
     */
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

                long timerSecs = timerDataSet.get(position).getTimerInSecs();
                if (timerSecs <= 1)
                {
                    holder.decrementButton.setVisibility(View.INVISIBLE);
                }
                else if (timerSecs >= 5999)
                {
                    holder.incrementButton.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * This method retrieves the size of the {@link #timerDataSet}.
     * @return - Integer that is the size of the data set.
     */
    @Override
    public int getItemCount()
    {
        if (timerDataSet != null)
        {
            return timerDataSet.size();
        }
        return 0;
    }

    /**
     * This interface functions as a listener for each individual element of the RecyclerView. In
     * this case, there needs to be four interactions: when the user clicks on the decrement button,
     * the timer value at the position must decrease by one second; when the user clicks the
     * increment button, the timer value at the position must increase by one second; when the user
     * clicks the mm:ss timer itself, the user must be able to edit the timer by inputting the exact
     * values they want; and when the user clicks on the remove button, the step (row) needs to be
     * deleted.
     */
    public interface OnRowClickedListener
    {
        /**
         * This method will occur when the user clicks the decrement [-] button. It should decrease
         * the step of the timer at the position by one second.
         * @param v
         * @param position - The position or step number of the timer.
         */
        public void onDecrementClicked(View v, int position);
        /**
         * This method will occur when the user clicks the increment [+] button. It should increase
         * the step of the timer at the position by one second.
         * @param v
         * @param position - The position or step number of the timer.
         */
        public void onIncrementClicked(View v, int position);
        /**
         * This method will occur when the user clicks the mm:ss timer itself. The user should then
         * be able to manually input the timer value at the position.
         * @param v
         * @param position - The position or step number of the timer.
         */
        public void onTimerInputClicked(View v, int position);
        /**
         * This method will occur when the user clicks the delete [x] button. It should delete the
         * step of the timer at the position.
         * @param v
         * @param position - The position or step number of the timer.
         */
        public void onRemoveTimerClicked(View v, int position);
    }
}
