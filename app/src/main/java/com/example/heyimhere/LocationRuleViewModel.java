package com.example.heyimhere;

import android.app.Application;
import android.content.Context;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.LocationRuleDao;

import java.util.List;

public class LocationRuleViewModel extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private LocationRuleDao mLocationRuleDao;
    private LiveData<List<LocationRule>> mLocationRules;

    public LocationRuleViewModel(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mLocationRuleDao = mDatabaseManager.mLocationRuleDao();
        mLocationRules = mLocationRuleDao.getAll();
    }

    LiveData<List<LocationRule>> getLocationRules() {
        return mLocationRules;
    }

    LiveData<List<LocationRule>> getRulesForMessage (int id) {
        return mLocationRuleDao.getRulesForMessage(id);
    }

    void insert(final LocationRule rule, final Context context) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mLocationRuleDao.insert(rule);
            }
        });
    }

    void update(final LocationRule rule) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mLocationRuleDao.update(rule);
            }
        });
    }

    void delete(final LocationRule rule) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mLocationRuleDao.delete(rule);
            }
        });
    }

}
