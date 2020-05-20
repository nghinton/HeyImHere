package com.example.heyimhere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.TimeRule;
import java.util.List;
import static android.app.Activity.RESULT_OK;

public class DraftsFragment extends Fragment implements DraftsListAdapter.OnDeleteClickListener {

    public static final String DRAFT_ID = "DraftID";

    private MessagesViewModel mMessagesViewModel;
    private ContactViewModel mContactViewModel;
    private TelephonyManager telephonyManager;
    public static final int ADD_DRAFT_ACTIVITY_REQUEST_CODE = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle Sa) {
        telephonyManager = (TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE);
        View view = inflater.inflate(R.layout.fragment_drafts, container, false);

        // Initialize the list view
        RecyclerView DraftsList = view.findViewById(R.id.DraftList);
        final DraftsListAdapter adapter = new DraftsListAdapter(getActivity(), this);
        DraftsList.setAdapter(adapter);
        DraftsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Add an observer on the LiveData returned by getAllContacts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mMessagesViewModel.getDrafts().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable final List<Message> messages) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setDrafts(messages);
                }
        });

        // Set Add Contact button and its listener
        ImageButton AddDraftButton = view.findViewById(R.id.btnCreateNewDraft);
        AddDraftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addDraft = new Intent(getActivity(), AddDraftActivity.class);
                    startActivityForResult(addDraft, ADD_DRAFT_ACTIVITY_REQUEST_CODE);
                }
        });

        // Set up Search View
        SearchView searchView = view.findViewById(R.id.SearchDrafts);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mMessagesViewModel.search(newText);
                return false;
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case(ADD_DRAFT_ACTIVITY_REQUEST_CODE) : {
                if (resultCode == RESULT_OK) {
                    // Insert new message into database
                    Message message = new Message(
                            data.getStringExtra("DraftMessage"),
                            PhoneNumberUtils.GetOwn(telephonyManager),
                            data.getStringExtra("DraftNumber"),
                            false,
                            String.valueOf(System.currentTimeMillis())         //Time of creation, will need to update when sent
                            );
                    mMessagesViewModel.insert(message);
                    Toast.makeText(getContext(), "Draft Set", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void OnDeleteClickListener(Message message) {
        // Code for Delete operation
        mMessagesViewModel.delete(message);
    }

}

