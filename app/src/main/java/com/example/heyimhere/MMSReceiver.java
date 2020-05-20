package com.example.heyimhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Todo: MMS Support. This background nonsense will receive MMS Messages eventually.
        throw new UnsupportedOperationException("WHY MMS GOTTA BE ITS OWN THING AAAAAAAAAA");
    }
}