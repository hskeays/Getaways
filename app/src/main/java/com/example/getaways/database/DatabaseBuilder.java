package com.example.getaways.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.getaways.dao.ExcursionDAO;
import com.example.getaways.dao.VacationDAO;
import com.example.getaways.entities.Excursion;
import com.example.getaways.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
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
