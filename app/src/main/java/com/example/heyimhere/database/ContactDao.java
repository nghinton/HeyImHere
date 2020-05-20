package com.example.heyimhere.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.nio.channels.SelectableChannel;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts")
    public LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM contacts WHERE number == :phone_number")
    public Contact getContactByPhoneNumber(String phone_number);

    @Query("SELECT * FROM contacts WHERE number == :name")
    public Contact getContactByName(String name);

    @Query("SELECT * FROM contacts WHERE name LIKE :query OR number LIKE :query")
    public LiveData<List<Contact>> search(String query);

    @Query("SELECT COUNT(number) FROM contacts")
    public int getCount();

    @Update
    public void update(Contact... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Contact contact);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Contact> contacts);

    @Delete
    public int delete(Contact... contacts);

    @Query("DELETE FROM contacts")
    public void clear();

    @Query("SELECT * FROM contacts")
    public List<Contact> getAllAsList();
}
