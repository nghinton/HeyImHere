package com.example.heyimhere;

import android.content.Context;

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
public class TestPopulateContact {

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
    public void populateContact() throws InterruptedException {

        //Create a list of contacts
        newContacts = new ArrayList<Contact>();
        for (int i=0; i< 10; i++) {
            newContacts.add(new Contact("testContact" + Integer.toString(i), Integer.toString(i * 1111111111)));
        }

        //insert contact list into database
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contactDao.insert(newContacts);
            }
        });

        Thread.sleep(100);
        contactList = contactDao.getAllAsList();

        //ensure that each added contact is in the database
        for (Contact c : newContacts) {
            assertEquals(true, contactList.contains(c));

        }
    }
}
