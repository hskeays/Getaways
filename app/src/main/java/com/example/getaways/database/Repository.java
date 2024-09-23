package com.example.getaways.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.getaways.dao.ExcursionDAO;
import com.example.getaways.dao.VacationDAO;
import com.example.getaways.entities.Excursion;
import com.example.getaways.entities.Vacation;
import com.example.getaways.entities.VacationWithExcursions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private final VacationDAO vacationDAO;
    private final ExcursionDAO excursionDAO;

    public Repository(Application application) {
        DatabaseBuilder db = DatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
    }

    // GET **********************
    public LiveData<List<Vacation>> getAllVacations() {
        return vacationDAO.getAllVacations();
    }

    public LiveData<List<VacationWithExcursions>> getAllVacationsWithExcursions() {
        return vacationDAO.getAllVacationsWithExcursions();
    }

    public LiveData<Vacation> getVacationByID(int vacationID) {
        return vacationDAO.getVacationByID(vacationID);
    }

    public LiveData<Excursion> getExcursionByID(int excursionID) {
        return excursionDAO.getExcursionByID(excursionID);
    }

    public LiveData<List<Excursion>> getAllExcursions() {
        return excursionDAO.getAllExcursions();
    }

    public LiveData<List<Excursion>> getAssociatedExcursions(int vacationID) {
        return excursionDAO.getAssociatedExcursions(vacationID);
    }

    public LiveData<Boolean> vacationExists(int vacationID) {
        return vacationDAO.vacationExists(vacationID);
    }

    public LiveData<Boolean> excursionExists(int excursionID) {
        return excursionDAO.excursionExists(excursionID);
    }

    // INSERT *********************
    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.insert(vacation));
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.insert(excursion));
    }

    // UPDATE *************************
    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.update(vacation));
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.update(excursion));
    }

    // DELETE *********************
    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.delete(vacation));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.delete(excursion));
    }

    public LiveData<Integer> getLastInsertedVacation() {
        return vacationDAO.getLastInsertedVacationID();
    }
}
