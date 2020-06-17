package com.example.heyimhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Adapter_List_Sent extends RecyclerView.Adapter<Adapter_List_Sent.ViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(Message message);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class SentViewHolder extends ViewHolder {
        private final TextView BodyView;
        private final TextView RecieverView;
        private final TextView TimeView;
        private final ImageButton btnDelete;

        private SentViewHolder(View itemView) {
            super(itemView);
            BodyView = itemView.findViewById(R.id.txtBody);
            RecieverView = itemView.findViewById(R.id.txtReciever);
            TimeView = itemView.findViewById(R.id.txtTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void setListeners(final Message message) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
    }

    public class EmptyViewHolder extends ViewHolder {
        private final TextView mTextView;

        private EmptyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.txtView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> mSent; //data source of the list adapter
    private OnDeleteClickListener onDeleteClickListener;

    public Adapter_List_Sent(Context context, OnDeleteClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.onDeleteClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.listview_pending, parent, false);
            itemHolder = new SentViewHolder(itemView);
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
            SentViewHolder myHolder = (SentViewHolder) holder;
            Message current = mSent.get(position);
            myHolder.RecieverView.setText(current.receiver);
            myHolder.BodyView.setText(current.body);
            myHolder.TimeView.setText(current.time);
            // Set on click listeners
            myHolder.setListeners(current);
        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            EmptyViewHolder myHolder = (EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Drafts");
        }

    }

    void setSent(List<Message> messages) {
        mSent = messages;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mSent != null)
            return mSent.size() > 0 ? mSent.size() : 1;
        else return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mSent == null || mSent.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }


}

