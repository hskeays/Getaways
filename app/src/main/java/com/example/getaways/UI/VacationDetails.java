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
import java.util.Date;

public class VacationDetails extends AppCompatActivity {
    Repository repository;
    ExcursionAdapter excursionAdapter;
    private EditText etvVacationTitle;
    private EditText etvHotelName;
    private Button btnPickStartDate;
    private Button btnPickEndDate;
    private Button btnAlert;

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

        // Initialize views, set on click listeners for date picker buttons
        etvVacationTitle = findViewById(R.id.etv_vacation_title);
        etvHotelName = findViewById(R.id.etv_hotel_name);

        btnPickStartDate = findViewById(R.id.btn_start_date_picker);
        btnPickStartDate.setOnClickListener(view -> showDatePickerDialog(btnPickStartDate));

        btnPickEndDate = findViewById(R.id.btn_end_date_picker);
        btnPickEndDate.setOnClickListener(view -> showDatePickerDialog(btnPickEndDate));

        Button btnSave = findViewById(R.id.btn_save_vacation);
        btnSave.setOnClickListener(view -> handleSaveButtonClick(vacationID));

        Button btnDelete = findViewById(R.id.btn_delete_vacation);
        btnDelete.setOnClickListener(view -> handleDeleteButtonClick(vacationID));

        // Initialize FAB, set on click listener to start ExcursionDetails activity
        //TODO: Add dialog asking user to first save the vacation before adding excursions
        FloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_details);
        addFloatingActionButton.setOnClickListener(view -> {
            Intent excursionDetailsIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            excursionDetailsIntent.putExtra("VACATION_ID", vacationID);
            excursionDetailsIntent.putExtra("VACATION_START_DATE", startDate);
            excursionDetailsIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(excursionDetailsIntent);
        });

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
        repository.getAssociatedExcursions(vacationID)
                  .observe(this, excursions -> excursionAdapter.setExcursions(excursions));
    }


    @Override
    protected void onResume() {
        super.onResume();
        repository.getAllExcursions().observe(this, excursions -> excursionAdapter.setExcursions(excursions));
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

    private void handleSaveButtonClick(int vacationID) {
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        // Check if vacation title and hotel name are not empty, start date is before end date, and start date is today or in the future
        if (isValidVacation(vacationTitle, hotelName, startDate, endDate)) {
            Vacation vacation;
            if (vacationID == 0) {
                vacation = new Vacation(vacationTitle, hotelName, startDate, endDate);
                repository.insert(vacation);
                Toast.makeText(this, "Successfully added new vacation", Toast.LENGTH_SHORT).show();
            } else {
                vacation = new Vacation(vacationTitle, hotelName, startDate, endDate);
                vacation.setId(vacationID);
                repository.update(vacation);
                Toast.makeText(this, "Successfully updated vacation", Toast.LENGTH_SHORT).show();
            }
            //TODO Ask user if they want to add excursions, if no finish(), else click FAB programmatically ???
            finish();
        }
        // TODO: Add more checks to provide more descriptive feedback to correct input
        else {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteButtonClick(int vacationID) {
        // Check if the vacation exists
        repository.vacationExists(vacationID).observe(this, exists -> {
            if (exists) {
                // Get the vacation to delete
                repository.getVacationByID(vacationID).observe(this, vacation -> {
                    if (vacation != null) {
                        // Show confirmation dialog before deletion
                        new AlertDialog.Builder(this).setTitle("Delete Vacation")
                                                     .setMessage("Are you sure you want to delete this vacation?")
                                                     .setPositiveButton("Yes", (dialog, which) -> {
                                                         repository.delete(vacation);
                                                         Toast.makeText(this, "Successfully deleted vacation.", Toast.LENGTH_SHORT)
                                                              .show();
                                                         finish();
                                                     }).setNegativeButton("No", null).show();
                    }
                });
            } else {
                Toast.makeText(this, "Failed to delete vacation. Vacation does not exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Below are methods to normalize and validate date inputs
    private String normalizeDate(String date) {
        String[] parts = date.split("/");

        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }

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

    private boolean isValidVacation(String vacationTitle, String hotelName, String startDate, String endDate) {
        return !vacationTitle.isEmpty() && !hotelName.isEmpty() && isDateOnOrAfterCurrentDate(startDate) && isDateBefore(startDate, endDate);
    }
}