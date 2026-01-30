package com.busreservation.util;

import com.busreservation.dto.ErrorResponseDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility class for handling HTTP requests and responses in servlets.
 * Provides convenient methods for reading request bodies and writing JSON responses.
 */
public class HttpUtil {
    
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_UTF8 = "UTF-8";
    
    /**
     * Read the request body as a string.
     * 
     * @param request HTTP servlet request
     * @return request body as string
     * @throws IOException if reading fails
     */
    public static String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        
        return body.toString();
    }
    
    /**
     * Write a JSON response.
     * 
     * @param response HTTP servlet response
     * @param statusCode HTTP status code
     * @param object object to serialize to JSON
     * @throws IOException if writing fails
     */
    public static void writeJsonResponse(HttpServletResponse response, int statusCode, Object object) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.setStatus(statusCode);
        
        String json = JsonUtil.toJson(object);
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
    
    /**
     * Write a success JSON response with 200 OK status.
     * 
     * @param response HTTP servlet response
     * @param object object to serialize to JSON
     * @throws IOException if writing fails
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object object) throws IOException {
        writeJsonResponse(response, HttpServletResponse.SC_OK, object);
    }
    
    /**
     * Write an error JSON response.
     * 
     * @param response HTTP servlet response
     * @param statusCode HTTP status code
     * @param error error type
     * @param message error message
     * @throws IOException if writing fails
     */
    public static void writeErrorResponse(HttpServletResponse response, int statusCode, String error, String message) throws IOException {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(error, message, statusCode);
        writeJsonResponse(response, statusCode, errorResponse);
    }
    
    /**
     * Write a bad request error response (400).
     * 
     * @param response HTTP servlet response
     * @param message error message
     * @throws IOException if writing fails
     */
    public static void writeBadRequestError(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad Request", message);
    }
    
    /**
     * Write an internal server error response (500).
     * 
     * @param response HTTP servlet response
     * @param message error message
     * @throws IOException if writing fails
     */
    public static void writeInternalServerError(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
    
    /**
     * Write a not found error response (404).
     * 
     * @param response HTTP servlet response
     * @param message error message
     * @throws IOException if writing fails
     */
    public static void writeNotFoundError(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Not Found", message);
    }
    
    /**
     * Write a method not allowed error response (405).
     * 
     * @param response HTTP servlet response
     * @param message error message
     * @throws IOException if writing fails
     */
    public static void writeMethodNotAllowedError(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed", message);
    }
    
    /**
     * Set CORS headers for the response.
     * 
     * @param response HTTP servlet response
     */
    public static void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
    /**
     * Get client IP address from request.
     * 
     * @param request HTTP servlet request
     * @return client IP address
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        return ipAddress;
    }
}
