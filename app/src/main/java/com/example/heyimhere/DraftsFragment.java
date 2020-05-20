package com.example.heyimhere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DraftsFragment extends Fragment {

    private MessagesViewModel mMessagesViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle Sa) {
        View view = inflater.inflate(R.layout.fragment_drafts, container, false);

        // Initialize the list view
        RecyclerView DraftsList = view.findViewById(R.id.DraftList);
        final DraftsListAdapter adapter = new DraftsListAdapter(getActivity());
        DraftsList.setAdapter(adapter);
        DraftsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

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

        return view;
    }

}

