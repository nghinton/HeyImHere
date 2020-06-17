package com.example.heyimhere;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Message.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {

    // Getters for DAOs
    public abstract MessageDao mMessageDao();

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
                            .allowMainThreadQueries()
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
 /*                   MessageDao mMessageDao = INSTANCE.mMessageDao();
                    for (int i=0; i<25; ++i) {
                        Message current = new Message("Hi its me", "2058615449", false, false, "1");
                        int rand = i % 3;
                        switch(rand) {
                            case 0:
                                mMessageDao.insert(current);
                                break;
                            case 1:
                                current.isSent = true;
                                mMessageDao.insert(current);
                                break;
                            case 2:
                                current.isDraft = true;
                                mMessageDao.insert(current);
                                break;
                        }
                    }
*/
                }
            });
        }
    };
}
