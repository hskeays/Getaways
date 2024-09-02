package com.example.getaways.database;


import android.app.Application;

import com.example.getaways.dao.ExcursionDAO;
import com.example.getaways.dao.VacationDAO;
import com.example.getaways.entities.Excursion;
import com.example.getaways.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private final VacationDAO vacationDAO;
    private final ExcursionDAO excursionDAO;
    private List<Vacation> allVacations;
    private List<Excursion> allExcursions;

    public Repository(Application application) {
        DatabaseBuilder db = DatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
    }

    public List<Vacation> getAllVacations() {
        databaseExecutor.execute(() -> allVacations = vacationDAO.getAllVacations());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allVacations;
    }

    public List<Excursion> getAllExcursions() {
        databaseExecutor.execute(() -> allExcursions = excursionDAO.getAllExcursions());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allExcursions;
    }

    public List<Excursion> getAssociatedExcursions(int id) {
        databaseExecutor.execute(() -> allExcursions = excursionDAO.getAssociatedExcursions(id));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allExcursions;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.insert(vacation));
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.insert(excursion));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.update(vacation));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.update(excursion));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.delete(vacation));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.delete(excursion));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
