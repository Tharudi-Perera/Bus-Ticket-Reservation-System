package com.busreservation.api;

import com.busreservation.dto.AvailabilityRequestDTO;
import com.busreservation.dto.AvailabilityResponseDTO;
import com.busreservation.service.AvailabilityService;
import com.busreservation.util.HttpUtil;
import com.busreservation.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling seat availability requests.
 * Endpoint: /api/availability
 * Method: POST
 * 
 * Request Body:
 * {
 *   "passengers": 2,
 *   "origin": "A",
 *   "destination": "C"
 * }
 * 
 * Response:
 * {
 *   "availableSeats": ["1A", "1B"],
 *   "totalPrice": 200.0,
 *   "pricePerSeat": 100.0,
 *   "passengers": 2,
 *   "origin": "A",
 *   "destination": "C"
 * }
 */
@WebServlet(name = "AvailabilityServlet", urlPatterns = {"/api/availability"})
public class AvailabilityServlet extends HttpServlet {
    
    private AvailabilityService availabilityService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.availabilityService = AvailabilityService.getInstance();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set CORS headers
        HttpUtil.setCorsHeaders(response);
        
        try {
            // Read request body
            String requestBody = HttpUtil.readRequestBody(request);
            
            // Parse JSON to DTO
            AvailabilityRequestDTO availabilityRequest = JsonUtil.fromJson(requestBody, AvailabilityRequestDTO.class);
            
            // Check availability
            AvailabilityResponseDTO availabilityResponse = availabilityService.checkAvailability(availabilityRequest);
            
            // Return success response
            HttpUtil.writeSuccessResponse(response, availabilityResponse);
            
        } catch (IllegalArgumentException e) {
            // Bad request - validation errors
            HttpUtil.writeBadRequestError(response, e.getMessage());
            
        } catch (Exception e) {
            // Internal server error
            HttpUtil.writeInternalServerError(response, "An error occurred while checking availability: " + e.getMessage());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle CORS preflight requests
        HttpUtil.setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "GET method not supported. Use POST.");
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "PUT method not supported. Use POST.");
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpUtil.writeMethodNotAllowedError(response, "DELETE method not supported. Use POST.");
    }
}
