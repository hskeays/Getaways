package com.example.getaways.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.getaways.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColorBasedOnTheme();
    }

    public void setStatusBarColorBasedOnTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Light mode
                setStatusBarColor(R.color.white); // Set status bar color for light mode
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark mode
                setStatusBarColor(R.color.dark_grey); // Set status bar color for dark mode
                break;
        }
    }

    private void setStatusBarColor(int colorResId) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorResId));
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
    public boolean isDateBefore(String date1, String date2) {
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
    public boolean isDateOnOrAfterCurrentDate(String date) {
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

    // ***EVALUATION, TASK B5-f:  Include validation that the excursion date is during the associated vacation.
    // Method to validate excursion date is between vacation start and end date
    public boolean isDateBetween(String targetDate, String startDate, String endDate) {
        // Check for empty input to avoid exception
        if (startDate.equals("Pick a date") || endDate.equals("Pick a date") || targetDate.equals("Pick a date")) {
            return false;
        }

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

    public void showLogoutDialog() {
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
}
