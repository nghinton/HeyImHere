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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Activity_EditPending extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ViewModel_Messages mMessageViewModel;
    Message mMessage;

    // Calendar variable for easy access
    Calendar calendar = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmessage);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessageViewModel = new ViewModelProvider(this).get(ViewModel_Messages.class);

        // Grab edit text boxes
        final EditText receiverField = findViewById(R.id.etxtTo);
        final EditText messageField = findViewById(R.id.etxtMessage);
        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtTime = findViewById(R.id.txtTime);

        // Get messageID from the bundle then retrieve the message
        // and set the text boxes
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mMessage = mMessageViewModel.getMessage(bundle.getInt("messageID"));
            // Set the message and receiver text boxes
            receiverField.setText(mMessage.receiver);
            messageField.setText(mMessage.body);
            // Create a simple date format to parse the time string and set the calendar
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa mm/dd/yy");
            try {
                calendar.setTime(sdf.parse(mMessage.time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Set the time and date text boxes
            txtDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
            txtTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        }

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
                if (isNumberValid && isMessageValid && isTimeValid) {
                    // Fire the function to handle message creation
                    // I can grab the calender since its global so only need to pass in the message and receiver
                    updateMessage(message, receiver);

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

    public void updateMessage(String message, String receiver) {
        // Format the time into a string and update all fields
        String formattedTime = Utility.formatTime(calendar);
        mMessage.receiver = receiver;
        mMessage.body = message;
        mMessage.time = formattedTime;

        // Update the message
        mMessageViewModel.update(mMessage);

        // Toast for user conformation
        Toast.makeText(this, "Message updated!", Toast.LENGTH_SHORT).show();
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
