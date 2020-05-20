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

    @Query("SELECT * FROM messages WHERE is_sent == 0 ORDER BY time DESC")
    public LiveData<List<Message>> getDrafts();

    @Query("SELECT * FROM messages WHERE id == :ID")
    public Message getMessage(int ID);

    @Query("SELECT COUNT(id) FROM messages")
    public int getCount();

    @Update
    public void update(Message messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Message message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Message> messages);

    @Delete
    public void delete(Message messages);

    @Delete
    public void delete(List<Message> messages);

}
