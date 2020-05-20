package com.example.heyimhere;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.heyimhere.database.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_FROM = 2;
    private static final int VIEW_TYPE_TO = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MessageViewHolder extends ViewHolder {
        private final TextView textMessageView;
        private final TextView textTimeView;

        private MessageViewHolder(View itemView) {
            super(itemView);
            textMessageView = itemView.findViewById(R.id.txtMessage);
            textTimeView = itemView.findViewById(R.id.txtTime);
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
    private final Context mContext;
    private List<Message> mTexts; //data source of the list adapter

    public MessageListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_TO) {
            itemView = mInflater.inflate(R.layout.listview_to, parent, false);
            itemHolder = new MessageViewHolder(itemView);
        } else if(viewType == VIEW_TYPE_FROM) {
            itemView = mInflater.inflate(R.layout.listview_from, parent, false);
            itemHolder = new MessageViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.listview_empty, parent, false);
            itemHolder = new EmptyViewHolder(itemView);
        }
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_TO) {
            // If the text if from you
            MessageViewHolder myHolder = (MessageViewHolder) holder;
            Message current = mTexts.get(position);
            myHolder.textMessageView.setText(current.body);

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
            Long longDate = Long.parseLong(current.time);
            Date date = new Date(longDate);
            String time = dateFormat.format(date);

            myHolder.textTimeView.setText(time);

        } else if(viewType == VIEW_TYPE_FROM) {
            // If the text if from someone else
            MessageViewHolder myHolder = (MessageViewHolder) holder;
            Message current = mTexts.get(position);
            myHolder.textMessageView.setText(current.body);
            myHolder.textTimeView.setText(current.time);

        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            EmptyViewHolder myHolder = (EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Texts");
        }

    }

    void setTexts(List<Message> texts) {
        mTexts = texts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTexts != null)
            return mTexts.size() > 0 ? mTexts.size() : 1;
        else return 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (mTexts == null || mTexts.size() == 0) {
            // If there are no texts
            return VIEW_TYPE_EMPTY;
        }

        // Get current message
        Message current = mTexts.get(position);
        //Gets user's phone number.
        TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        String ownNumber = PhoneNumberUtils.GetOwn(tMgr);

        if(current.receiver != ownNumber) {
            // If sending the text
            return VIEW_TYPE_TO;
        } else {
            // If receiving the text
            return VIEW_TYPE_FROM;
        }
    }


}