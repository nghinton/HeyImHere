package com.example.heyimhere.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.heyimhere.PhoneNumberUtils;

@Entity(tableName = "Contacts")
public class Contact {
    //Needs to be replaced with a generic ID field once we move to having a
    //separate table for phone numbers to support multiple numbers per contact.
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "number")
    public String number;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "last_contact_time")
    public String lastContactTime;

    //public String pictureURI;
    public Contact(String name, String number) {
        this.name = name;
        this.number = PhoneNumberUtils.Clean(number);
    }

    @Override
    public boolean equals(Object obj) {
        Contact c = (Contact) obj;
        if (this.name.equals(c.name) && this.number.equals(c.number)) return true;
        return false;
    }

}
