package com.example.heyimhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.TimeRule;

import java.util.List;

public class LocationRuleListAdapter extends RecyclerView.Adapter<LocationRuleListAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(LocationRule rule);
    }

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class RuleViewHolder extends LocationRuleListAdapter.ViewHolder {

        private final TextView RuleBodyView;
        private final ImageButton btnDelete;

        private RuleViewHolder(View itemView) {
            super(itemView);
            RuleBodyView = itemView.findViewById(R.id.RuleBody);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void setListeners(final LocationRule rule) {

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

    public class EmptyViewHolder extends LocationRuleListAdapter.ViewHolder {
        private final TextView mTextView;

        private EmptyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.txtView);
        }
    }

    private final LayoutInflater mInflater;
    private List<LocationRule> mRules; //data source of the list adapter
    private OnDeleteClickListener onDeleteClickListener;

    public LocationRuleListAdapter(Context context, OnDeleteClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.onDeleteClickListener = listener;
    }

    @Override
    public LocationRuleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        LocationRuleListAdapter.ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.listview_rule, parent, false);
            itemHolder = new LocationRuleListAdapter.RuleViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.listview_no_rules, parent, false);
            itemHolder = new LocationRuleListAdapter.EmptyViewHolder(itemView);
        }

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(LocationRuleListAdapter.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL) {
            // If everything proceeds normally
            // Initialize view with data
            LocationRuleListAdapter.RuleViewHolder myHolder = (LocationRuleListAdapter.RuleViewHolder) holder;
            LocationRule current = mRules.get(position);


            String text = "Place: "+current.name+" Radius: "+current.radius;
            myHolder.RuleBodyView.setText(text);

            // Set on click listeners
            myHolder.setListeners(current);
        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            LocationRuleListAdapter.EmptyViewHolder myHolder = (LocationRuleListAdapter.EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Location Rules");
        }

    }

    void setRules(List<LocationRule> rules) {
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