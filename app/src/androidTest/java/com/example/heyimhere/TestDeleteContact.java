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

import java.util.List;

import static com.example.heyimhere.DatabaseManager.databaseWriteExecutor;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestDeleteContact {

    LiveData<List<Contact>> contactData;
    List<Contact> contactList;

    ContactDao contactDao;
    MessageDao messageDao;

    Contact newContact = new Contact("Test4", "9876543333");

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
    public void deleteContact() throws InterruptedException {

        //insert new contact
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                contactDao.insert(newContact);
            }
        });

        //delete contact
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                contactDao.delete(newContact);
            }
        });

        Thread.sleep(100);
        contactList = contactDao.getAllAsList();

        //ensure that the deleted contact does not exist
        assertEquals(false, contactList.contains(newContact));
    }
}
