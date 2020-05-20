package com.example.heyimhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;

import static java.lang.String.valueOf;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";


    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        DatabaseManager mDatabaseManager = DatabaseManager.getDatabase(context);
        final ContactDao mContactDao = mDatabaseManager.mContactDao();
        final MessageDao mMessageDao = mDatabaseManager.mMessageDao();

        if (intentExtras != null) {
            //Gets user's phone number.
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String ownNumber = PhoneNumberUtils.GetOwn(tMgr);

            //Get every text and add to DB.
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                String smsBody = smsMessage.getMessageBody();
                String sender = smsMessage.getOriginatingAddress();
                String timeStamp = valueOf(smsMessage.getTimestampMillis());

                final Message message = new Message(smsBody, sender, ownNumber, true, timeStamp);

                mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mContactDao.getContactByPhoneNumber(message.sender) == null) {
                            Contact contact = new Contact(message.sender, message.sender);
                            mContactDao.insert(contact);
                        }
                        if (mContactDao.getContactByPhoneNumber(message.receiver) == null) {
                            Contact contact = new Contact(message.receiver, message.receiver);
                            mContactDao.insert(contact);
                        }
                        mMessageDao.insert(message);
                    }
                });
                Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
