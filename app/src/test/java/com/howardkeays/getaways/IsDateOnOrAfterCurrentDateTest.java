package com.example.getaways;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.getaways.UI.Validators.DateValidator;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IsDateOnOrAfterCurrentDateTest {

    private DateValidator dateValidator;

    @Before
    public void setUp() {
        dateValidator = new DateValidator();
    }

    // Test case where the provided date is in the future, one day after the current date
    @Test
    public void testIsDateOnOrAfterCurrentDate_validFutureDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Add one day to the current date
        Date futureDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String futureDateString = sdf.format(futureDate); // Format the future date

        // Expecting true since the provided date is after the current date
        assertTrue(dateValidator.isDateOnOrAfterCurrentDate(futureDateString));
    }

    // Test case where the provided date is exactly the current date
    @Test
    public void testIsDateOnOrAfterCurrentDate_validCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime(); // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateString = sdf.format(currentDate); // Format the current date

        // Expecting true since the provided date is exactly the current date
        assertTrue(dateValidator.isDateOnOrAfterCurrentDate(currentDateString));
    }

    // Test case where the provided date is in the past, one day before the current date
    @Test
    public void testIsDateOnOrAfterCurrentDate_validPastDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Subtract one day to get a past date
        Date pastDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String pastDateString = sdf.format(pastDate); // Format the past date

        // Expecting false since the provided date is before the current date
        assertFalse(dateValidator.isDateOnOrAfterCurrentDate(pastDateString));
    }

    // Test case where the input date is "Pick a date", which is considered empty
    @Test
    public void testIsDateOnOrAfterCurrentDate_emptyDate() {
        // Expecting false since "Pick a date" is an invalid input
        assertFalse(dateValidator.isDateOnOrAfterCurrentDate("Pick a date"));
    }

    // Test case where the input date format is invalid
    @Test
    public void testIsDateOnOrAfterCurrentDate_invalidDateFormat() {
        // Expecting false since the date format is incorrect (not MM/dd/yyyy)
        assertFalse(dateValidator.isDateOnOrAfterCurrentDate("invalid-date"));
    }

    // Test case where the input date is null
    @Test
    public void testIsDateOnOrAfterCurrentDate_nullDate() {
        // Expecting false since null is not a valid date input
        assertFalse(dateValidator.isDateOnOrAfterCurrentDate(null));
    }

    // Test case where the input date is non-existent, like February 30
    @Test
    public void testIsDateOnOrAfterCurrentDate_nonExistentDate() {
        // Expecting false since February 30 is not a valid date
        assertFalse(dateValidator.isDateOnOrAfterCurrentDate("02/30/2023"));
    }
}
