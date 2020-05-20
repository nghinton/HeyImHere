package com.example.heyimhere;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class PhoneNumberUtils {
    public static String Clean(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        char firstCharacter = phoneNumber.charAt(0);
        if (firstCharacter != '#') { //Seems to be a reserved symbol for amber alerts, would otherwise result in blank numbers.
            phoneNumber = phoneNumber.replaceAll("[^0-9]", ""); //Remove all non-numeric characters.
            if (phoneNumber.length() == 10) { //Add US for country code if none given. May make configurable later.
                phoneNumber = "1" + phoneNumber;
            }
        }

        return phoneNumber;
    }

    //Returns user's phone number. Make absolutely sure that you have permission to read phone state before calling.
    public static String GetOwn(TelephonyManager tMgr) {
        @SuppressLint("MissingPermission") String number = tMgr.getLine1Number();
        return Clean(number);
    }
}
