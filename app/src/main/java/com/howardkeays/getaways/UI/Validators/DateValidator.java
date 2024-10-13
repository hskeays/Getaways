package com.howardkeays.getaways.UI.Validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {

    // Normalizes String dates for input validation
    public String normalizeDate(String date) {
        String[] parts = date.split("/");

        if (parts.length != 3) {
            return date;
        }

        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }

    // Checks if a given date is before another given date
    public boolean isDateBefore(String date1, String date2) {
        // Check for empty input/null values to avoid exceptions
        if (date1 == null || date2 == null || date1.equals("Pick a date") || date2.equals("Pick a date")) {
            return false;
        }

        // Normalize the dates to MM/dd/yyyy format
        date1 = normalizeDate(date1);
        date2 = normalizeDate(date2);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);  // Ensures strict date parsing

        try {
            Date parsedDate1 = sdf.parse(date1);
            Date parsedDate2 = sdf.parse(date2);

            // If parsing fails or returns null, return false
            if (parsedDate1 == null || parsedDate2 == null) {
                return false;
            }

            // Compare the two parsed dates
            return parsedDate1.before(parsedDate2);
        } catch (ParseException e) {
            // In case of a parsing error, return false
            return false;
        }
    }

    // Checks if a given date is on or after the current date
    public boolean isDateOnOrAfterCurrentDate(String date) {
        // Check for empty input to avoid exception
        if (date == null || date.equals("Pick a date")) {
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

    // Method to validate excursion date is between vacation start and end date
    public boolean isDateBetween(String targetDate, String startDate, String endDate) {
        // Check for empty/null input to avoid exception
        if (targetDate == null || startDate == null || endDate == null || startDate.equals("Pick a date") || endDate.equals("Pick a date") || targetDate.equals("Pick a date")) {
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
}
