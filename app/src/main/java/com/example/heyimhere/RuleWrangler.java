package com.example.heyimhere;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.LocationRuleDao;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;
import com.example.heyimhere.database.TimeRule;
import com.example.heyimhere.database.TimeRuleDao;

import java.sql.Time;
import java.util.List;

//I'm sick of "manager" classes, I want to see more wrangler classes.
//Holds static methods for grabbing rules together to see if a message needs to be sent.
public class RuleWrangler {
    public static void CheckRules (Context context, int messageId, DatabaseManager db) {
        Log.i("RULE", "CheckRules: Executing");

        //Check that there are no unfulfilled rules for a message, return if there are.
        TimeRuleDao timeRuleDao = db.mTimeRuleDao();
        List<TimeRule> unfulfilledTimeRules = timeRuleDao.GetUnfulfilledRulesForMessage(messageId);
        if (unfulfilledTimeRules != null && !unfulfilledTimeRules.isEmpty()) {
            Log.i("RULE", "CheckRules: Found unfulfilled rules.");
            return; //Unfulfilled rules exist, we return.
        }

        LocationRuleDao locationRuleDao = db.mLocationRuleDao();
        List<LocationRule> unfulfilledLocationRules = locationRuleDao.GetUnfulfilledRulesForMessage(messageId);
        if (unfulfilledLocationRules != null && !unfulfilledLocationRules.isEmpty()) {
            return; //Unfulfilled rules exist, we return.
        }

        Log.i("RULE", "CheckRules: Sending");

        //If no unfulfilled rules exist for a message, then send it.
        MessageDao messageDao = db.mMessageDao();
        Message message = messageDao.getMessage(messageId);
        SmsManager smsManager = SmsManager.getDefault();
        if (!message.isSent) {
            smsManager.sendTextMessage(message.receiver, null, message.body, null, null);
            message.isSent = true;
            messageDao.update(message);
        }

        //Cleanup now vestigial rules.
        timeRuleDao.ClearRulesForId(messageId);
        locationRuleDao.ClearRulesForId(messageId);

        return;
    }
}
