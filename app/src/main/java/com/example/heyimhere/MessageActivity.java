package com.example.heyimhere;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heyimhere.database.Message;

import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private static MessageActivity inst; //Copy of the MainActivity instance.
    private static final int SMS_PERMISSION = 1;
    private static boolean isActive = false;
    private ImageButton uiBackButton;
    private TextView uiContactName;
    private TextView uiContactNumber;
    private EditText uiInputBox;
    private Button uiSendButton;
    private static SmsManager smsManager;
    private String phoneNumber;
    private String phoneName;
    private String ownNumber;
    private MessagesViewModel mMessagesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Grab UI elements
        uiContactName = findViewById(R.id.contactName);
        uiContactNumber = findViewById(R.id.contactNumber);
        uiInputBox = findViewById(R.id.etTextInput);
        uiBackButton = findViewById(R.id.btnBack);
        uiBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        uiSendButton = findViewById(R.id.btnSend);
        uiSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick();
            }
        });

        //Get phone number for current contact.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = (String)bundle.get("number");
            phoneName = (String)bundle.get("name");
        }
        uiContactNumber.setText(phoneNumber);
        uiContactName.setText(phoneName);

        //Gets user's phone number.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        ownNumber = PhoneNumberUtils.GetOwn(tMgr);

        // Initialize the list view
        RecyclerView uiContactsList = findViewById(R.id.lstMessages);
        final MessageListAdapter adapter = new MessageListAdapter(this);
        uiContactsList.setAdapter(adapter);
        uiContactsList.setLayoutManager(new LinearLayoutManager(this));

        // Get a view model
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        //Add an observer on the LiveData returned by getAllContacts.
        //The onChanged() method fires when the observed data changes and the activity is
        //in the foreground.
        if (!phoneNumber.equals(ownNumber)) {
            mMessagesViewModel.getRelevantMessages(phoneNumber).observe(this, new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable final List<Message> messages) {
                    //Update the cached copy of the words in the adapter.
                    adapter.setTexts(messages);
                }
            });
        } else { //Deal with special case where you're viewing messages you sent to yourself.
            mMessagesViewModel.getReminders(phoneNumber).observe(this, new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable final List<Message> messages) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setTexts(messages);
                }
            });
        }

        // Initialize SMS Manager
        smsManager = SmsManager.getDefault();

        // Get Permissions
        if(!CheckSmsPermission()) {
            GetPermissionToSendSMS();
        }

    }

    public void onSendClick() {
        String message = uiInputBox.getText().toString();
        if (CheckSmsPermission() && message.length() > 0) {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Message text = new Message(message, ownNumber, phoneNumber, true, String.valueOf(System.currentTimeMillis()));
            //TODO: add checks to see if message was actually received.
            mMessagesViewModel.insert(text);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to send blank message.", Toast.LENGTH_SHORT).show();
        }
        uiInputBox.getText().clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION: {
                if (CheckSmsPermission()) {
                    return;
                } else {
                    Toast.makeText(this, "Cannot send messages, please grant the app SMS permissions in settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean CheckSmsPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void GetPermissionToSendSMS() {
        if (!CheckSmsPermission()) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "This permission is required in order for the app to send your messages.", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION);
        }
    }


    // Misc functions
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    public static MessageActivity GetInstance() {
        return inst;
    }

    public static boolean GetIsActive() {
        return isActive;
    }

}
