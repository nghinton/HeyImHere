package com.example.heyimhere;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import com.example.heyimhere.database.MessageDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.example.heyimhere.DatabaseManager.databaseWriteExecutor;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestClearContact {

    LiveData<List<Contact>> contactData;
    List<Contact> contactList;

    ContactDao contactDao;
    MessageDao messageDao;

    ArrayList<Contact> newContacts;

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
    public void clearContact() throws InterruptedException {

        //insert contacts into database
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                contactDao.insert(new Contact("contact1", "1234567890"));
                contactDao.insert(new Contact("contact1", "0987654321"));

                contactDao.clear();
            }
        });
        Thread.sleep(100);

        contactList = contactDao.getAllAsList();

        //ensure that contact list is empty
        assertEquals(true, contactList.isEmpty());
    }
}
