package com.example.heyimhere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Fragment_Pending extends Fragment implements Adapter_List_Sent.OnDeleteClickListener {

    private ViewModel_Messages mMessagesViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle Sa) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        // Initialize the list view
        RecyclerView DraftsList = view.findViewById(R.id.DraftList);
        final Adapter_List_Pending adapter = new Adapter_List_Pending(getActivity(), this);
        DraftsList.setAdapter(adapter);
        DraftsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessagesViewModel = new ViewModelProvider(this).get(ViewModel_Messages.class);

        // Add an observer on the LiveData returned by getAllContacts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mMessagesViewModel.getPending().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable final List<Message> messages) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setPending(messages);
                }
        });

        return view;
    }

    @Override
    public void OnDeleteClickListener(Message message) {
        mMessagesViewModel.delete(message);
    }

}

