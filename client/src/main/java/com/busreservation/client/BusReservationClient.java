package com.busreservation.client;

import com.busreservation.client.dto.*;
import com.busreservation.client.util.HttpClient;

import java.util.Scanner;

/**
 * Command-line client for the Bus Reservation System.
 * Provides a menu-driven interface to check seat availability and make reservations.
 */
public class BusReservationClient {
    
    private static final String DEFAULT_API_URL = "http://localhost:8080/bus-reservation";
    private static final String AVAILABILITY_ENDPOINT = "/api/availability";
    private static final String RESERVATION_ENDPOINT = "/api/reservation";
    
    private final HttpClient httpClient;
    private final Scanner scanner;
    
    public BusReservationClient(String apiUrl) {
        this.httpClient = new HttpClient(apiUrl);
        // Enable debug mode if -Ddebug=true is passed
        if (Boolean.parseBoolean(System.getProperty("debug", "false"))) {
            this.httpClient.setDebugMode(true);
        }
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        // Use custom API URL if provided, otherwise use default
        String apiUrl = args.length > 0 ? args[0] : DEFAULT_API_URL;
        
        BusReservationClient client = new BusReservationClient(apiUrl);
        client.run();
    }
    
    /**
     * Main application loop with menu interface.
     */
    public void run() {
        printWelcomeBanner();
        
        boolean running = true;
        while (running) {
            printMenu();
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    checkAvailability();
                    break;
                case "2":
                    makeReservation();
                    break;
                case "3":
                    running = false;
                    System.out.println("\n✓ Thank you for using Bus Reservation System. Goodbye!");
                    break;
                default:
                    System.out.println("\n✗ Invalid choice. Please enter 1, 2, or 3.\n");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Prints welcome banner.
     */
    private void printWelcomeBanner() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║        BUS RESERVATION SYSTEM - CLIENT APP             ║");
        System.out.println("║                                                        ║");
        System.out.println("║  Route: A → B → C → D                                  ║");
        System.out.println("║  Total Seats: 40 (Rows 1-10, Seats A-D)                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Prints main menu.
     */
    private void printMenu() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│           MAIN MENU                    │");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│  1. Check Seat Availability & Pricing  │");
        System.out.println("│  2. Make a Reservation                 │");
        System.out.println("│  3. Exit                               │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("\nEnter your choice (1-3): ");
    }
    
    /**
     * Handles seat availability checking.
     */
    private void checkAvailability() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            CHECK SEAT AVAILABILITY & PRICING");
        System.out.println("=".repeat(60));
        
        try {
            // Get input
            int passengers = getPassengerCount();
            String origin = getLocation("Origin");
            String destination = getLocation("Destination");
            
            // Validate origin != destination
            if (origin.equals(destination)) {
                System.out.println("\n✗ Error: Origin and destination cannot be the same.\n");
                return;
            }
            
            // Create request
            AvailabilityRequestDTO request = new AvailabilityRequestDTO(passengers, origin, destination);
            
            System.out.println("\n⏳ Checking availability...");
            
            // Call API
            AvailabilityResponseDTO response = httpClient.post(
                AVAILABILITY_ENDPOINT,
                request,
                AvailabilityResponseDTO.class
            );
            
            // Display results
            displayAvailabilityResults(response);
            
        } catch (HttpClient.ApiException e) {
            handleApiError(e);
        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Handles reservation booking.
     */
    private void makeReservation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              MAKE A RESERVATION");
        System.out.println("=".repeat(60));
        
        try {
            // Get input
            int passengers = getPassengerCount();
            String origin = getLocation("Origin");
            String destination = getLocation("Destination");
            
            // Validate origin != destination
            if (origin.equals(destination)) {
                System.out.println("\n✗ Error: Origin and destination cannot be the same.\n");
                return;
            }
            
            // First check availability to get the price
            AvailabilityRequestDTO availRequest = new AvailabilityRequestDTO(passengers, origin, destination);
            
            System.out.println("\n⏳ Checking seat availability and pricing...");
            
            AvailabilityResponseDTO availResponse = httpClient.post(
                AVAILABILITY_ENDPOINT,
                availRequest,
                AvailabilityResponseDTO.class
            );
            
            // Display availability
            System.out.println("\n✓ Availability Check Results:");
            System.out.println("  • Available Seats: " + availResponse.getAvailableSeats().size());
            System.out.println("  • Price per Seat: Rs. " + String.format("%.2f", availResponse.getPricePerSeat()));
            System.out.println("  • Total Price: Rs. " + String.format("%.2f", availResponse.getTotalPrice()));
            
            if (availResponse.getAvailableSeats().size() < passengers) {
                System.out.println("\n✗ Not enough seats available. Only " + availResponse.getAvailableSeats().size() + " seats remaining.\n");
                return;
            }
            
            // Confirm booking
            System.out.print("\nDo you want to proceed with the reservation? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("\n✗ Reservation cancelled.\n");
                return;
            }
            
            // Create reservation request
            ReservationRequestDTO request = new ReservationRequestDTO(
                passengers,
                origin,
                destination,
                availResponse.getTotalPrice()
            );
            
            System.out.println("\n⏳ Processing reservation...");
            
            // Call API
            ReservationResponseDTO response = httpClient.post(
                RESERVATION_ENDPOINT,
                request,
                ReservationResponseDTO.class
            );
            
            // Display results
            displayReservationResults(response);
            
        } catch (HttpClient.ApiException e) {
            handleApiError(e);
        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Gets passenger count from user input.
     */
    private int getPassengerCount() {
        while (true) {
            try {
                System.out.print("Number of passengers (1-40): ");
                int passengers = Integer.parseInt(scanner.nextLine().trim());
                if (passengers >= 1 && passengers <= 40) {
                    return passengers;
                }
                System.out.println("✗ Please enter a number between 1 and 40.");
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets location from user input.
     */
    private String getLocation(String prompt) {
        while (true) {
            System.out.print(prompt + " location (A, B, C, or D): ");
            String location = scanner.nextLine().trim().toUpperCase();
            if (location.matches("[A-D]")) {
                return location;
            }
            System.out.println("✗ Invalid location. Please enter A, B, C, or D.");
        }
    }
    
    /**
     * Displays availability check results.
     */
    private void displayAvailabilityResults(AvailabilityResponseDTO response) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            AVAILABILITY CHECK RESULTS");
        System.out.println("=".repeat(60));
        System.out.println("Route: " + response.getOrigin() + " → " + response.getDestination());
        System.out.println("Passengers: " + response.getPassengers());
        System.out.println();
        System.out.println("Price per Seat: Rs. " + String.format("%.2f", response.getPricePerSeat()));
        System.out.println("Total Price: Rs. " + String.format("%.2f", response.getTotalPrice()));
        System.out.println();
        System.out.println("Available Seats: " + response.getAvailableSeats().size());
        
        if (response.getAvailableSeats().isEmpty()) {
            System.out.println("\n⚠ No seats available for this route.");
        } else if (response.getAvailableSeats().size() < response.getPassengers()) {
            System.out.println("\n⚠ Not enough seats available. Only " + response.getAvailableSeats().size() + " seats remaining.");
        } else {
            System.out.println("\n✓ Sufficient seats available!");
            System.out.println("\nSample Available Seats:");
            int displayCount = Math.min(10, response.getAvailableSeats().size());
            for (int i = 0; i < displayCount; i++) {
                System.out.print(response.getAvailableSeats().get(i));
                if (i < displayCount - 1) {
                    System.out.print(", ");
                }
            }
            if (response.getAvailableSeats().size() > 10) {
                System.out.print(" ... and " + (response.getAvailableSeats().size() - 10) + " more");
            }
            System.out.println();
        }
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Displays reservation confirmation results.
     */
    private void displayReservationResults(ReservationResponseDTO response) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          ✓ RESERVATION SUCCESSFUL!");
        System.out.println("=".repeat(60));
        System.out.println("Reservation ID: " + response.getReservationId());
        System.out.println("Timestamp: " + response.getReservationTime());
        System.out.println();
        System.out.println("Journey Details:");
        System.out.println("  • Route: " + response.getOrigin() + " → " + response.getDestination());
        System.out.println("  • Passengers: " + response.getPassengers());
        System.out.println("  • Total Price: Rs. " + String.format("%.2f", response.getTotalPrice()));
        System.out.println();
        System.out.println("Assigned Seats: " + String.join(", ", response.getSeatNumbers()));
        System.out.println("=".repeat(60));
        System.out.println("Please save your Reservation ID for future reference.");
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Handles API errors with user-friendly messages.
     */
    private void handleApiError(HttpClient.ApiException e) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✗ API ERROR");
        System.out.println("=".repeat(60));
        
        switch (e.getStatusCode()) {
            case 400:
                System.out.println("Invalid request. Please check your input:");
                System.out.println(e.getResponseBody());
                break;
            case 409:
                System.out.println("Conflict: Seats not available.");
                System.out.println(e.getResponseBody());
                System.out.println("\n⚠ TIP: Another user may have booked these seats.");
                System.out.println("   Please check availability again before retrying.");
                break;
            case 500:
                System.out.println("Server error occurred. Please try again later.");
                break;
            case 503:
                System.out.println("Service unavailable. Please ensure the backend is running.");
                System.out.println("Expected URL: " + DEFAULT_API_URL);
                break;
            default:
                System.out.println("Error " + e.getStatusCode() + ": " + e.getResponseBody());
        }
        
        System.out.println("=".repeat(60) + "\n");
    }
}
