package com.busreservation.api;

import com.busreservation.util.HttpUtil;
import com.busreservation.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Test/Health Check Servlet
 * Provides a simple endpoint to verify the backend is running
 * Endpoint: GET /api/test
 */
@WebServlet("/api/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Create test response
        Map<String, Object> testResponse = new HashMap<>();
        testResponse.put("status", "success");
        testResponse.put("message", "Bus Reservation System API is running");
        testResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        testResponse.put("version", "1.0.0");
        
        // Add system info
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("serverInfo", getServletContext().getServerInfo());
        testResponse.put("system", systemInfo);
        
        // Add available endpoints
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("test", "GET /api/test - Health check endpoint");
        endpoints.put("availability", "POST /api/availability - Check seat availability");
        endpoints.put("reservation", "POST /api/reservation - Create a reservation");
        testResponse.put("endpoints", endpoints);
        
        // Send response
        HttpUtil.writeSuccessResponse(response, testResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtil.setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "POST method is not supported. Use GET /api/test");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "PUT method is not supported. Use GET /api/test");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "DELETE method is not supported. Use GET /api/test");
    }
}
