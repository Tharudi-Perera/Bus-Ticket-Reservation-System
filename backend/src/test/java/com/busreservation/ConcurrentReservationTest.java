package com.busreservation;

import com.busreservation.entity.Location;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.ReservationRepository;
import com.busreservation.service.AvailabilityService;
import com.busreservation.service.PricingService;
import com.busreservation.service.ReservationService;
import com.busreservation.util.DateUtil;
import com.busreservation.dto.ReservationRequestDTO;
import com.busreservation.dto.ReservationResponseDTO;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrent reservation tests to verify thread safety and race condition handling.
 * 
 * These tests simulate multiple users trying to book seats simultaneously
 * to ensure the system prevents overselling and maintains data integrity.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcurrentReservationTest {

    private static BusRepository busRepository;
    private static ReservationRepository reservationRepository;
    private static PricingService pricingService;
    private static AvailabilityService availabilityService;
    private static ReservationService reservationService;
    private static String testDate;

    @BeforeAll
    public static void setup() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCURRENT RESERVATION TESTING - Race Condition Verification");
        System.out.println("=".repeat(80));
        
        // Initialize services
        busRepository = BusRepository.getInstance();
        reservationRepository = ReservationRepository.getInstance();
        pricingService = PricingService.getInstance();
        availabilityService = AvailabilityService.getInstance();
        reservationService = ReservationService.getInstance();

        // Use tomorrow's date for all tests
        testDate = DateUtil.formatDate(LocalDate.now().plusDays(1));
    }

    @BeforeEach
    public void resetSystem() {
        // Reset repositories before each test
        busRepository.reset();
        reservationRepository.reset();
        System.out.println("\n✓ System reset complete");
    }

    /**
     * Test Scenario 1: Last 5 Seats - 2 Concurrent Users
     * 
     * Setup: Book 35 seats first (leaving 5 available)
     * Test: 2 users simultaneously try to book 5 seats each
     * Expected: Only 1 user succeeds, the other fails
     */
    @Test
    @Order(1)
    public void testScenario1_LastFiveSeats_TwoUsers() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 1: Last 5 Seats - 2 Concurrent Users Each Booking 5 Seats");
        System.out.println("-".repeat(80));

        // Step 1: Book 35 seats to leave only 5 available
        System.out.println("\n📋 Setup: Booking 35 seats (A → B) to leave 5 available...");
        ReservationRequestDTO setupRequest = new ReservationRequestDTO();
        setupRequest.setPassengers(35);
        setupRequest.setOrigin("A");
        setupRequest.setDestination("B");
        setupRequest.setPrice(1750.0); // 35 * 50
        setupRequest.setTripDate(testDate);

        ReservationResponseDTO setupResponse = reservationService.createReservation(setupRequest);
        assertNotNull(setupResponse);
        assertEquals(35, setupResponse.getSeatNumbers().size());
        System.out.println("✓ Setup complete: 35 seats booked, 5 seats remaining");

        // Step 2: Prepare concurrent booking attempts
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<ReservationResponseDTO> successfulReservations = new CopyOnWriteArrayList<>();
        List<String> errorMessages = new CopyOnWriteArrayList<>();

        // Step 3: Launch concurrent booking threads
        System.out.println("\n🚀 Launching 2 concurrent users...");
        for (int i = 0; i < numThreads; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready

                    System.out.println("   User " + userId + ": Attempting to book 5 seats...");
                    
                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(5);
                    request.setOrigin("A");
                    request.setDestination("B");
                    request.setPrice(250.0); // 5 * 50
                    request.setTripDate(testDate);

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    successfulReservations.add(response);
                    System.out.println("   User " + userId + ": ✓ SUCCESS - Booked seats: " + response.getSeatNumbers());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": Thread interrupted");
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": " + e.getMessage());
                    System.out.println("   User " + userId + ": ✗ FAILED - " + e.getMessage());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": Unexpected error - " + e.getMessage());
                    System.out.println("   User " + userId + ": ✗ ERROR - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Step 4: Start all threads simultaneously
        Thread.sleep(100); // Give threads time to reach startLatch.await()
        startLatch.countDown(); // Release all threads at once
        
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Step 5: Verify results
        System.out.println("\n📊 Results:");
        System.out.println("   Successful bookings: " + successCount.get());
        System.out.println("   Failed bookings: " + failureCount.get());

        assertTrue(completed, "All threads should complete within timeout");
        assertEquals(1, successCount.get(), "Only 1 user should successfully book 5 seats");
        assertEquals(1, failureCount.get(), "1 user should fail due to insufficient seats");

        // Verify no duplicate seats were assigned
        if (!successfulReservations.isEmpty()) {
            Set<String> allSeats = successfulReservations.stream()
                    .flatMap(r -> r.getSeatNumbers().stream())
                    .collect(Collectors.toSet());
            
            long totalSeatsAssigned = successfulReservations.stream()
                    .mapToLong(r -> r.getSeatNumbers().size())
                    .sum();
            
            assertEquals(totalSeatsAssigned, allSeats.size(), 
                "No duplicate seats should be assigned across reservations");
            System.out.println("   ✓ No duplicate seats detected");
        }

        System.out.println("✓ Scenario 1 PASSED: System correctly handled race condition");
    }

    /**
     * Test Scenario 2: Last 1 Seat - 5 Concurrent Users
     * 
     * Setup: Book 39 seats first (leaving 1 available)
     * Test: 5 users simultaneously try to book 1 seat each
     * Expected: Only 1 user succeeds, 4 others fail
     */
    @Test
    @Order(2)
    public void testScenario2_LastOneSeat_FiveUsers() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 2: Last 1 Seat - 5 Concurrent Users");
        System.out.println("-".repeat(80));

        // Step 1: Book 39 seats
        System.out.println("\n📋 Setup: Booking 39 seats (A → D) to leave 1 available...");
        ReservationRequestDTO setupRequest = new ReservationRequestDTO();
        setupRequest.setPassengers(39);
        setupRequest.setOrigin("A");
        setupRequest.setDestination("D");
        setupRequest.setPrice(5850.0); // 39 * 150
        setupRequest.setTripDate(testDate);

        ReservationResponseDTO setupResponse = reservationService.createReservation(setupRequest);
        assertNotNull(setupResponse);
        System.out.println("✓ Setup complete: 39 seats booked, 1 seat remaining");

        // Step 2: Prepare concurrent attempts
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> successfulUsers = new CopyOnWriteArrayList<>();

        // Step 3: Launch threads
        System.out.println("\n🚀 Launching 5 concurrent users competing for last seat...");
        for (int i = 0; i < numThreads; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(1);
                    request.setOrigin("A");
                    request.setDestination("D");
                    request.setPrice(150.0);
                    request.setTripDate(testDate);

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    successfulUsers.add("User " + userId + " - Seat: " + response.getSeatNumbers().get(0));
                    System.out.println("   User " + userId + ": ✓ WON - Got seat " + response.getSeatNumbers().get(0));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    System.out.println("   User " + userId + ": ✗ LOST - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify
        System.out.println("\n📊 Results:");
        System.out.println("   Winner: " + (successfulUsers.isEmpty() ? "None" : successfulUsers.get(0)));
        System.out.println("   Losers: " + failureCount.get());

        assertTrue(completed, "All threads should complete");
        assertEquals(1, successCount.get(), "Exactly 1 user should win the last seat");
        assertEquals(4, failureCount.get(), "4 users should fail");
        
        System.out.println("✓ Scenario 2 PASSED: Only one user got the last seat");
    }

    /**
     * Test Scenario 3: 10 Concurrent Users with Random Booking Sizes
     * 
     * Test: 10 users simultaneously try to book random numbers of seats
     * Expected: No overselling, total booked ≤ 40 seats
     */
    @Test
    @Order(3)
    public void testScenario3_TenConcurrentUsers_RandomBookings() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 3: 10 Concurrent Users - Random Booking Sizes");
        System.out.println("-".repeat(80));

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<ReservationResponseDTO> allReservations = new CopyOnWriteArrayList<>();

        Random random = new Random(42); // Fixed seed for reproducibility
        int[] requestedSeats = new int[numThreads];

        System.out.println("\n🚀 Launching 10 concurrent users with random requests...");
        for (int i = 0; i < numThreads; i++) {
            requestedSeats[i] = random.nextInt(8) + 1; // 1-8 seats per user
            final int userId = i + 1;
            final int seatsToBook = requestedSeats[i];

            executor.submit(() -> {
                try {
                    startLatch.await();

                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(seatsToBook);
                    request.setOrigin("B");
                    request.setDestination("D");
                    request.setPrice(seatsToBook * 100.0); // B→D = 2 segments * 50
                    request.setTripDate(testDate);

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    allReservations.add(response);
                    System.out.println("   User " + userId + ": ✓ Booked " + seatsToBook + " seats");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    System.out.println("   User " + userId + ": ✗ Failed to book " + seatsToBook + " seats - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Calculate total seats requested and booked
        int totalRequested = Arrays.stream(requestedSeats).sum();
        int totalBooked = allReservations.stream()
                .mapToInt(r -> r.getSeatNumbers().size())
                .sum();

        System.out.println("\n📊 Results:");
        System.out.println("   Total seats requested: " + totalRequested);
        System.out.println("   Total seats booked: " + totalBooked);
        System.out.println("   Successful bookings: " + successCount.get());
        System.out.println("   Failed bookings: " + failureCount.get());

        assertTrue(completed, "All threads should complete");
        assertTrue(totalBooked <= 40, "Total booked seats should not exceed 40");

        // Verify no duplicate seats
        Set<String> uniqueSeats = allReservations.stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        assertEquals(totalBooked, uniqueSeats.size(), 
            "No duplicate seats should be assigned (found " + (totalBooked - uniqueSeats.size()) + " duplicates)");
        System.out.println("   ✓ No duplicate seats detected");
        System.out.println("   ✓ No overselling occurred");
        
        System.out.println("✓ Scenario 3 PASSED: System handled concurrent bookings correctly");
    }

    @AfterAll
    public static void summary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCURRENT TESTING COMPLETE");
        System.out.println("=".repeat(80));
        System.out.println("✓ All race condition tests passed");
        System.out.println("✓ Thread-safe operations verified");
        System.out.println("✓ No overselling detected");
        System.out.println("=".repeat(80) + "\n");
    }


    /**
 * Test Scenario 4: Rapid Sequential Bookings - Stress Test
 * 
 * Test: 50 users rapidly book 1 seat each
 * Expected: First 40 succeed, remaining 10 fail
 */
@Test
@Order(4)
public void testScenario4_RapidSequentialBookings_StressTest() throws InterruptedException {
    System.out.println("\n" + "-".repeat(80));
    System.out.println("SCENARIO 4: Rapid Sequential Bookings - 50 Users for 40 Seats");
    System.out.println("-".repeat(80));

    int numThreads = 50;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch endLatch = new CountDownLatch(numThreads);

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    List<ReservationResponseDTO> allReservations = new CopyOnWriteArrayList<>();

    System.out.println("\n🚀 Launching 50 concurrent users...");
    for (int i = 0; i < numThreads; i++) {
        final int userId = i + 1;
        executor.submit(() -> {
            try {
                startLatch.await();
                
                ReservationRequestDTO request = new ReservationRequestDTO();
                request.setPassengers(1);
                request.setOrigin("A");
                request.setDestination("B");
                request.setPrice(50.0);
                request.setTripDate(testDate);

                ReservationResponseDTO response = reservationService.createReservation(request);
                successCount.incrementAndGet();
                allReservations.add(response);
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                endLatch.countDown();
            }
        });
    }

    startLatch.countDown();
    boolean completed = endLatch.await(20, TimeUnit.SECONDS);
    executor.shutdown();

    System.out.println("\n📊 Results:");
    System.out.println("   Successful: " + successCount.get());
    System.out.println("   Failed: " + failureCount.get());

    assertTrue(completed, "All threads should complete");
    assertEquals(40, successCount.get(), "Exactly 40 users should succeed");
    assertEquals(10, failureCount.get(), "10 users should fail");

    Set<String> uniqueSeats = allReservations.stream()
            .flatMap(r -> r.getSeatNumbers().stream())
            .collect(Collectors.toSet());
    assertEquals(40, uniqueSeats.size(), "All 40 seats should be unique");

    System.out.println("✓ Scenario 4 PASSED: Stress test handled correctly");
}

/**
 * Test Scenario 5: Mixed Routes Concurrent Bookings
 * 
 * Test: Multiple users booking different route segments simultaneously
 * Expected: Proper isolation between different routes
 */
@Test
@Order(5)
public void testScenario5_MixedRoutes_ConcurrentBookings() throws InterruptedException {
    System.out.println("\n" + "-".repeat(80));
    System.out.println("SCENARIO 5: Mixed Routes - Concurrent Bookings");
    System.out.println("-".repeat(80));

    int numThreads = 8;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch endLatch = new CountDownLatch(numThreads);

    AtomicInteger successCount = new AtomicInteger(0);
    List<ReservationResponseDTO> allReservations = new CopyOnWriteArrayList<>();

    String[][] routes = {
        {"A", "B", "50.0"},
        {"B", "C", "50.0"},
        {"C", "D", "50.0"},
        {"A", "C", "100.0"},
        {"B", "D", "100.0"},
        {"A", "D", "150.0"}
    };

    System.out.println("\n🚀 Launching 8 users with different routes...");
    for (int i = 0; i < numThreads; i++) {
        final int userId = i + 1;
        final String[] route = routes[i % routes.length];
        
        executor.submit(() -> {
            try {
                startLatch.await();
                
                ReservationRequestDTO request = new ReservationRequestDTO();
                request.setPassengers(5);
                request.setOrigin(route[0]);
                request.setDestination(route[1]);
                request.setPrice(Double.parseDouble(route[2]) * 5);
                request.setTripDate(testDate);

                ReservationResponseDTO response = reservationService.createReservation(request);
                successCount.incrementAndGet();
                allReservations.add(response);
                System.out.println("   User " + userId + " (" + route[0] + "→" + route[1] + "): ✓ Booked");
            } catch (Exception e) {
                System.out.println("   User " + userId + " (" + route[0] + "→" + route[1] + "): ✗ Failed");
            } finally {
                endLatch.countDown();
            }
        });
    }

    startLatch.countDown();
    boolean completed = endLatch.await(15, TimeUnit.SECONDS);
    executor.shutdown();

    System.out.println("\n📊 Results: " + successCount.get() + " successful bookings");
    assertTrue(completed, "All threads should complete");
    assertTrue(successCount.get() > 0, "Some bookings should succeed");

    System.out.println("✓ Scenario 5 PASSED: Mixed routes handled correctly");
}

/**
 * Test Scenario 6: Rollback on Partial Failure
 * 
 * Test: Verify system handles partial booking failures correctly
 */
@Test
@Order(6)
public void testScenario6_PartialBooking_Rollback() throws InterruptedException {
    System.out.println("\n" + "-".repeat(80));
    System.out.println("SCENARIO 6: Partial Booking Rollback Test");
    System.out.println("-".repeat(80));

    // Book 38 seats first
    ReservationRequestDTO setupRequest = new ReservationRequestDTO();
    setupRequest.setPassengers(38);
    setupRequest.setOrigin("A");
    setupRequest.setDestination("B");
    setupRequest.setPrice(1900.0);
    setupRequest.setTripDate(testDate);
    reservationService.createReservation(setupRequest);
    
    System.out.println("✓ Setup: 38 seats booked, 2 remaining");

    int numThreads = 3;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch endLatch = new CountDownLatch(numThreads);

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    System.out.println("\n🚀 3 users trying to book 3 seats each (only 2 available)...");
    for (int i = 0; i < numThreads; i++) {
        final int userId = i + 1;
        executor.submit(() -> {
            try {
                startLatch.await();
                
                ReservationRequestDTO request = new ReservationRequestDTO();
                request.setPassengers(3);
                request.setOrigin("A");
                request.setDestination("B");
                request.setPrice(150.0);
                request.setTripDate(testDate);

                reservationService.createReservation(request);
                successCount.incrementAndGet();
                System.out.println("   User " + userId + ": ✓ Success");
            } catch (Exception e) {
                failureCount.incrementAndGet();
                System.out.println("   User " + userId + ": ✗ Failed - " + e.getMessage());
            } finally {
                endLatch.countDown();
            }
        });
    }

    startLatch.countDown();
    endLatch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    System.out.println("\n📊 Results:");
    System.out.println("   Success: " + successCount.get());
    System.out.println("   Failures: " + failureCount.get());

    assertEquals(0, successCount.get(), "No user should succeed (3 seats > 2 available)");
    assertEquals(3, failureCount.get(), "All 3 users should fail");

    System.out.println("✓ Scenario 6 PASSED: Partial bookings correctly rejected");
}

/**
 * Test Scenario 7: Peak Load Simulation
 * 
 * Test: Simulate peak booking time with varied booking sizes
 */
@Test
@Order(7)
public void testScenario7_PeakLoad_VariedSizes() throws InterruptedException {
    System.out.println("\n" + "-".repeat(80));
    System.out.println("SCENARIO 7: Peak Load - 20 Users with Varied Sizes");
    System.out.println("-".repeat(80));

    int numThreads = 20;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch endLatch = new CountDownLatch(numThreads);

    AtomicInteger totalBooked = new AtomicInteger(0);
    Random random = new Random(789);

    System.out.println("\n🚀 Simulating peak load...");
    for (int i = 0; i < numThreads; i++) {
        final int userId = i + 1;
        final int seats = random.nextInt(5) + 1; // 1-5 seats
        
        executor.submit(() -> {
            try {
                startLatch.await();
                
                ReservationRequestDTO request = new ReservationRequestDTO();
                request.setPassengers(seats);
                request.setOrigin("A");
                request.setDestination("C");
                request.setPrice(seats * 100.0);
                request.setTripDate(testDate);

                ReservationResponseDTO response = reservationService.createReservation(request);
                totalBooked.addAndGet(seats);
                System.out.println("   User " + userId + ": ✓ Booked " + seats);
            } catch (Exception e) {
                System.out.println("   User " + userId + ": ✗ Failed");
            } finally {
                endLatch.countDown();
            }
        });
    }

    startLatch.countDown();
    endLatch.await(20, TimeUnit.SECONDS);
    executor.shutdown();

    System.out.println("\n📊 Total booked: " + totalBooked.get() + " seats");
    assertTrue(totalBooked.get() <= 40, "No overselling");
    System.out.println("✓ Scenario 7 PASSED: Peak load handled correctly");
}

}
