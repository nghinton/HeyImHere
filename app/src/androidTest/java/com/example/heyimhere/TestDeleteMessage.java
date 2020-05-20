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
public class TestDeleteMessage {

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

                //add new contact
                contactDao.insert(receiveContact);
            }
        });
        Thread.sleep(100);
    }

    @Test
    public void deleteMessage() throws InterruptedException {

        //insert new message
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                //insert message sent to contact
                newMessage = new Message("message contents", receiveContact.number, receiveContact.number, false, "11111");
                messageDao.insert(newMessage);
            }
        });

        Thread.sleep(100);

        //delete message
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                messageDao.delete(newMessage);
            }
        });

        Thread.sleep(100);

        messageList = messageDao.getMessagesSentTo(receiveContact.number);

        //ensure that the message is deleted
        for (Message m : messageList) {
            assertEquals(true, m.time.equals("11111"));
        }
    }
}
