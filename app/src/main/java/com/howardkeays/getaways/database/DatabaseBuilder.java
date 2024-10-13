package com.howardkeays.getaways.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.howardkeays.getaways.dao.ExcursionDAO;
import com.howardkeays.getaways.dao.VacationDAO;
import com.howardkeays.getaways.entities.Excursion;
import com.howardkeays.getaways.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 2, exportSchema = false)
public abstract class DatabaseBuilder extends RoomDatabase {
    private static volatile DatabaseBuilder INSTANCE;

    static DatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseBuilder.class, "Getaways.db").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract VacationDAO vacationDAO();

    public abstract ExcursionDAO excursionDAO();
}
