package com.example.getaways;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.getaways.UI.Validators.DateValidator;

import org.junit.Before;
import org.junit.Test;

public class IsDateBetweenTest {

    private DateValidator dateValidator;

    // Set up method to initialize DateValidator instance before each test
    @Before
    public void setUp() {
        dateValidator = new DateValidator();
    }

    // Test case where the target date is exactly the start date
    @Test
    public void testIsDateBetween_targetDateIsStartDate() {
        String targetDate = "12/01/2023";
        String startDate = "12/01/2023";
        String endDate = "12/31/2023";

        // Expecting true since the target date is the same as the start date
        assertTrue(dateValidator.isDateBetween(targetDate, startDate, endDate));
    }

    // Test case where the target date is exactly the end date
    @Test
    public void testIsDateBetween_targetDateIsEndDate() {
        String targetDate = "12/31/2023";
        String startDate = "12/01/2023";
        String endDate = "12/31/2023";

        // Expecting true since the target date is the same as the end date
        assertTrue(dateValidator.isDateBetween(targetDate, startDate, endDate));
    }

    // Test case where the target date is between start and end dates
    @Test
    public void testIsDateBetween_targetDateIsInBetween() {
        String targetDate = "12/15/2023";
        String startDate = "12/01/2023";
        String endDate = "12/31/2023";

        // Expecting true since the target date is between the start and end dates
        assertTrue(dateValidator.isDateBetween(targetDate, startDate, endDate));
    }

    // Test case where the target date is before the start date
    @Test
    public void testIsDateBetween_targetDateIsBeforeStartDate() {
        String targetDate = "11/30/2023";
        String startDate = "12/01/2023";
        String endDate = "12/31/2023";

        // Expecting false since the target date is before the start date
        assertFalse(dateValidator.isDateBetween(targetDate, startDate, endDate));
    }

    // Test case where the target date is after the end date
    @Test
    public void testIsDateBetween_targetDateIsAfterEndDate() {
        String targetDate = "01/01/2024";
        String startDate = "12/01/2023";
        String endDate = "12/31/2023";

        // Expecting false since the target date is after the end date
        assertFalse(dateValidator.isDateBetween(targetDate, startDate, endDate));
    }

    // Test case where the input date is "Pick a date"
    @Test
    public void testIsDateBetween_emptyDate() {
        assertFalse(dateValidator.isDateBetween("Pick a date", "12/01/2023", "12/31/2023"));
        assertFalse(dateValidator.isDateBetween("12/15/2023", "Pick a date", "12/31/2023"));
        assertFalse(dateValidator.isDateBetween("12/15/2023", "12/01/2023", "Pick a date"));
    }

    // Test case where the input date format is invalid
    @Test
    public void testIsDateBetween_invalidDateFormat() {
        assertFalse(dateValidator.isDateBetween("invalid-date", "12/01/2023", "12/31/2023"));
    }

    // Test case where one of the dates is null
    @Test
    public void testIsDateBetween_nullDate() {
        assertFalse(dateValidator.isDateBetween(null, "12/01/2023", "12/31/2023"));
        assertFalse(dateValidator.isDateBetween("12/15/2023", null, "12/31/2023"));
        assertFalse(dateValidator.isDateBetween("12/15/2023", "12/01/2023", null));
    }

    // Test case where the target date is non-existent (e.g., February 30)
    @Test
    public void testIsDateBetween_nonExistentDate() {
        assertFalse(dateValidator.isDateBetween("02/30/2023", "12/01/2023", "12/31/2023"));
    }
}
