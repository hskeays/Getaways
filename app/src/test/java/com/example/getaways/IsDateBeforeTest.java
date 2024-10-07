package com.example.getaways;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.getaways.UI.Validators.DateValidator;

import org.junit.Before;
import org.junit.Test;

public class IsDateBeforeTest {
    private DateValidator dateValidator;

    @Before
    public void setUp() {
        dateValidator = new DateValidator();
    }

    // Test to check if the first date is before the second date (valid case)
    @Test
    public void testIsDateBefore_validDates() {
        // The first date is before the second
        assertTrue(dateValidator.isDateBefore("12/01/2023", "12/31/2023"));
    }

    // Test to check if the method handles the case where both dates are the same
    @Test
    public void testIsDateBefore_sameDate() {
        // Both dates are the same, so it should return false
        assertFalse(dateValidator.isDateBefore("12/01/2023", "12/01/2023"));
    }

    // Test to check if the method correctly identifies when the first date is after the second
    @Test
    public void testIsDateBefore_firstDateAfter() {
        // The first date is after the second, so it should return false
        assertFalse(dateValidator.isDateBefore("12/31/2023", "12/01/2023"));
    }

    // Test to check if the method handles the case where the first date is "Pick a date"
    @Test
    public void testIsDateBefore_emptyDate1() {
        // The first date is "Pick a date", so it should return false
        assertFalse(dateValidator.isDateBefore("Pick a date", "12/01/2023"));
    }

    // Test to check if the method handles the case where the second date is "Pick a date"
    @Test
    public void testIsDateBefore_emptyDate2() {
        // The second date is "Pick a date", so it should return false
        assertFalse(dateValidator.isDateBefore("12/01/2023", "Pick a date"));
    }

    // Test to check if the method handles invalid date formats
    @Test
    public void testIsDateBefore_invalidDateFormat() {
        // Invalid date format (dash instead of slashes), should return false
        assertFalse(dateValidator.isDateBefore("12-01-2023", "12/31/2023"));
    }

    // Test to check if the method handles null values for one or both dates
    @Test
    public void testIsDateBefore_nullDate() {
        // Either or both dates are null, so it should return false
        assertFalse(dateValidator.isDateBefore(null, "12/01/2023"));
        assertFalse(dateValidator.isDateBefore("12/01/2023", null));
        assertFalse(dateValidator.isDateBefore(null, null));
    }

    // Test to check if the method handles non-existent dates like "02/30/2023"
    @Test
    public void testIsDateBefore_nonExistentDate() {
        // One of the dates does not exist (Feb 30th), should return false
        assertFalse(dateValidator.isDateBefore("02/30/2023", "12/01/2023"));
    }
}
