package com.example.heyimhere.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.heyimhere.database.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    public LiveData<List<Message>> getAll();

    @Query("SELECT * FROM messages WHERE receiver == :phone_number ORDER BY time DESC")
    public LiveData<List<Message>> getMessagesSentTo(String phone_number);

    @Query("SELECT * FROM messages WHERE sender == :phone_number ORDER BY time DESC")
    public LiveData<List<Message>> getMessagesSentFrom(String phone_number);

    @Query("SELECT * FROM messages WHERE sender == :phone_number AND receiver == :phone_number ORDER BY time DESC")
    public LiveData<List<Message>> getReminders(String phone_number);

    @Query("SELECT * FROM messages WHERE sender == :phone_number OR receiver == :phone_number ORDER BY time ASC")
    public LiveData<List<Message>> getMessagesInvolving(String phone_number);

    @Query("SELECT * FROM messages WHERE is_sent == 0 ORDER BY time DESC")
    public LiveData<List<Message>> getDrafts();

    @Query("SELECT * FROM messages WHERE is_sent == 0 AND (body LIKE :query or receiver LIKE :query)")
    public LiveData<List<Message>> search(String query);

    @Query("SELECT * FROM messages WHERE id == :ID")
    public Message getMessage(int ID);

    @Query("SELECT COUNT(id) FROM messages")
    public int getCount();

    @Update
    public void update(Message... messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Message message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Message> messages);

    @Delete
    public int delete(Message... messages);

    @Query("DELETE FROM messages")
    public void clear();
}
