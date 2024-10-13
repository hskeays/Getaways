package com.howardkeays.getaways.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// ***EVALUATION, TASK B4:  Include the following details for each excursion: The excursion title, The excursion date
@Entity(tableName = "excursions", foreignKeys = @ForeignKey(entity = com.howardkeays.getaways.entities.Vacation.class, parentColumns = "id", childColumns = "vacationID", onDelete = ForeignKey.CASCADE))
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String excursionTitle;
    private String excursionDate;
    private int vacationID;

    public Excursion(String excursionDate, String excursionTitle, int vacationID) {
        this.excursionDate = excursionDate;
        this.excursionTitle = excursionTitle;
        this.vacationID = vacationID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExcursionTitle() {
        return excursionTitle;
    }

    public void setExcursionTitle(String excursionTitle) {
        this.excursionTitle = excursionTitle;
    }

    public String getExcursionDate() {
        return excursionDate;
    }

    public void setExcursionDate(String excursionDate) {
        this.excursionDate = excursionDate;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }
}
