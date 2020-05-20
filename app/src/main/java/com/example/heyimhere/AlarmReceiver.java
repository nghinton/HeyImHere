package com.example.heyimhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get message dao to interact with the database
        DatabaseManager mDatabaseManager;
        mDatabaseManager = DatabaseManager.getDatabase(context.getApplicationContext());
        MessageDao mMessageDao;
        mMessageDao = mDatabaseManager.mMessageDao();

        Bundle intentExtras = intent.getExtras();

        //Retrieve message from database
        int ID = (int) intentExtras.getLong("DraftID");
        Message message = mMessageDao.getMessage(ID);

        // Send the message
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(message.receiver, null, message.body, null, null);

        // Update the message field "is_sent"
        message.isSent = true;
        mMessageDao.update(message);

        // Toast for user verification
        Toast.makeText(context, "Draft Sent", Toast.LENGTH_SHORT).show();

    }

}
