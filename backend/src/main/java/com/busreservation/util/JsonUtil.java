package com.busreservation.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * Utility class for JSON serialization and deserialization using Jackson.
 * Provides convenient methods for converting between Java objects and JSON strings.
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // Configure ObjectMapper
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Convert a Java object to JSON string.
     * 
     * @param object the object to serialize
     * @return JSON string representation
     * @throws IOException if serialization fails
     */
    public static String toJson(Object object) throws IOException {
        if (object == null) {
            return "{}";
        }
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Convert JSON string to Java object.
     * 
     * @param json JSON string
     * @param clazz target class type
     * @param <T> type parameter
     * @return deserialized object
     * @throws IOException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        return objectMapper.readValue(json, clazz);
    }
    
    /**
     * Convert JSON string to Java object, returning null if parsing fails.
     * 
     * @param json JSON string
     * @param clazz target class type
     * @param <T> type parameter
     * @return deserialized object or null if parsing fails
     */
    public static <T> T fromJsonSafe(String json, Class<T> clazz) {
        try {
            return fromJson(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Check if a string is valid JSON.
     * 
     * @param json string to validate
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Pretty print a JSON string.
     * 
     * @param json JSON string
     * @return formatted JSON string
     * @throws IOException if formatting fails
     */
    public static String prettyPrint(String json) throws IOException {
        Object jsonObject = objectMapper.readValue(json, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
    
    /**
     * Get the ObjectMapper instance for advanced usage.
     * 
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
