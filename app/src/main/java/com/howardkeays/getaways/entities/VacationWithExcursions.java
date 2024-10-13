package com.howardkeays.getaways.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class VacationWithExcursions {
    @Embedded
    private Vacation vacation;

    @Relation(parentColumn = "id", entityColumn = "vacationID")
    private List<Excursion> excursions;

    // Constructor, getters, and setters
    public Vacation getVacation() {
        return vacation;
    }

    public void setVacation(Vacation vacation) {
        this.vacation = vacation;
    }

    public List<Excursion> getExcursions() {
        return excursions;
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
    }
}
