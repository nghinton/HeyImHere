package com.example.heyimhere;

import android.content.Context;

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
public class TestUpdateMessage {

    List<Message> messageList;

    ContactDao contactDao;
    MessageDao messageDao;

    Message newMessage;
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
    public void updateMessage() throws InterruptedException {

        //insert test contact
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contactDao.insert(receiveContact);
            }
        });

        Thread.sleep(100);

        //insert new message
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                newMessage = new Message("message contents", receiveContact.number, receiveContact.number, false, "11111");
                newMessage.id = 1;
                messageDao.insert(newMessage);
            }
        });

        Thread.sleep(100);

        //update message
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Message updatedMessage = new Message("updated contents", receiveContact.number, receiveContact.number, false, "11111");
                updatedMessage.id = 1;
                messageDao.update(updatedMessage);
            }
        });

        Thread.sleep(100);

        messageList = messageDao.getMessagesSentTo(receiveContact.number);

        //ensure that the message content matches
        assertEquals(true, messageList.get(0).body.equals("updated contents"));
    }
}
