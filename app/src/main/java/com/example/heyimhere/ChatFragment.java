package com.example.heyimhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.heyimhere.database.Contact;

import java.util.List;


public class ChatFragment extends Fragment {

    private ContactViewModel mContactViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize the list view
        RecyclerView uiContactsList = view.findViewById(R.id.ContactList);
        final ChatListAdapter adapter = new ChatListAdapter(getActivity());
        uiContactsList.setAdapter(adapter);
        uiContactsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Add an observer on the LiveData returned by getAllContacts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mContactViewModel.getAllContacts().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts) {
                // Update the cached copy of the words in the adapter.
                adapter.setContacts(contacts);
            }
        });

        // Set button and its listener
        ImageButton uiCreateNewButton = view.findViewById(R.id.btnCreateNewMessage);
        uiCreateNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), NewMessageActivity.class);
                startActivity(newIntent);
            }
        });

        // Set button and its listener
        ImageButton uiContactButton = view.findViewById(R.id.btnContact);
        uiContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(newIntent);
            }
        });

        // Set up Search View
        SearchView searchView = view.findViewById(R.id.SearchContact);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mContactViewModel.search(newText);
                return false;
            }
        });

        return view;
    }



}

