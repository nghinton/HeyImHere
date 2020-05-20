package com.example.heyimhere;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.TimeRule;
import java.util.List;

public class DraftsListAdapter extends RecyclerView.Adapter<DraftsListAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(Message message);
    }

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class DraftsViewHolder extends ViewHolder implements TimeRuleListAdapter.OnDeleteClickListener, LocationRuleListAdapter.OnDeleteClickListener {
        private final TextView draftMessageView;
        private final TextView draftNumberView;
        private final ImageButton btnDelete;
        private final ImageButton btnAddLocationRule;
        private final ImageButton btnAddTimeRule;
        private final RecyclerView lstTimeRule;
        private final RecyclerView lstLocationRule;

        private DraftsViewHolder(View itemView) {
            super(itemView);
            draftMessageView = itemView.findViewById(R.id.txtNumber);
            draftNumberView = itemView.findViewById(R.id.txtName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddLocationRule = itemView.findViewById(R.id.btnAddLocationRule);
            btnAddTimeRule = itemView.findViewById(R.id.btnAddTimeRule);
            lstTimeRule = itemView.findViewById(R.id.TimeRuleList);
            lstLocationRule = itemView.findViewById(R.id.LocationRuleList);
        }

        public void setListeners(final Message message) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  Intent msgIntent = new Intent(v.getContext(), MessageActivity.class);
                    msgIntent.putExtra("draftNumber", message.receiver);
                    msgIntent.putExtra("draftBody", message.body);
                    v.getContext().startActivity(msgIntent); */
                }
            });

            btnAddTimeRule.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddTimeRuleActivity.class);
                    intent.putExtra("messageID", message.id);
                    v.getContext().startActivity(intent);
                }
            });

            btnAddLocationRule.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddLocationRuleActivity.class);
                    intent.putExtra("messageID", message.id);
                    v.getContext().startActivity(intent);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.OnDeleteClickListener(message);
                    }
                }
            });

        }

        public void setTimeRuleList(final Message message) {
            // initialize and set adapter for the rule list
            final TimeRuleListAdapter adapter = new TimeRuleListAdapter(mContext, this);
            lstTimeRule.setAdapter(adapter);
            lstTimeRule.setLayoutManager(new LinearLayoutManager(mContext));

            // Get a new or existing ViewModel from the ViewModelProvider.
            TimeRuleViewModel mTimeRuleViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(TimeRuleViewModel.class);

            // Add an observer on the LiveData returned by getAllContacts.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.
            mTimeRuleViewModel.getRulesForMessage(message.id).observe((LifecycleOwner) mContext, new Observer<List<TimeRule>>() {
                @Override
                public void onChanged(@Nullable final List<TimeRule> rules) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setRules(rules);
                }
            });

        }

        public void setLocationRuleList(final Message message) {
            // initialize and set adapter for the rule list
            final LocationRuleListAdapter adapter = new LocationRuleListAdapter(mContext, this);
            lstLocationRule.setAdapter(adapter);
            lstLocationRule.setLayoutManager(new LinearLayoutManager(mContext));

            // Get a new or existing ViewModel from the ViewModelProvider.
            LocationRuleViewModel mLocationRuleViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(LocationRuleViewModel.class);

            // Add an observer on the LiveData returned by getAllContacts.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.
            mLocationRuleViewModel.getRulesForMessage(message.id).observe((LifecycleOwner) mContext, new Observer<List<LocationRule>>() {
                @Override
                public void onChanged(@Nullable final List<LocationRule> rules) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setRules(rules);
                }
            });

        }

        @Override
        public void OnDeleteClickListener(TimeRule rule) {
            // Get a new or existing ViewModel from the ViewModelProvider.
            TimeRuleViewModel mRuleViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(TimeRuleViewModel.class);
            // Code for Delete operation
            mRuleViewModel.delete(rule);
        }

        @Override
        public void OnDeleteClickListener(LocationRule rule) {
            // Get a new or existing ViewModel from the ViewModelProvider.
            LocationRuleViewModel mLocationRuleViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(LocationRuleViewModel.class);
            // Code for Delete operation
            mLocationRuleViewModel.delete(rule);
        }

    }

    public class EmptyViewHolder extends ViewHolder {
        private final TextView mTextView;

        private EmptyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.txtView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> mDrafts; //data source of the list adapter
    private OnDeleteClickListener onDeleteClickListener;
    private Context mContext;

    public DraftsListAdapter(Context context, OnDeleteClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.onDeleteClickListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.listview_drafts, parent, false);
            itemHolder = new DraftsViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.listview_empty, parent, false);
            itemHolder = new EmptyViewHolder(itemView);
        }

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL) {
            // If everything proceeds normally
            DraftsViewHolder myHolder = (DraftsViewHolder) holder;
            Message current = mDrafts.get(position);
            myHolder.draftMessageView.setText(current.body);
            myHolder.draftNumberView.setText(current.receiver);
            // Set on click listeners
            myHolder.setListeners(current);
            // Set Lists
            myHolder.setTimeRuleList(current);
            myHolder.setLocationRuleList(current);
        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            EmptyViewHolder myHolder = (EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Drafts");
        }

    }

    void setDrafts(List<Message> drafts) {
        mDrafts = drafts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mDrafts != null)
            return mDrafts.size() > 0 ? mDrafts.size() : 1;
        else return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDrafts == null || mDrafts.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }


}
