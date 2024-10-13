package com.example.getaways;

import com.example.getaways.UI.Validators.DateValidator;

import org.junit.Before;
import org.junit.Test;

public class DateNormalizerTest {
    private DateValidator dateValidator;

    @Before
    public void setUp() {
        dateValidator = new DateValidator();
    }

    // Test to check if a single-digit month and day are properly normalized
    @Test
    public void testNormalizeDate_singleDigitMonthAndDay() {
        String date = "1/2/2023";  // Date with single-digit month and day
        String expected = "01/02/2023";  // Expected normalized date
        assertEquals(expected, dateValidator.normalizeDate(date));
    }

    // Test to check if a single-digit month and a two-digit day are normalized
    @Test
    public void testNormalizeDate_singleDigitMonth_twoDigitDay() {
        String date = "1/12/2023";  // Date with single-digit month and two-digit day
        String expected = "01/12/2023";  // Expected normalized date
        assertEquals(expected, dateValidator.normalizeDate(date));
    }

    // Test to check if a date with two-digit month and day is unchanged
    @Test
    public void testNormalizeDate_twoDigitMonthAndDay() {
        String date = "11/22/2023";  // Date with two-digit month and day
        String expected = "11/22/2023";  // Expected normalized date (no change)
        assertEquals(expected, dateValidator.normalizeDate(date));
    }

    // Test to check behavior with an invalid date format
    @Test
    public void testNormalizeDate_invalidDateFormat() {
        // Testing invalid date input, expecting ArrayIndexOutOfBoundsException or similar
        try {
            String invalidDate = "invalid-date";  // Invalid date format
            dateValidator.normalizeDate(invalidDate);
            assertEquals(invalidDate, dateValidator.normalizeDate(invalidDate)); // Expecting no change
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            // Expected behavior due to incorrect date format
        }
    }

    // Test to check behavior when the date is an empty string
    @Test
    public void testNormalizeDate_emptyDate() {
        // Testing empty date input, expecting ArrayIndexOutOfBoundsException or similar
        try {
            String emptyString = ""; // Empty date input
            dateValidator.normalizeDate(emptyString);
            assertEquals(emptyString, dateValidator.normalizeDate(emptyString)); // Expecting no change
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            // Expected behavior
        }
    }
}
