package com.busreservation.client.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client utility for making REST API calls to the bus reservation backend.
 */
public class HttpClient {
    
    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private boolean debugMode;
    
    /**
     * Creates a new HTTP client with the specified base URL.
     *
     * @param baseUrl The base URL of the backend API (e.g., "http://localhost:8080/bus-reservation")
     */
    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.debugMode = Boolean.parseBoolean(System.getProperty("debug", "false"));
    }
    
    /**
     * Enables or disables debug mode to show network calls.
     *
     * @param enabled true to show network calls, false to hide
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
    
    /**
     * Sends a POST request with JSON body.
     *
     * @param endpoint The API endpoint (e.g., "/api/availability")
     * @param requestBody The request body object to be serialized to JSON
     * @param responseType The expected response type class
     * @param <T> The type of the response
     * @return The deserialized response object
     * @throws IOException If an I/O error occurs
     * @throws ApiException If the API returns an error response
     */
    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws IOException, ApiException {
        String fullUrl = baseUrl + endpoint;
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            // Configure connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // Send request body
            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            
            if (debugMode) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("🌐 HTTP REQUEST");
                System.out.println("=".repeat(70));
                System.out.println("Method: POST");
                System.out.println("URL: " + fullUrl);
                System.out.println("\nRequest Body:");
                System.out.println(jsonRequest);
                System.out.println("=".repeat(70));
            }
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Get response code
            int responseCode = connection.getResponseCode();
            
            // Read response
            String responseBody = readResponse(connection, responseCode >= 400);
            
            if (debugMode) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("🌐 HTTP RESPONSE");
                System.out.println("=".repeat(70));
                System.out.println("Status Code: " + responseCode);
                System.out.println("\nResponse Body:");
                System.out.println(responseBody);
                System.out.println("=".repeat(70) + "\n");
            }
            
            // Handle error responses
            if (responseCode >= 400) {
                throw new ApiException(responseCode, responseBody);
            }
            
            // Parse successful response
            return objectMapper.readValue(responseBody, responseType);
            
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Sends a GET request.
     *
     * @param endpoint The API endpoint
     * @param responseType The expected response type class
     * @param <T> The type of the response
     * @return The deserialized response object
     * @throws IOException If an I/O error occurs
     * @throws ApiException If the API returns an error response
     */
    public <T> T get(String endpoint, Class<T> responseType) throws IOException, ApiException {
        String fullUrl = baseUrl + endpoint;
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            // Configure connection
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            if (debugMode) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("🌐 HTTP REQUEST");
                System.out.println("=".repeat(70));
                System.out.println("Method: GET");
                System.out.println("URL: " + fullUrl);
                System.out.println("=".repeat(70));
            }
            
            // Get response code
            int responseCode = connection.getResponseCode();
            
            // Read response
            String responseBody = readResponse(connection, responseCode >= 400);
            
            if (debugMode) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("🌐 HTTP RESPONSE");
                System.out.println("=".repeat(70));
                System.out.println("Status Code: " + responseCode);
                System.out.println("\nResponse Body:");
                System.out.println(responseBody);
                System.out.println("=".repeat(70) + "\n");
            }
            
            // Handle error responses
            if (responseCode >= 400) {
                throw new ApiException(responseCode, responseBody);
            }
            
            // Parse successful response
            return objectMapper.readValue(responseBody, responseType);
            
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Reads the response from the connection.
     *
     * @param connection The HTTP connection
     * @param isError Whether to read from error stream
     * @return The response body as string
     * @throws IOException If an I/O error occurs
     */
    private String readResponse(HttpURLConnection connection, boolean isError) throws IOException {
        BufferedReader reader;
        if (isError) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * Exception thrown when the API returns an error response.
     */
    public static class ApiException extends Exception {
        private final int statusCode;
        private final String responseBody;
        
        public ApiException(int statusCode, String responseBody) {
            super("API returned error " + statusCode + ": " + responseBody);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getResponseBody() {
            return responseBody;
        }
    }
}
