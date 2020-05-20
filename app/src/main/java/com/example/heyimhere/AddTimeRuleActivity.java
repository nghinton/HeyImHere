package com.example.heyimhere;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.heyimhere.database.TimeRule;
import java.text.DateFormat;
import java.util.Calendar;

public class AddTimeRuleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    MessagesViewModel mMessagesViewModel;
    TimeRuleViewModel mTimeRuleViewModel;
    int messageID;

    TextView txtDate;
    TextView txtTime;
    private long datestamp = 0;
    private long timestamp = 0;

    private boolean hasTimeRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtimerule);

        txtDate = findViewById(R.id.txtEditDate);
        txtTime = findViewById(R.id.txtEditTime);

        // Get message id that was passed in
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            messageID = (Integer) bundle.get("messageID");
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        mTimeRuleViewModel = new ViewModelProvider(this).get(TimeRuleViewModel.class);

        // Set SetDate button and its listener
        Button setDate = findViewById(R.id.btnSetDate);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // Set SetTime button and its listener
        Button setTime = findViewById(R.id.btnSetTime);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // Set Save Contact button and its listener
        Button SaveButton = findViewById(R.id.btnSave);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRule();
            }
        });

        // Set Cancel button and its listener
        Button CancelButton = findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void setRule() {
        if (hasTimeRule) {
            if (datestamp != 0) {
                timestamp += datestamp;
            } else { //If no date given, assume today.
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0); 
                calendar.set(Calendar.MILLISECOND, 0);
                timestamp += calendar.getTime().getTime(); //calendar returns a date object which then returns a long. Looks silly.
            }
            //How are we going to handle "send this message before X time" conditions with the current UI setup?
            TimeRule timeRule = new TimeRule(messageID, false, false, timestamp);
            mTimeRuleViewModel.insert(timeRule, getApplicationContext());
            Log.i("RULE", "setRule: " + timestamp + " " + System.currentTimeMillis());
        }

        onBackPressed();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        txtDate.setText(currentDate);
        datestamp = calendar.getTime().getTime();
        hasTimeRule = true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String currentTime = DateFormat.getTimeInstance(DateFormat.LONG).format(calendar.getTime());
        txtTime.setText(currentTime);
        timestamp = 3600000 * hourOfDay + 60000 * minute;
        hasTimeRule = true;
    }

}
