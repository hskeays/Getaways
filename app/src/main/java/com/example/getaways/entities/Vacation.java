package com.example.getaways.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// ***EVALUATION, TASK B2: Include the following details for each vacation: title, hotel or other place where you will be staying, start date, end date
// Create Vacation class with all necessary details
@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String vacationTitle;
    private String hotelName;
    private String startDate;
    private String endDate;

    public Vacation(String vacationTitle, String hotelName, String startDate, String endDate) {
        this.vacationTitle = vacationTitle;
        this.hotelName = hotelName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVacationTitle() {
        return vacationTitle;
    }

    public void setVacationTitle(String vacationTitle) {
        this.vacationTitle = vacationTitle;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
