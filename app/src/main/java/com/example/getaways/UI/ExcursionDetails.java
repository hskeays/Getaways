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

import com.example.getaways.R;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Excursion;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

// ***EVALUATION, TASK B3-h:  Add, update, and delete as many excursions as needed.
// Class that handles adding, updating, deleting excursions
public class ExcursionDetails extends BaseActivity {
    private Repository repository;
    private EditText etvExcursionTitle;
    private Button btnPickExcursionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excursion Details");
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

        // Get extras from previous activity
        int excursionID = getIntent().getIntExtra("ID", 0);
        String excursionDate = getIntent().getStringExtra("EXCURSION_DATE");
        String excursionTitle = getIntent().getStringExtra("EXCURSION_TITLE");
        int vacationID = getIntent().getIntExtra("VACATION_ID", 0);
        String vacationStartDate = getIntent().getStringExtra("VACATION_START_DATE");
        String vacationEndDate = getIntent().getStringExtra("VACATION_END_DATE");

        // ***EVALUATION, TASK B4:  Include the following details for each excursion: The excursion title, The excursion date
        // ***EVALUATION, TASK B5-a:  Display a detailed view of the excursion, including the title, and date
        // Initialize views and set on click listeners
        etvExcursionTitle = findViewById(R.id.etv_enter_excursion_title);

        btnPickExcursionDate = findViewById(R.id.btn_excursion_date_picker);
        btnPickExcursionDate.setOnClickListener(view -> showDatePickerDialog(btnPickExcursionDate));

        Button btnSave = findViewById(R.id.btn_save_excursion);
        btnSave.setOnClickListener(view -> handleSaveButtonClick(excursionID, vacationID));

        Button btnDelete = findViewById(R.id.btn_delete_excursion);
        btnDelete.setOnClickListener(view -> handleDeleteButtonClick(excursionID));

        Button btnAlert = findViewById(R.id.btn_alert_excursion);
        btnAlert.setOnClickListener(view -> handleAlertButtonClick());

        // If vacation clicked and exists, prefill details in views
        if (excursionID != 0) {
            etvExcursionTitle.setText(excursionTitle);
            btnPickExcursionDate.setText(excursionDate);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            button.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
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

    // ***EVALUATION, TASK B5-b:  Enter, edit, and delete excursion information.
    private void handleSaveButtonClick(int excursionID, int vacationID) {
        String excursionTitle = etvExcursionTitle.getText().toString();
        String excursionDate = btnPickExcursionDate.getText().toString();

        repository.getVacationByID(vacationID).observe(this, vacation -> {
            String vacationStartDate = vacation.getStartDate();
            String vacationEndDate = vacation.getEndDate();

            if (isValidExcursion(excursionTitle, excursionDate, vacationStartDate, vacationEndDate)) {
                Excursion excursion;
                // If excursion does not exist insert, else update
                if (excursionID == 0) {
                    excursion = new Excursion(excursionDate, excursionTitle, vacationID);
                    repository.insert(excursion);
                    Toast.makeText(this, "Excursion successfully added.", Toast.LENGTH_SHORT).show();
                } else {
                    excursion = new Excursion(excursionDate, excursionTitle, vacationID);
                    excursion.setId(excursionID);
                    repository.update(excursion);
                    Toast.makeText(this, "Excursion successfully updated.", Toast.LENGTH_SHORT).show();
                }
                finish();
            } else if (excursionTitle.isEmpty()) {
                Toast.makeText(this, "Excursion title field cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Excursion date must be between " + vacationStartDate + " and " + vacationEndDate + ".", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ***EVALUATION, TASK B5-b:  Enter, edit, and delete excursion information.
    private void handleDeleteButtonClick(int excursionID) {
        // Check if the excursion exists
        repository.excursionExists(excursionID).observe(this, exists -> {
            if (exists != null && exists) {
                // Get the excursion to delete
                repository.getExcursionByID(excursionID).observe(this, excursion -> {
                    if (excursion != null) {
                        // Show confirmation dialog before deletion
                        new AlertDialog.Builder(this).setTitle("Delete Excursion").setMessage("Are you sure you want to delete this excursion?").setPositiveButton("Yes", (dialog, which) -> {
                            repository.delete(excursion);
                            Toast.makeText(this, "Successfully deleted excursion.", Toast.LENGTH_SHORT).show();
                            finish();
                        }).setNegativeButton("No", null).show();
                    }
                });
            } else {
                // Only show this if the user hasn't just deleted the vacation
                if (!isFinishing()) {
                    Toast.makeText(this, "Failed to delete excursion. Excursion does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ***EVALUATION, TASK B5-d:  Include an alert that the user can set that will trigger on the excursion date, stating the excursion title.
    // Method to handle creating intent with excursion details to navigate to ExcursionAlert activity
    private void handleAlertButtonClick() {
        int excursionID = getIntent().getIntExtra("ID", 0);
        String excursionTitle = etvExcursionTitle.getText().toString();
        String excursionDate = btnPickExcursionDate.getText().toString();

        if (excursionID == 0) {
            new AlertDialog.Builder(this).setTitle("Save Excursion").setMessage("The Excursion must first be saved.").setPositiveButton("Okay", (dialog, which) -> dialog.dismiss()).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            Intent alertIntent = new Intent(ExcursionDetails.this, ExcursionAlert.class);
            alertIntent.putExtra("EXCURSION_ID", excursionID);
            alertIntent.putExtra("EXCURSION_TITLE", excursionTitle);
            alertIntent.putExtra("EXCURSION_DATE", excursionDate);
            startActivity(alertIntent);
        }
    }

    private boolean isValidExcursion(String excursionTitle, String excursionDate, String vacationStartDate, String vacationEndDate) {
        return !excursionTitle.isEmpty() && !excursionDate.equals("Pick a date") && isDateBetween(excursionDate, vacationStartDate, vacationEndDate);
    }
}