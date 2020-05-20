package com.example.heyimhere;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.ContactDao;
import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.LocationRuleDao;
import com.example.heyimhere.database.Message;
import com.example.heyimhere.database.MessageDao;
import com.example.heyimhere.database.TimeRule;
import com.example.heyimhere.database.TimeRuleDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Contact.class, Message.class, TimeRule.class, LocationRule.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {

    // Getters for DAOs
    public abstract ContactDao mContactDao();
    public abstract MessageDao mMessageDao();
    public abstract TimeRuleDao mTimeRuleDao();
    public abstract LocationRuleDao mLocationRuleDao();

    // Define the Singleton
    private static volatile DatabaseManager INSTANCE;

    // Create thread pool to asynchronously run database tasks in the background
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);



    // Database creation
    static DatabaseManager getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            DatabaseManager.class, "Database")
                            .allowMainThreadQueries() //Replace with with async queries later for the love of god and performance.
                            //I'm just getting stuff working for sprint 2.
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     *
     * If you want to populate the database only when the database is created for the 1st time,
     * override RoomDatabase.Callback()#onCreate
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Populate the database for testing.

                }
            });
        }
    };
}
