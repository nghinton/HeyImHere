package com.example.heyimhere;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;

import java.util.List;

public class MessagesViewModel extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private MessageDao mMessageDao;
    private ContactDao mContactDao;
    private LiveData<List<Message>> mMessageList;

    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public MessagesViewModel(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mMessageDao = mDatabaseManager.mMessageDao();
        mContactDao = mDatabaseManager.mContactDao();
        mMessageList = Transformations.switchMap(searchQuery, new Function<String, LiveData<List<Message>>>() {
            @Override
            public LiveData<List<Message>> apply(String query) {
                query = "%"+query+"%";
                return mMessageDao.search(query);
            }
        });
    }

    LiveData<List<Message>> getAllMessages() {
        return mMessageList;
    }

    LiveData<List<Message>> getRelevantMessages (String phone_number) {
        return mMessageDao.getMessagesInvolving(phone_number);
    }

    LiveData<List<Message>> getReminders (String phone_number) {
        return mMessageDao.getReminders(phone_number);
    }

    LiveData<List<Message>> getDrafts () {
        return mMessageList;
    }

    void search(String query) {
        searchQuery.setValue(query);
    }

    Message getMessage (int ID) {
        return mMessageDao.getMessage(ID);
    }

    long insertMainThread(final Message message) {
        long value = mMessageDao.insert(message);
        return value;
    }

    void insert(final Message message) {
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
    }

    void delete(final Message message) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMessageDao.delete(message);
            }
        });
    }

    void update(final Message message) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMessageDao.update(message);
            }
        });
    }

    int getCount() {
        return mMessageDao.getCount();
    }
}
