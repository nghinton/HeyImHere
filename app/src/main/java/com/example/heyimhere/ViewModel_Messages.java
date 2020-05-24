

package com.example.heyimhere;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ViewModel_Messages extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private MessageDao mMessageDao;
    private LiveData<List<Message>> mMessageList;

    public ViewModel_Messages(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mMessageDao = mDatabaseManager.mMessageDao();
    }

    LiveData<List<Message>> getSent () {
        mMessageList = mMessageDao.getSent();
        return mMessageList;
    }

    LiveData<List<Message>> getPending () {
        mMessageList = mMessageDao.getPending();
        return mMessageList;
    }

    LiveData<List<Message>> getSaved () {
        mMessageList = mMessageDao.getSaved();
        return mMessageList;
    }

    void insert(final Message message) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
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