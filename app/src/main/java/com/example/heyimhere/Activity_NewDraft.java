package com.example.heyimhere;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Calendar;

public class Activity_NewDraft extends AppCompatActivity  {

    ViewModel_Messages mMessageViewModel;

    // Calendar variable for easy access
    Calendar calendar = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdraft);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessageViewModel = new ViewModelProvider(this).get(ViewModel_Messages.class);

        // Grab edit text boxes
        final EditText receiverField = findViewById(R.id.etxtTo);
        final EditText messageField = findViewById(R.id.etxtMessage);

        // Cancel Button Setup
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just close the activity, save nothing
                finish();
            }
        });

        // Create Button Setup
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Grab the edit text entries
                String message = messageField.getText().toString();
                String receiver = receiverField.getText().toString();

                // Add the time and date stamps to get the time in milliseconds
                long time = calendar.getTimeInMillis();

                // Validate the phone number
                boolean isNumberValid = Utility.validatePhoneNumber(receiver);
                // Validate the message
                boolean isMessageValid = Utility.validateMessage(message);

                // Check that user input is at least mildly correct
                if(isNumberValid && isMessageValid) {
                    // Fire the function to handle message creation
                    // I can grab the calender since its global so only need to pass in the message and receiver
                    createNewMessage(message, receiver);

                    // Close the activity
                    finish();
                } else {
                    // Throw a toast to let the user know they fucked up
                    if (!isMessageValid) {
                        Toast.makeText(v.getContext(), "Cannot send empty message", Toast.LENGTH_SHORT).show();
                    }
                    if (!isNumberValid) {
                        Toast.makeText(v.getContext(), "Phone number is invalid", Toast.LENGTH_LONG).show();
                    }
                }

                // Let the activity keep going
            }
        });

    }

    public void createNewMessage(String message, String receiver) {
        // Create and insert the new draft
        Message newMessage = new Message(message, receiver, false, true, null);
        mMessageViewModel.insert(newMessage);

        // Toast for user conformation
        Toast.makeText(this, "Draft created!", Toast.LENGTH_SHORT).show();
    }

}
