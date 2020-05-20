package com.example.heyimhere.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Time;
import java.util.List;

@Dao
public interface TimeRuleDao {
    @Query("SELECT * FROM TimeRule")
    public LiveData<List<TimeRule>> getAll();

    @Query("SELECT * FROM TimeRule WHERE messageId == :id")
    public LiveData<List<TimeRule>> getRulesForMessage(int id);

    //Get next time rule that will trigger.
    @Query("SELECT MIN(time) FROM TimeRule WHERE fulfilled = 0")
    public Long getNextTime();

    //Returns a list of TimeRules that will trigger at roughly the current time.
    @Query("SELECT * FROM TimeRule WHERE ABS(time - :currentTime) < :leeway AND fulfilled == beforeTime")
    public List<TimeRule> getRulesAroundTime(long currentTime, long leeway);

    @Query("SELECT * FROM TimeRule WHERE messageId == :id AND fulfilled == beforeTime")
    public List<TimeRule> GetUnfulfilledRulesForMessage(int id);

    @Query("DELETE FROM TimeRule WHERE messageId == :id")
    public void ClearRulesForId(int id);

    @Update
    public void update(TimeRule rule);

    @Update
    public void update(List<TimeRule> rules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(TimeRule rule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<TimeRule> rules);

    @Delete
    public int delete(TimeRule rule);

    @Delete
    public int delete(List<TimeRule> rules);
}
