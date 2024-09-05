package com.example.getaways.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class VacationDetails extends AppCompatActivity {
    Repository repository;
    ExcursionAdapter excursionAdapter;
    private EditText etvVacationTitle;
    private EditText etvHotelName;
    private Button btnPickStartDate;
    private Button btnPickEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository
        repository = new Repository(getApplication());

        // Get extras from previous intent
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("START_DATE");
        String endDate = getIntent().getStringExtra("END_DATE");
        // Initialize views/buttons, set on click listeners
        etvVacationTitle = findViewById(R.id.etv_vacation_title);
        etvHotelName = findViewById(R.id.etv_hotel_name);
        btnPickStartDate = findViewById(R.id.btn_start_date_picker);
        btnPickStartDate.setOnClickListener(view -> showDatePickerDialog(btnPickStartDate));
        btnPickEndDate = findViewById(R.id.btn_end_date_picker);
        btnPickEndDate.setOnClickListener(view -> showDatePickerDialog(btnPickEndDate));
        Button btnSave = findViewById(R.id.btn_save_vacation);
        btnSave.setOnClickListener(view -> handleSaveButtonClick());
        Button btnDelete = findViewById(R.id.btn_delete_vacation);
        btnDelete.setOnClickListener(view -> handleDeleteButtonClick(vacationID));

        //TODO: implement alert feature
        Button btnAlert = findViewById(R.id.btn_alert_vacation);
        //btnAlert.setOnClickListener(view -> handleAlertButtonClick());

        //TODO: implement share feature
        Button btnShare = findViewById(R.id.btn_share_vacation);
        btnShare.setOnClickListener(view -> handleShareButtonClick());

        FloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_details);
        addFloatingActionButton.setOnClickListener(view -> handleFabButtonClick());

        // If vacation clicked and exists, prefill details in views
        if (vacationID != 0) {
            etvVacationTitle.setText(vacationTitle);
            etvHotelName.setText(hotelName);
            btnPickStartDate.setText(startDate);
            btnPickEndDate.setText(endDate);
        }

        // Show associated excursions in recycler view
        RecyclerView recyclerView = findViewById(R.id.rv_excursion_list_items);
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe associated excursions
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> excursionAdapter.setExcursions(excursions));
    }

    @Override
    protected void onResume() {
        // Populate associated excursions after saving new excursion
        super.onResume();
        int vacationID = getIntent().getIntExtra("ID", 0);

        if (vacationID != 0) {
            repository.getAssociatedExcursions(vacationID).observe(this, excursions -> excursionAdapter.setExcursions(excursions));
        } else {
            excursionAdapter.setExcursions(Collections.emptyList());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.ic_back) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.vacation_list) {
            startActivity(new Intent(this, VacationList.class));
        } else if (id == R.id.vacation_details) {
            startActivity(new Intent(this, VacationDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog(Button button) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            button.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void handleSaveButtonClick() {
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        saveOrUpdateValidVacation(vacationID, vacationTitle, hotelName, startDate, endDate);
    }

    private void handleDeleteButtonClick(int vacationID) {
        // Check if the vacation exists
        repository.vacationExists(vacationID).observe(this, exists -> {
            if (exists) {
                // Get the vacation to delete
                repository.getVacationByID(vacationID).observe(this, vacation -> {
                    if (vacation != null) {
                        // Show confirmation dialog before deletion
                        new AlertDialog.Builder(this).setTitle("Delete Vacation").setMessage("Are you sure you want to delete this vacation?").setPositiveButton("Yes", (dialog, which) -> {
                            repository.delete(vacation);
                            Toast.makeText(this, "Successfully deleted vacation.", Toast.LENGTH_SHORT).show();
                            finish();
                        }).setNegativeButton("No", null).show();
                    }
                });
            } else {
                // Only show this if the user hasn't just deleted the vacation
                if (!isFinishing()) {
                    Toast.makeText(this, "Failed to delete vacation. Vacation does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleShareButtonClick() {
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        if (!isValidVacation(vacationTitle, hotelName, startDate, endDate)) {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        } else if (vacationID == 0) {
            new AlertDialog.Builder(this).setTitle("Save Vacation").setMessage("The vacation must first be saved.").setPositiveButton("Okay", (dialog, which) -> dialog.dismiss()).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            Intent shareIntent = new Intent(VacationDetails.this, VacationShare.class);
            shareIntent.putExtra("VACATION_ID", vacationID);
            shareIntent.putExtra("VACATION_TITLE", vacationTitle);
            shareIntent.putExtra("HOTEL_NAME", hotelName);
            shareIntent.putExtra("VACATION_START_DATE", startDate);
            shareIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(shareIntent);
        }
    }

    private void handleFabButtonClick() {
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        if (!isValidVacation(vacationTitle, hotelName, startDate, endDate)) {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        } else if (vacationID == 0) {
            new AlertDialog.Builder(this).setTitle("Save Vacation").setMessage("The vacation must first be saved.").setPositiveButton("Okay", (dialog, which) -> dialog.dismiss()).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            Intent excursionDetailsIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            excursionDetailsIntent.putExtra("VACATION_ID", vacationID);
            excursionDetailsIntent.putExtra("VACATION_START_DATE", startDate);
            excursionDetailsIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(excursionDetailsIntent);
        }
    }

    private boolean isValidVacation(String vacationTitle, String hotelName, String startDate, String endDate) {
        return !startDate.equals("Pick a date") && !endDate.equals("Pick a date") && !vacationTitle.isEmpty() && !hotelName.isEmpty() && isDateOnOrAfterCurrentDate(startDate) && isDateBefore(startDate, endDate);
    }

    private void saveOrUpdateValidVacation(int vacationID, String vacationTitle, String hotelName, String startDate, String endDate) {
        if (isValidVacation(vacationTitle, hotelName, startDate, endDate)) {
            Vacation vacation = new Vacation(vacationTitle, hotelName, startDate, endDate);

            if (vacationID == 0) {
                // Add new vacation
                repository.insert(vacation);
                repository.getLastInsertedVacation().observe(this, newVacationID -> {
                    // Update the vacationID in the intent
                    getIntent().putExtra("ID", newVacationID);
                    Toast.makeText(this, "Successfully added new vacation!", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Check if vacation exists asynchronously using LiveData
                repository.vacationExists(vacationID).observe(this, exists -> {
                    if (exists != null && exists) {
                        // If it exists, update the vacation
                        vacation.setId(vacationID);
                        repository.update(vacation);
                        Toast.makeText(this, "Successfully updated vacation!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vacation does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (vacationTitle.isEmpty() || hotelName.isEmpty()) {
            Toast.makeText(this, "Vacation title and hotel name fields cannot be empty.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid dates chosen, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Normalizes String dates for input validation
    private String normalizeDate(String date) {
        String[] parts = date.split("/");
        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }

    // Checks if a given date is before another given date
    private boolean isDateBefore(String date1, String date2) {
        // Normalize the dates to MM/dd/yyyy format
        date1 = normalizeDate(date1);
        date2 = normalizeDate(date2);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date parsedDate1 = sdf.parse(date1);
            Date parsedDate2 = sdf.parse(date2);

            if (parsedDate1 != null) {
                return parsedDate1.before(parsedDate2);
            } else return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if a given date is on or after the current date
    private boolean isDateOnOrAfterCurrentDate(String date) {
        // Normalize the date to MM/dd/yyyy format
        date = normalizeDate(date);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date parsedDate = sdf.parse(date);

            // Get the current date and reset its time to midnight
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);

            Date currentDate = currentCalendar.getTime();

            if (parsedDate != null) {
                return !parsedDate.before(currentDate); // Returns true if date is on or after current date
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}