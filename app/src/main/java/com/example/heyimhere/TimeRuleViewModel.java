package com.example.heyimhere;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.heyimhere.database.TimeRule;
import com.example.heyimhere.database.TimeRuleDao;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class TimeRuleViewModel extends AndroidViewModel {

    private DatabaseManager mDatabaseManager;
    private TimeRuleDao mTimeRuleDao;
    private LiveData<List<TimeRule>> mTimeRules;

    public TimeRuleViewModel(Application application) {
        super(application);
        mDatabaseManager = DatabaseManager.getDatabase(application);
        mTimeRuleDao = mDatabaseManager.mTimeRuleDao();
        mTimeRules = mTimeRuleDao.getAll();
    }

    LiveData<List<TimeRule>> getTimeRules() {
        return mTimeRules;
    }

    LiveData<List<TimeRule>> getRulesForMessage (int id) {
        return mTimeRuleDao.getRulesForMessage(id);
    }

    void insert(final TimeRule rule, final Context context) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTimeRuleDao.insert(rule);
                long nextTime = mTimeRuleDao.getNextTime();
                if (rule.time == nextTime) {
                    AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Intent nextRule = new Intent(context, TimeRuleManager.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, nextRule, PendingIntent.FLAG_UPDATE_CURRENT);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, rule.time, pendingIntent);
                }
            }
        });
    }

    void update(final TimeRule rule) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTimeRuleDao.update(rule);
            }
        });
    }

    void delete(final TimeRule rule) {
        mDatabaseManager.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTimeRuleDao.delete(rule);
            }
        });
    }

}
