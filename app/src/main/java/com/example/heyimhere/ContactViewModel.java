package com.example.heyimhere;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private ContactDao mContactDao;
    private LiveData<List<Contact>> mContactList;

    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public ContactViewModel(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mContactDao = mDatabaseManager.mContactDao();
        mContactList = Transformations.switchMap(searchQuery, new Function<String, LiveData<List<Contact>>>() {
            @Override
            public LiveData<List<Contact>> apply(String query) {
                query = "%"+query+"%";
                return mContactDao.search(query);
            }
        });
    }

    LiveData<List<Contact>> getAllContacts() {
        return mContactList;
    }

    void search(String query) {
        searchQuery.setValue(query);
    }

    Contact getContactByPhoneNumber(String phoneNumber) {
        return mContactDao.getContactByPhoneNumber(phoneNumber);
    }

    Contact getContactByName(String Name) {
        return mContactDao.getContactByPhoneNumber(Name);
    }

    void insertMainThread(final Contact contact) {
        mContactDao.insert(contact);
    }

    void insert(final Contact contact) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mContactDao.insert(contact);
            }
        });
    }

    void update(final Contact contact) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mContactDao.update(contact);
            }
        });
    }

    void delete(final Contact contact) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mContactDao.delete(contact);
            }
        });
    }

    public int getCount() {
        return mContactDao.getCount();
    }

}
