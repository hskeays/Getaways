package com.example.getaways.dao;

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

    @Query("SELECT * FROM excursions ORDER BY id ASC;")
    List<Excursion> getAllExcursions();

    @Query("SELECT * FROM excursions WHERE id = :id LIMIT 1")
    Excursion getExcursionByID(int id);

    @Query("SELECT * FROM excursions WHERE vacationID=:vacation ORDER BY id ASC;")
    List<Excursion> getAssociatedExcursions(int vacation);

    @Query("SELECT EXISTS(SELECT * FROM excursions WHERE id = :id LIMIT 1)")
    boolean excursionExists(int id);
}
