package com.example.heyimhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DraftsListAdapter extends RecyclerView.Adapter<DraftsListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class DraftsViewHolder extends ViewHolder {
        private final TextView draftMessageView;
        private final TextView draftNumberView;

        private DraftsViewHolder(View itemView) {
            super(itemView);
            draftMessageView = itemView.findViewById(R.id.txtNumber);
            draftNumberView = itemView.findViewById(R.id.txtName);
        }

        public void setListeners(final Message message) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

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
    private Context mContext;

    public DraftsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
            // Set on click listeners
            myHolder.setListeners(current);
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
