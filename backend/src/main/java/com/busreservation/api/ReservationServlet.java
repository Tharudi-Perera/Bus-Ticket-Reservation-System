package com.busreservation.api;

import com.busreservation.dto.ReservationRequestDTO;
import com.busreservation.dto.ReservationResponseDTO;
import com.busreservation.service.ReservationService;
import com.busreservation.util.HttpUtil;
import com.busreservation.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling ticket reservation requests.
 * Endpoint: /api/reservation
 * Method: POST
 * 
 * Request Body:
 * {
 *   "tripdate": "2026-02-14",
 *   "passengers": 2,
 *   "origin": "A",
 *   "destination": "C",
 *   "price": 200.0
 * }
 * 
 * Response:
 * {
 *   "reservationId": "550e8400-e29b-41d4-a716-446655440000",
 *   "seatNumbers": ["1A", "1B"],
 *   "origin": "A",
 *   "destination": "C",
 *   "passengers": 2,
 *   "totalPrice": 200.0,
 *   "reservationTime": "2026-01-29 12:45:00"
 * }
 */

@WebServlet(name = "ReservationServlet", urlPatterns = {"/api/reservation"})
public class ReservationServlet extends HttpServlet {
    
    private ReservationService reservationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.reservationService = ReservationService.getInstance();
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
            ReservationRequestDTO reservationRequest = JsonUtil.fromJson(requestBody, ReservationRequestDTO.class);
            
            // Create reservation
            ReservationResponseDTO reservationResponse = reservationService.createReservation(reservationRequest);
            
            // Return success response with 201 Created
            HttpUtil.writeJsonResponse(response, HttpServletResponse.SC_CREATED, reservationResponse);
            
        } catch (IllegalArgumentException e) {
            // Bad request - validation errors
            HttpUtil.writeBadRequestError(response, e.getMessage());
            
        } catch (IllegalStateException e) {
            // Conflict - seats not available
            HttpUtil.writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Conflict", e.getMessage());
            
        } catch (Exception e) {
            // Internal server error
            HttpUtil.writeInternalServerError(response, "An error occurred while creating reservation: " + e.getMessage());
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
