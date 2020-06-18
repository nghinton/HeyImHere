package com.example.heyimhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get message dao to interact with the database
        DatabaseManager mDatabaseManager;
        MessageDao mMessageDao;
        mDatabaseManager = DatabaseManager.getDatabase(context.getApplicationContext());
        mMessageDao = mDatabaseManager.mMessageDao();

        // Grab the bundle and the message id in it
        Bundle intentExtras = intent.getExtras();
        assert intentExtras != null;
        int ID = (int) intentExtras.getLong("MESSAGE_ID");

        //Retrieve message from database
        Message message = mMessageDao.getMessage(ID);

        // Send the message
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(message.receiver, null, message.body, null, null);

        // Update the message field "is_sent" and "time"
        message.isSent = true;
        Calendar calendar = Calendar.getInstance();
        String formattedTime = Utility.formatTime(calendar);
        message.time = formattedTime;
        mMessageDao.update(message);

        // Toast for user verification
        Toast.makeText(context, "Draft Sent", Toast.LENGTH_SHORT).show();

    }

}
