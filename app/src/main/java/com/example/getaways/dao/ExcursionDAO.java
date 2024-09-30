package com.example.getaways.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.getaways.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("DELETE FROM excursions WHERE vacationID = :vacationID")
    void deleteAllAssociatedExcursions(int vacationID);

    @Query("SELECT * FROM excursions ORDER BY id ASC;")
    LiveData<List<Excursion>> getAllExcursions();

    @Query("SELECT * FROM excursions WHERE id = :id LIMIT 1")
    LiveData<Excursion> getExcursionByID(int id);

    @Query("SELECT * FROM excursions WHERE vacationID=:vacation ORDER BY id ASC;")
    LiveData<List<Excursion>> getAssociatedExcursions(int vacation);

    @Query("SELECT EXISTS(SELECT * FROM excursions WHERE id = :id LIMIT 1)")
    LiveData<Boolean> excursionExists(int id);
}
