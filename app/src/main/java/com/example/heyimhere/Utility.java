package com.example.heyimhere;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.DateFormat;
import java.util.Calendar;

public class Utility {

    public static boolean validatePhoneNumber(String number) {

        // Turn the number into a phone number object
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(number, "US"); // yes I am only checking for US valid phone numbers
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }

        // Function that returns a boolean
        return phoneUtil.isValidNumber(phoneNumber);
    }

    public static boolean validateMessage(String message) {

        // Make sure the message is not empty
        if (message.length() == 0) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean validateTime(long time) {

        // Make sure the time is not set to before the time right now
        if (time <= System.currentTimeMillis()) {
            return false;
        } else {
            return true;
        }

    }

    public static String formatTime(Calendar calendar) {

        // Grab and format the date and time strings
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        // Add them together
        String formattedTime = currentTime + " " + currentDate;

        // Return the pretty string
        return formattedTime;
    }

}
