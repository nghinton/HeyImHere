package com.example.heyimhere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.heyimhere.database.TimeRule;
import com.example.heyimhere.database.TimeRuleDao;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static java.lang.String.valueOf;

public class TimeRuleManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseManager mDatabaseManager = DatabaseManager.getDatabase(context);
        TimeRuleDao mTimeRuleDao = mDatabaseManager.mTimeRuleDao();
        long currentTime = System.currentTimeMillis();
        Log.i("RULE", "onReceive: RECEIVED " + currentTime);
        //Trigger existing rules.
        //OnReceive() might not be called at the exact desired time, this should cover any time leeway it needs.
        List<TimeRule> rules = mTimeRuleDao.getRulesAroundTime(currentTime, (long)30000);
        if (rules != null) {
            Log.i("RULE", "onReceive: Rules Found");
            for (int i = 0; i < rules.size(); i++) {
                TimeRule rule = rules.get(i);
                rule.fulfilled = !rule.fulfilled;
                mTimeRuleDao.update(rule);
                RuleWrangler.CheckRules(context, rule.messageId, mDatabaseManager);
            }
        }

        //Set up next alarm.
        Long nextTime = mTimeRuleDao.getNextTime();
        if (nextTime != null) {
            AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            Intent nextRule = new Intent(context, TimeRuleManager.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, nextRule, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }

    }
}
