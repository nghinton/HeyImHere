package com.example.heyimhere;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    public LiveData<List<Message>> getAll();

    // Get messages that have been sent
    @Query("SELECT * FROM messages WHERE is_sent == 1 AND is_draft == 0 ORDER BY time DESC")
    public LiveData<List<Message>> getSent();

    // Get messages that have are queued to be sent
    @Query("SELECT * FROM messages WHERE is_sent == 0 AND is_draft == 0 ORDER BY time DESC")
    public LiveData<List<Message>> getPending();

    // Get drafts of messages
    @Query("SELECT * FROM messages WHERE is_sent == 0 AND is_draft == 1 ORDER BY time DESC")
    public LiveData<List<Message>> getSaved();

    @Query("SELECT * FROM messages WHERE is_sent == 1 AND is_draft == 0 AND (body LIKE :query or receiver LIKE :query) ORDER BY id DESC")
    public LiveData<List<Message>> searchSent(String query);

    @Query("SELECT * FROM messages WHERE is_sent == 0 AND is_draft == 0 AND (body LIKE :query or receiver LIKE :query) ORDER BY id DESC")
    public LiveData<List<Message>> searchPending(String query);

    @Query("SELECT * FROM messages WHERE is_sent == 0 AND is_draft == 1 AND (body LIKE :query or receiver LIKE :query) ORDER BY id DESC")
    public LiveData<List<Message>> searchSaved(String query);

    @Query("SELECT * FROM messages WHERE id == :ID")
    public Message getMessage(int ID);

    @Query("SELECT COUNT(id) FROM messages")
    public int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Message message);

    @Update
    public void update(Message messages);

    @Delete
    public void delete(Message messages);

}
