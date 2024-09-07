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

import com.example.getaways.R;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Excursion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExcursionDetails extends AppCompatActivity {
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

        // Initialize repository
        repository = new Repository(getApplication());

        // Get extras from previous activity
        int excursionID = getIntent().getIntExtra("ID", 0);
        String excursionDate = getIntent().getStringExtra("EXCURSION_DATE");
        String excursionTitle = getIntent().getStringExtra("EXCURSION_TITLE");
        int vacationID = getIntent().getIntExtra("VACATION_ID", 0);
        //String vacationStartDate = getIntent().getStringExtra("VACATION_START_DATE");
        //String vacationEndDate = getIntent().getStringExtra("VACATION_END_DATE");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
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

    // TODO: add more checks for invalid input with feedback
    private void handleSaveButtonClick(int excursionID, int vacationID) {
        String excursionTitle = etvExcursionTitle.getText().toString();
        String excursionDate = btnPickExcursionDate.getText().toString();
        String vacationStartDate = getIntent().getStringExtra("VACATION_START_DATE");
        String vacationEndDate = getIntent().getStringExtra("VACATION_END_DATE");

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
        } else {
            Toast.makeText(this, "Invalid input, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

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
                    Toast.makeText(this, "Failed to delete vacation. Vacation does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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

    private String normalizeDate(String date) {
        String[] parts = date.split("/");

        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }

    private boolean isDateBetween(String targetDate, String startDate, String endDate) {
        // Normalize the dates to MM/dd/yyyy format
        targetDate = normalizeDate(targetDate);
        startDate = normalizeDate(startDate);
        endDate = normalizeDate(endDate);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date parsedTargetDate = sdf.parse(targetDate);
            Date parsedStartDate = sdf.parse(startDate);
            Date parsedEndDate = sdf.parse(endDate);

            if (parsedTargetDate != null && parsedStartDate != null && parsedEndDate != null) {
                return !parsedTargetDate.before(parsedStartDate) && !parsedTargetDate.after(parsedEndDate);
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidExcursion(String excursionTitle, String excursionDate, String vacationStartDate, String vacationEndDate) {
        return !excursionTitle.isEmpty() && !excursionDate.equals("Pick a date") && isDateBetween(excursionDate, vacationStartDate, vacationEndDate);
    }
}