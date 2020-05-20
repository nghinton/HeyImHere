package com.example.heyimhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.heyimhere.database.TimeRule;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class TimeRuleListAdapter extends RecyclerView.Adapter<TimeRuleListAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(TimeRule rule);
    }

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class RuleViewHolder extends TimeRuleListAdapter.ViewHolder {

        private final TextView RuleBodyView;
        private final ImageButton btnDelete;

        private RuleViewHolder(View itemView) {
            super(itemView);
            RuleBodyView = itemView.findViewById(R.id.RuleBody);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void setListeners(final TimeRule rule) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.OnDeleteClickListener(rule);
                    }
                }
            });
        }
    }

    public class EmptyViewHolder extends TimeRuleListAdapter.ViewHolder {
        private final TextView mTextView;

        private EmptyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.txtView);
        }
    }

    private final LayoutInflater mInflater;
    private List<TimeRule> mRules; //data source of the list adapter
    private OnDeleteClickListener onDeleteClickListener;

    public TimeRuleListAdapter(Context context, OnDeleteClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.onDeleteClickListener = listener;
    }

    @Override
    public TimeRuleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        TimeRuleListAdapter.ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.listview_rule, parent, false);
            itemHolder = new TimeRuleListAdapter.RuleViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.listview_no_rules, parent, false);
            itemHolder = new TimeRuleListAdapter.EmptyViewHolder(itemView);
        }

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(TimeRuleListAdapter.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL) {
            // If everything proceeds normally
            // Initialize view with data
            TimeRuleListAdapter.RuleViewHolder myHolder = (TimeRuleListAdapter.RuleViewHolder) holder;
            TimeRule current = mRules.get(position);

            // Format the time of the rule so it is readable
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(current.time);
            String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
            String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
            String text = "Send: "+currentTime+" "+currentDate;
            myHolder.RuleBodyView.setText(text);

            // Set on click listeners
            myHolder.setListeners(current);
        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            TimeRuleListAdapter.EmptyViewHolder myHolder = (TimeRuleListAdapter.EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Time Rules");
        }

    }

    void setRules(List<TimeRule> rules) {
        mRules = rules;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mRules != null)
            return mRules.size() > 0 ? mRules.size() : 1;
        else return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mRules == null || mRules.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }


}
