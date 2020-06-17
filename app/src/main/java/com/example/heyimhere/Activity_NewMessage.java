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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Activity_NewMessage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ViewModel_Messages mMessageViewModel;

    // Date and Time variable for easy acccess
    private long dateStamp = 0;
    private long timeStamp = 0;

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
                long time = timeStamp + dateStamp;

                // Fire the function to handle message creation
                createNewMessage(message, receiver, time);

                // Close the activity
                finish();
            }
        });

    }

    public void createNewMessage(String message, String receiver, long time) {
        // Format the time into a string and create the new message
        Date sendAt = new Date(time);
        DateFormat df = new SimpleDateFormat("dd:MM:yyyy - HH:mm:ss");
        Message newMessage = new Message(message, receiver, false, false, df.format(sendAt));

        // Set the intents for the alarm and insert the message, the inset will return a row ID
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("MESSAGE_ID", mMessageViewModel.insert(newMessage));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm for the new message
        AlarmManager mAlarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        // Toast for user conformation
        Toast.makeText(this, "Message created!", Toast.LENGTH_SHORT).show();
    }

    // Date picker listener
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Grab the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        // Set the date stamp
       dateStamp = calendar.getTimeInMillis();

        // Grab the txt box and display the dat
        TextView txtDate = findViewById(R.id.txtDate);
        txtDate.setText(currentDate);
    }

    // Time picker listener
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Grab and format date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 0);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String currentTime = DateFormat.getTimeInstance(DateFormat.LONG).format(calendar.getTime());

        // Set the time stamp
        timeStamp = calendar.getTimeInMillis();

        // Grab the txt box and display time
        TextView txtTime = findViewById(R.id.txtTime);
        txtTime.setText(currentTime);

    }


}
