package com.example.heyimhere;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.example.heyimhere.DatabaseManager.databaseWriteExecutor;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestGetMessagesSentTo {

    List<Message> messageList;

    ContactDao contactDao;
    MessageDao messageDao;

    Contact receiveContact = new Contact("contactName", "1234567890");

    @Before
    public void clearDatabase() throws InterruptedException {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final Context context = appContext.getApplicationContext();
        contactDao = DatabaseManager.getDatabase(context).mContactDao();
        messageDao = DatabaseManager.getDatabase(context).mMessageDao();

        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                messageDao.clear();
                contactDao.clear();
            }
        });
        Thread.sleep(100);
    }

    @Test
    public void getMessages() throws InterruptedException {

        //insert new contact into database
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contactDao.insert(receiveContact);
            }
        });

        Thread.sleep(100);

        //insert new contact into database
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                //send 4 messages to the target contact
                for (int i=1; i<5; i++) {
                    Message newMessage = new Message("message contents", receiveContact.number, receiveContact.number, true, "11111");
                    newMessage.id = i;
                    newMessage.body = "message contents " + i;
                    newMessage.isSent = true;
                    newMessage.receiver = receiveContact.number;
                    messageDao.insert(newMessage);
                }

            }
        });

        Thread.sleep(100);

        messageList = messageDao.getMessagesSentTo(receiveContact.number);

        //check message list
        int matches = 0;
        for (Message m : messageList) {
            if (m.id >= 1 && m.id <= 4) {
                matches++;
            }
        }
        assertEquals(true, matches  == 4);
    }
}
