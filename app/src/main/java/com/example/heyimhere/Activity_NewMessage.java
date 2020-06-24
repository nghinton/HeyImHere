package com.example.heyimhere;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import java.text.DateFormat;
import java.util.Calendar;

public class Activity_NewMessage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ViewModel_Messages mMessageViewModel;

    // Calendar variable for easy access
    Calendar calendar = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmessage);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessageViewModel = new ViewModelProvider(this).get(ViewModel_Messages.class);

        // Grab edit text boxes
        final EditText receiverField = findViewById(R.id.etxtTo);
        final EditText messageField = findViewById(R.id.etxtMessage);

        // Pick Time Button Setup
        Button btnTime = findViewById(R.id.btnPickTime);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the date picker fragment
                DialogFragment timePicker = new PickerTimeFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // Pick Date Button Setup
        Button btnDate = findViewById(R.id.btnPickDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the date picker fragment
                DialogFragment datePicker = new PickerDateFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

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
                // Validate the time
                boolean isTimeValid = Utility.validateTime(time);

                // Check that user input is at least mildly correct
                if(isNumberValid && isMessageValid && isTimeValid) {
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
                    if (!isTimeValid) {
                        Toast.makeText(v.getContext(), "Live in the future, not the past", Toast.LENGTH_LONG).show();
                    }
                }

                // Let the activity keep going
            }
        });

    }

    public void createNewMessage(String message, String receiver) {
        // Format the time into a string and create the new message
        String formattedTime = Utility.formatTime(calendar);
        Message newMessage = new Message(message, receiver, false, false, formattedTime);

        // Set the intents for the alarm and insert the message, the inset will return a row ID
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("MESSAGE_ID", mMessageViewModel.insert(newMessage));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // Set the alarm for the new message
        AlarmManager mAlarmManager = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Toast for user conformation
        Toast.makeText(this, "Message created!", Toast.LENGTH_SHORT).show();
    }

    // Date picker listener
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Grab and set the date
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        // Grab the txt box and display the dat
        TextView txtDate = findViewById(R.id.txtDate);
        txtDate.setText(currentDate);
    }

    // Time picker listener
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Grab and set the time
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        // Grab the txt box and display time
        TextView txtTime = findViewById(R.id.txtTime);
        txtTime.setText(currentTime);

    }


}
