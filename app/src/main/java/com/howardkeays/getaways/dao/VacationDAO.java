package com.howardkeays.getaways.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.howardkeays.getaways.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY id ASC")
    LiveData<List<Vacation>> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :id LIMIT 1")
    LiveData<Vacation> getVacationByID(int id);

    @Query("SELECT EXISTS(SELECT * FROM vacations WHERE id = :id LIMIT 1)")
    LiveData<Boolean> vacationExists(int id);

    @Query(("SELECT id FROM vacations ORDER BY id DESC LIMIT 1"))
    LiveData<Integer> getLastInsertedVacationID();
}
