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
public class TestUpdateContact {

    LiveData<List<Contact>> contactData;
    List<Contact> contactList;

    ContactDao contactDao;
    MessageDao messageDao;

    Contact oldContact = new Contact("Test3", "1234569999");
    Contact updatedContact = new Contact("Test4", "1234569999");

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
    public void updateContact() throws InterruptedException {

        //insert new contact into database
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                contactDao.insert(oldContact);
            }
        });
        Thread.sleep(100);


        //update contact
        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                contactDao.update(updatedContact);
            }
        });
        Thread.sleep(100);



        contactList = contactDao.getAllAsList();

        //ensure that the updated contact exists & the old contact does not exist
        assertEquals(false, contactList.contains(oldContact));
        assertEquals(true, contactList.contains(updatedContact));
    }
}
