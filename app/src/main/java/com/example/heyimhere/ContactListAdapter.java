package com.example.heyimhere;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heyimhere.database.Contact;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(Contact contact);
    }

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContactViewHolder extends ViewHolder {
        private final TextView contactNameView;
        private final TextView contactNumberView;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        private ContactViewHolder(View itemView) {
            super(itemView);
            contactNameView = itemView.findViewById(R.id.txtName);
            contactNumberView = itemView.findViewById(R.id.txtNumber);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void setListeners(final Contact contact) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent msgIntent = new Intent(v.getContext(), MessageActivity.class);
                    msgIntent.putExtra("number", contact.number);
                    msgIntent.putExtra("name", contact.name);
                    v.getContext().startActivity(msgIntent);
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditContactActivity.class);
                    intent.putExtra("number", contact.number);
                    intent.putExtra("name", contact.name);
                    ((Activity)v.getContext()).startActivityForResult(intent, ContactsActivity.EDIT_CONTACT_ACTIVITY_REQUEST_CODE);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.OnDeleteClickListener(contact);
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
    private List<Contact> mContacts; //data source of the list adapter
    private OnDeleteClickListener onDeleteClickListener;

    public ContactListAdapter(Context context, OnDeleteClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.onDeleteClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        ViewHolder itemHolder;

        if (viewType == VIEW_TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.listview_contacts, parent, false);
            itemHolder = new ContactViewHolder(itemView);
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
            ContactViewHolder myHolder = (ContactViewHolder) holder;
            Contact current = mContacts.get(position);
            myHolder.contactNameView.setText(current.name);
            myHolder.contactNumberView.setText(current.number);
            // Set on click listeners
            myHolder.setListeners(current);
        } else if (viewType == VIEW_TYPE_EMPTY){
            // Covers the case of data not being ready yet.
            EmptyViewHolder myHolder = (EmptyViewHolder) holder;
            myHolder.mTextView.setText("No Contacts");
        }

    }

    void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mContacts != null)
            return mContacts.size() > 0 ? mContacts.size() : 1;
        else return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContacts == null || mContacts.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }


}
