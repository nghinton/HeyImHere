package com.example.heyimhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.heyimhere.database.Contact;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements ContactListAdapter.OnDeleteClickListener {

    private ContactViewModel mContactViewModel;
    public static final int ADD_CONTACT_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_CONTACT_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Initialize the list view
        RecyclerView ContactsList = findViewById(R.id.ContactList);
        final ContactListAdapter adapter = new ContactListAdapter(this, this);
        ContactsList.setAdapter(adapter);
        ContactsList.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Add an observer on the LiveData returned by getAllContacts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mContactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts) {
                // Update the cached copy of the words in the adapter.
                adapter.setContacts(contacts);
            }
        });

        // Set Add Contact button and its listener
        Button AddContactButton = findViewById(R.id.btnAddContact);
        AddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addContact = new Intent(ContactsActivity.this, AddContactActivity.class);
                startActivityForResult(addContact, ADD_CONTACT_ACTIVITY_REQUEST_CODE);
            }
        });

        // Implement Back button
        ImageButton BackButton = findViewById(R.id.btnBack);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up Search View
        SearchView searchView = findViewById(R.id.contactSearch);
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

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case(ADD_CONTACT_ACTIVITY_REQUEST_CODE) : {
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("ContactName");
                    String number = data.getStringExtra("ContactNumber");

                    // Insert Contact into the database
                    Contact contact = new Contact(name, number);
                    mContactViewModel.insert(contact);
                }
            }
            case(EDIT_CONTACT_ACTIVITY_REQUEST_CODE) : {
                if(resultCode == RESULT_OK) {
                    String name = data.getStringExtra("ContactName");
                    String number = data.getStringExtra("ContactNumber");

                    // Update Contact in the database
                    Contact contact = new Contact(name, number);
                    mContactViewModel.update(contact);
                }
            }
        }
    }

    @Override
    public void OnDeleteClickListener(Contact contact) {
        // Code for Delete operation
        mContactViewModel.delete(contact);
    }
}
