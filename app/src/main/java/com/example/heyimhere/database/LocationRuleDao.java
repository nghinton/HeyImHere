package com.example.heyimhere.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationRuleDao {
    @Query("SELECT * FROM LocationRule")
    public LiveData<List<LocationRule>> getAll();

    @Query("SELECT * FROM LocationRule")
    public List<LocationRule> getAllList();

    @Query("SELECT * FROM LocationRule WHERE messageId == :id")
    public LiveData<List<LocationRule>> getRulesForMessage(int id);

    //Returns a list of LocationRules that MIGHT trigger at the current location.
    //Results must be verified in java using proper math, since SQL doesn't support trig functions.
    //Uses Distance squared with a generous distance measurement based on the equator.
    @Query("SELECT * FROM LocationRule WHERE" +
            "((radius * radius) / 4788.64) >" +
            "     ((longitude - :currLong) * (longitude - :currLong) + " +
            "     (latitude - :currLat) * (latitude - :currLat))" +
            "AND fulfilled == outsideSpot")
    public List<LocationRule> getRulesAroundSpot(double currLong, double currLat);

    @Query("SELECT * FROM LocationRule WHERE messageId == :id AND fulfilled == outsideSpot")
    public List<LocationRule> GetUnfulfilledRulesForMessage(int id);

    @Query("DELETE FROM LocationRule WHERE messageId == :id")
    public void ClearRulesForId(int id);

    @Update
    public void update(LocationRule rule);

    @Update
    public void update(List<LocationRule> rules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(LocationRule rule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<LocationRule> rules);

    @Delete
    public int delete(LocationRule rule);

    @Delete
    public int delete(List<LocationRule> rules);
}
