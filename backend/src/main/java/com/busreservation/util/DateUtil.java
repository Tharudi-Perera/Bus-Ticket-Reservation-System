package com.busreservation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date operations and validation.
 * Handles date parsing, formatting, and validation for single-day trip reservations.
 */
public class DateUtil {
    
    // Standard date format: YYYY-MM-DD (e.g., "2025-09-09")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    /**
     * Parse a date string to LocalDate.
     * 
     * @param dateString date string in format "YYYY-MM-DD"
     * @return LocalDate object
     * @throws IllegalArgumentException if date format is invalid
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid date format. Expected format: YYYY-MM-DD (e.g., 2025-09-09). Got: " + dateString
            );
        }
    }
    
    /**
     * Format a LocalDate to string.
     * 
     * @param date LocalDate object
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Validate that a trip date is not in the past.
     * 
     * @param date date to validate
     * @throws IllegalArgumentException if date is in the past
     */
    public static void validateNotPastDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new IllegalArgumentException(
                "Trip date cannot be in the past. Provided date: " + formatDate(date) + 
                ", Today: " + formatDate(today)
            );
        }
    }
    
    /**
     * Validate trip date string (format and not in past).
     * 
     * @param dateString date string to validate
     * @return validated LocalDate
     * @throws IllegalArgumentException if validation fails
     */
    public static LocalDate validateTripDate(String dateString) {
        LocalDate date = parseDate(dateString);
        validateNotPastDate(date);
        return date;
    }
    
    /**
     * Check if a date string is valid format.
     * 
     * @param dateString date string to check
     * @return true if valid format, false otherwise
     */
    public static boolean isValidDateFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateString.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Check if a date is today.
     * 
     * @param date date to check
     * @return true if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }
    
    /**
     * Check if a date is in the future (after today).
     * 
     * @param date date to check
     * @return true if date is after today
     */
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * Get today's date as LocalDate.
     * 
     * @return today's date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Get today's date as formatted string.
     * 
     * @return today's date string in YYYY-MM-DD format
     */
    public static String todayString() {
        return formatDate(LocalDate.now());
    }
}