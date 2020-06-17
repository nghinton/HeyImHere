package com.example.heyimhere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class Activity_NewMessage extends AppCompatActivity {

    ViewModel_Messages mMessageViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmessage);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessageViewModel = new ViewModelProvider(this).get(ViewModel_Messages.class);

        // Grab edit text boxes
        final EditText receiverField = findViewById(R.id.etxtTo);
        final EditText messageField = findViewById(R.id.etxtMessage);

        // Grab buttons and set up listeners
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just close the activity, save nothing
                finish();
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grab the edit text entries
                String message = messageField.getText().toString();
                String receiver = receiverField.getText().toString();
                // Fire the function to handle message creation
                createNewMessage(message, receiver);
            }
        });

    }

    public void createNewMessage(String message, String receiver) {
        // Create the new message
        Message newMessage = new Message(message, receiver, false, false, Long.toString(System.currentTimeMillis()));

        // Set the intents for the alarm and insert the message, the inset will return a row ID
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("MESSAGE_ID", mMessageViewModel.insert(newMessage));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm for the new message
        int interval = 8000;
        AlarmManager mAlarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);

        // Toast for user conformation
        Toast.makeText(this, "Message created!", Toast.LENGTH_SHORT).show();
    }

}
