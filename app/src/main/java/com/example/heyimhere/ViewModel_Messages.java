

package com.example.heyimhere;

import android.app.Application;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import java.util.List;

public class ViewModel_Messages extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private MessageDao mMessageDao;
    private LiveData<List<Message>> mSentList;
    private LiveData<List<Message>> mPendingList;
    private LiveData<List<Message>> mSavedList;

    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    public ViewModel_Messages(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mMessageDao = mDatabaseManager.mMessageDao();

        // Set up the different strings for searching
        mSentList = Transformations.switchMap(searchQuery, new Function<String, LiveData<List<Message>>>() {
            @Override
            public LiveData<List<Message>> apply(String query) {
                query = "%"+query+"%";
                return mMessageDao.searchSent(query);
            }
        });
        mPendingList = Transformations.switchMap(searchQuery, new Function<String, LiveData<List<Message>>>() {
            @Override
            public LiveData<List<Message>> apply(String query) {
                query = "%"+query+"%";
                return mMessageDao.searchPending(query);
            }
        });
        mSavedList = Transformations.switchMap(searchQuery, new Function<String, LiveData<List<Message>>>() {
            @Override
            public LiveData<List<Message>> apply(String query) {
                query = "%"+query+"%";
                return mMessageDao.searchSaved(query);
            }
        });
    }

    void searchSent(String query) {
        searchQuery.setValue(query);
    }

    void searchPending(String query) {
        searchQuery.setValue(query);
    }

    void searchSaved(String query) {
        searchQuery.setValue(query);
    }

    LiveData<List<Message>> getSent () {
        return mSentList;
    }

    LiveData<List<Message>> getPending () {
        return mPendingList;
    }

    LiveData<List<Message>> getSaved () {
        return mSavedList;
    }

    long insert(final Message message) {
        long id = mMessageDao.insert(message);
        return id;
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