package com.example.heyimhere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.Message;
import java.util.ArrayList;
import static com.example.heyimhere.PhoneNumberUtils.Clean;

public class NewMessageActivity extends AppCompatActivity {
    private static NewMessageActivity inst;
    private static final int SMS_PERMISSION = 1;

    private static SmsManager smsManager;
    ContactViewModel mContactViewModel;
    MessagesViewModel mMessagesViewModel;

    String ownNumber;

    private ImageButton uiBackButton;
    private EditText uiPhoneNumber;
    private EditText uiInputBox;
    private Button uiSendButton;
    private ListView uiMessageList;
    private ArrayList<String> messageList;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        // Grab UI elements
        uiBackButton = findViewById(R.id.btnBack);
        uiInputBox = findViewById(R.id.etTextInput);
        uiPhoneNumber = findViewById(R.id.etPhoneNumber);
        uiSendButton = findViewById(R.id.btnSend);
        uiMessageList = findViewById(R.id.lstMessages);

        // Set up button listeners
        uiBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        uiSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick();
            }
        });

        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        //Gets user's phone number.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        ownNumber = PhoneNumberUtils.GetOwn(tMgr);

        // Initialize SMS Manager
        smsManager = SmsManager.getDefault();

        // Get Permissions
        if(!CheckSmsPermission()) {
            GetPermissionToSendSMS();
        }

    }

    public void onSendClick() {
        // Get message values
        String body = uiInputBox.getText().toString();
        String phoneNumber = uiPhoneNumber.getText().toString();

        // Clean Phone number
        phoneNumber = Clean(phoneNumber);

        // Check message validity and send
        if (CheckSmsPermission() && body.length() > 0 && phoneNumber.length() > 0) {
            // Create new contact if the number isn't already in the DB
            if (mContactViewModel.getContactByPhoneNumber(phoneNumber) == null) {
                Contact contact = new Contact("NO NAME", phoneNumber);
                mContactViewModel.insert(contact);
            }

            // Send message
            smsManager.sendTextMessage(phoneNumber, null, body, null, null);

            // Add message to DB
            Message message = new Message(body, ownNumber, phoneNumber, true, Long.toString(System.currentTimeMillis()));
            mMessagesViewModel.insert(message);

            // Exit activity
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            finish();

        } else if (body.length() == 0) {
            Toast.makeText(this, "Unable to send blank message.", Toast.LENGTH_SHORT).show();
        } else if (phoneNumber.length() == 0) {
            Toast.makeText(this, "Unable to send message. Please specify a phone number.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to send message. Please change your permissions in settings.", Toast.LENGTH_SHORT).show();
        }
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

}
