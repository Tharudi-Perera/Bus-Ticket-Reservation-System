package com.busreservation.util;

import com.busreservation.entity.Location;

/**
 * Utility class for input validation.
 * Provides common validation methods for the application.
 */
public class ValidationUtil {
    
    /**
     * Validate that a string is not null or empty.
     * 
     * @param value string to validate
     * @param fieldName name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Validate that a number is positive.
     * 
     * @param value number to validate
     * @param fieldName name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }
    }
    
    /**
     * Validate that a number is positive.
     * 
     * @param value number to validate
     * @param fieldName name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }
    }
    
    /**
     * Validate that a number is within a range.
     * 
     * @param value number to validate
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @param fieldName name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                fieldName + " must be between " + min + " and " + max + " (inclusive)"
            );
        }
    }
    
    /**
     * Validate that a location string is valid.
     * 
     * @param location location string to validate (A, B, C, or D)
     * @param fieldName name of the field for error messages
     * @return validated Location enum
     * @throws IllegalArgumentException if validation fails
     */
    public static Location validateLocation(String location, String fieldName) {
        validateNotEmpty(location, fieldName);
        
        try {
            return Location.valueOf(location.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                fieldName + " must be one of: A, B, C, D. Got: " + location
            );
        }
    }
    
    /**
     * Validate that two locations are different.
     * 
     * @param origin origin location
     * @param destination destination location
     * @throws IllegalArgumentException if locations are the same
     */
    public static void validateDifferentLocations(Location origin, Location destination) {
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
    }
    
    /**
     * Validate passenger count.
     * Must be between 1 and 40 (total bus capacity).
     * 
     * @param passengers number of passengers
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePassengerCount(int passengers) {
        validateRange(passengers, 1, 40, "Number of passengers");
    }
    
    /**
     * Validate that an object is not null.
     * 
     * @param object object to validate
     * @param fieldName name of the field for error messages
     * @throws IllegalArgumentException if object is null
     */
    public static void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Validate price value.
     * 
     * @param price price to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePrice(Double price) {
        validateNotNull(price, "Price");
        validatePositive(price, "Price");
    }
    
}
