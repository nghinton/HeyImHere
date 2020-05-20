

package com.example.heyimhere;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class MessagesViewModel extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private MessageDao mMessageDao;
    private LiveData<List<Message>> mMessageList;

    public MessagesViewModel(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mMessageDao = mDatabaseManager.mMessageDao();
        mMessageList = mMessageDao.getDrafts();
    }

    LiveData<List<Message>> getDrafts () {
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