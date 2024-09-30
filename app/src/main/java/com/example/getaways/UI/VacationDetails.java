package com.example.getaways.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Vacation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

// ***EVALUATION, TASK B3-a/b:
// a: Display a detailed view of the vacation, including all vacation details. This view can also be used to add and update the vacation information.
// b: Enter, edit, and delete vacation information
// Activity for displaying, saving/updating, sharing, alerting a vacation
public class VacationDetails extends BaseActivity {
    Repository repository;
    ExcursionAdapter excursionAdapter;
    private EditText etvVacationTitle;
    private EditText etvHotelName;
    private Button btnPickStartDate;
    private Button btnPickEndDate;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vacation Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
        }

        // Configure overflow icon
        Drawable overflowIcon = customToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(ContextCompat.getColor(this, android.R.color.black));
        }

        // Initialize repository
        repository = new Repository(getApplication());

        // Get extras from previous intent
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("START_DATE");
        String endDate = getIntent().getStringExtra("END_DATE");

        // InitializeSwipeRefreshLayout, Set up the swipe-to-refresh listener
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_vacation_details);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshVacationList(vacationID));

        // ***EVALUATION, TASK B2: Include the following details for each vacation: title, hotel or other place where you will be staying, start date, end date
        // Create views for users to enter vacation details

        // ***EVALUATION, TASK B3-b: Enter, edit, and delete vacation information

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

        Button btnAlert = findViewById(R.id.btn_alert_vacation);
        btnAlert.setOnClickListener(view -> handleAlertButtonClick());

        Button btnShare = findViewById(R.id.btn_share_vacation);
        btnShare.setOnClickListener(view -> handleShareButtonClick());

        ExtendedFloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_details);
        addFloatingActionButton.setOnClickListener(view -> handleFabButtonClick());

        // If vacation clicked and exists, prefill details in views
        if (vacationID != 0) {
            etvVacationTitle.setText(vacationTitle);
            etvHotelName.setText(hotelName);
            btnPickStartDate.setText(startDate);
            btnPickEndDate.setText(endDate);
        }

        // ***EVALUATION, TASK B3-g:  Display a list of excursions associated with each vacation.
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

        setStatusBarColorBasedOnTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
        menu.findItem(R.id.ic_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_home) {
            startActivity(new Intent(this, VacationList.class));
        } else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.ic_logout) {
            showLogoutDialog();
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

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            button.setText(selectedDate);
        }, year, month, day).show();
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this).setTitle("Log out").setMessage("Are you sure you want to log out?").setPositiveButton("Yes", (dialog, which) -> {
            // Log out the user and navigate to the login screen
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();  // End the current activity
        }).setNegativeButton("No", (dialog, which) -> {
            // Dismiss the dialog if the user clicks "No"
            dialog.dismiss();
        }).create().show();
    }

    // ***EVALUATION, TASK B3-a:  Display a detailed view of the vacation, including all vacation details. This view can also be used to add and update the vacation information.
    // Method to handle saving, or updating if vacation already exists
    private void handleSaveButtonClick() {
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        if (isValidVacation(vacationTitle, hotelName, startDate, endDate)) {
            Vacation vacation = new Vacation(vacationTitle, hotelName, startDate, endDate);

            if (vacationID == 0) {
                // Add new vacation
                repository.insert(vacation);
                repository.getLastInsertedVacation().observe(this, newVacationID -> {
                    // Update the vacationID in the intent
                    getIntent().putExtra("ID", newVacationID);
                    Toast.makeText(this, "Successfully added new vacation!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                // Directly update the vacation without LiveData observation
                vacation.setId(vacationID);
                repository.update(vacation);

                // Display success message after updating
                Toast.makeText(this, "Successfully updated vacation!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (vacationTitle.isEmpty() || hotelName.isEmpty()) {
            Toast.makeText(this, "Vacation title and hotel name fields cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if (!isDateOnOrAfterCurrentDate(startDate)) {
            Toast.makeText(this, "Start date must be today, or in the future.", Toast.LENGTH_SHORT).show();
        } else if (!isDateBefore(startDate, endDate)) {
            Toast.makeText(this, "Start date must be before end date.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteButtonClick(int vacationID) {
        repository.vacationExists(vacationID).observe(this, exists -> {
            if (exists) {
                repository.getVacationByID(vacationID).observe(this, vacation -> {
                    if (vacation != null) {
                        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> {
                            // ***EVALUATION, TASK B1-b: Implement validation so that a vacation cannot be deleted if excursions are associated with it.
                            // Prevent deletion of vacations if the associated excursions list is not empty
                            if (excursions == null || excursions.isEmpty()) {
                                new AlertDialog.Builder(this).setTitle("Delete Vacation").setMessage("Are you sure you want to delete this vacation?").setPositiveButton("Yes", (dialog, which) -> {
                                    repository.delete(vacation);
                                    Toast.makeText(this, "Successfully deleted vacation.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
                            } else {
                                Toast.makeText(this, "Cannot delete vacation with associated excursions.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            } else {
                if (!isFinishing()) {
                    Toast.makeText(this, "Failed to delete vacation. Vacation does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ***EVALUATION, TASK B3-e:  Include an alert that the user can set which will trigger on the start and end date, displaying the vacation title and whether it is starting or ending.
    // Handle alert button click to create intent with vacation details
    private void handleAlertButtonClick() {
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
            Intent alertIntent = new Intent(VacationDetails.this, VacationAlert.class);
            alertIntent.putExtra("VACATION_ID", vacationID);
            alertIntent.putExtra("VACATION_TITLE", vacationTitle);
            alertIntent.putExtra("HOTEL_NAME", hotelName);
            alertIntent.putExtra("VACATION_START_DATE", startDate);
            alertIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(alertIntent);
        }
    }

    // ***EVALUATION, TASK B3-f:  Include sharing features so the user can share all the vacation details via a sharing feature (either e-mail, clipboard or SMS) that automatically populates with the vacation details.
    // Method to handle share button click, create intent with all current vacation details
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

    // ***EVALUATION, TASK B3-h:  Add, update, and delete as many excursions as needed.
    // Handle floating action button click to create intent with vacation details to go to Excursion Details activity
    private void handleFabButtonClick() {
        int vacationID = getIntent().getIntExtra("ID", 0);
        String vacationTitle = etvVacationTitle.getText().toString();
        String hotelName = etvHotelName.getText().toString();
        String startDate = btnPickStartDate.getText().toString();
        String endDate = btnPickEndDate.getText().toString();

        if (isValidVacation(vacationTitle, hotelName, startDate, endDate) && vacationID != 0) {
            Intent excursionDetailsIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            excursionDetailsIntent.putExtra("VACATION_ID", vacationID);
            excursionDetailsIntent.putExtra("VACATION_START_DATE", startDate);
            excursionDetailsIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(excursionDetailsIntent);
        } else if (vacationID == 0) {
            new AlertDialog.Builder(this).setTitle("Save Vacation").setMessage("The vacation must first be saved.").setPositiveButton("Okay", (dialog, which) -> dialog.dismiss()).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // ***EVALUATION, TASK B3-c:  Include validation that the input dates are formatted correctly.
    // Create method to validate all vacation input is valid
    private boolean isValidVacation(String vacationTitle, String hotelName, String startDate, String endDate) {
        return !startDate.equals("Pick a date") && !endDate.equals("Pick a date") && !vacationTitle.isEmpty() && !hotelName.isEmpty() && isDateOnOrAfterCurrentDate(startDate) && isDateBefore(startDate, endDate);
    }

    // ***EVALUATION, TASK B3-c:  Include validation that the input dates are formatted correctly.
    // Normalizes String dates for input validation
    private String normalizeDate(String date) {
        String[] parts = date.split("/");
        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }

    // ***EVALUATION, TASK B3-d:  Include validation that the vacation end date is after the start date.
    // Checks if a given date is before another given date
    private boolean isDateBefore(String date1, String date2) {
        // Check for empty input to avoid exception
        if (date1.equals("Pick a date") || date2.equals("Pick a date")) {
            return false;
        }
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

    // ***EVALUATION, TASK B3-d:  Include validation that the input dates are formatted correctly.
    // Checks if a given date is on or after the current date
    private boolean isDateOnOrAfterCurrentDate(String date) {
        // Check for empty input to avoid exception
        if (date.equals("Pick a date")) {
            return false;
        }
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

    private void refreshVacationList(int vacationID) {
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> {
            excursionAdapter.setExcursions(excursions);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}