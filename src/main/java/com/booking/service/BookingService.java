package com.booking.service;

import com.booking.exception.InvalidBookingDurationException;
import com.booking.exception.ResourceUnavailableException;
import com.booking.exception.UnauthorizedAccessException;
import com.booking.fileio.BookingFileHandler;
import com.booking.fileio.LogManager;
import com.booking.model.AvailabilityStatus;
import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Resource;
import com.booking.model.ResourceType;
import com.booking.model.Role;
import com.booking.model.TimeSlot;
import com.booking.model.User;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the creation, modification, and retrieval of resource bookings.
 */
public class BookingService {

    private static final int MAX_DURATION_HOURS_STUDENT = 2;
    private static final int MAX_DURATION_HOURS_STAFF = 4;
    private static final int MAX_DURATION_HOURS_ADMIN = 8;

    private final BookingFileHandler bookingFileHandler;
    private final AuthService authService;
    private final ResourceManager<Resource> resourceManager;

    /**
     * Bookings keyed by {@link Booking#getBookingId()} for O(1) lookup by ID,
     * backed by a {@link LinkedHashMap} to preserve insertion order for
     * consistent display in the GUI's tables.
     */
    private final Map<String, Booking> bookingsById;

    /**
     * Constructs a booking service with the required dependencies.
     *
     * @param bookingFileHandler handler for persisting and loading booking data
     * @param authService        service for authentication and role checks
     * @param resourceManager    manager for campus resources
     */
    public BookingService(BookingFileHandler bookingFileHandler,
                          AuthService authService,
                          ResourceManager<Resource> resourceManager) {
        this.bookingFileHandler = bookingFileHandler;
        this.authService = authService;
        this.resourceManager = resourceManager;
        this.bookingsById = new LinkedHashMap<>();
        loadBookings();
    }

    /**
     * Creates a new booking for the specified user and resource.
     *
     * @param userId     identifier of the booking user
     * @param resourceId identifier of the resource to book
     * @param startTime  requested start time
     * @param endTime    requested end time
     * @param notes      optional booking notes
     * @return the newly created {@link Booking}
     * @throws ResourceUnavailableException      if the resource is unavailable or already booked
     * @throws InvalidBookingDurationException   if the booking time or duration is invalid
     * @throws UnauthorizedAccessException       if the user is not permitted to book the resource
     */
    public Booking createBooking(String userId, String resourceId,
                                 LocalDateTime startTime, LocalDateTime endTime,
                                 String notes)
            throws ResourceUnavailableException, InvalidBookingDurationException,
            UnauthorizedAccessException {
        User user = findUserById(userId);
        if (user == null) {
            throw new UnauthorizedAccessException("User not found: " + userId);
        }

        Resource resource = resourceManager.findById(resourceId);
        if (resource == null) {
            throw new ResourceUnavailableException("Resource not found.");
        }

        TimeSlot slot = new TimeSlot(startTime, endTime);

        validateRoleCanBookResource(user, resource);
        validateBookingDuration(user, slot);
        validateResourceAvailable(resource, slot, null);

        BookingStatus initialStatus = user.getRole() == Role.STUDENT
                ? BookingStatus.PENDING
                : BookingStatus.CONFIRMED;

        Booking booking = new Booking(
                generateBookingId(),
                userId,
                resourceId,
                startTime,
                endTime,
                initialStatus
        );

        bookingsById.put(booking.getBookingId(), booking);
        saveBookings();
        LogManager.getInstance().logInfo("User " + userId + " created booking "
                + booking.getBookingId() + " for resource " + resourceId
                + " [" + initialStatus + "]");
        return booking;
    }

    /**
     * Cancels an existing booking by its identifier.
     *
     * @param bookingId identifier of the booking to cancel
     * @throws UnauthorizedAccessException if the current user is not permitted to cancel
     */
    public void cancelBooking(String bookingId) throws UnauthorizedAccessException {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("You must be logged in to cancel a booking.");
        }

        if (!canManageBooking(currentUser, booking)) {
            throw new UnauthorizedAccessException("You are not permitted to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED
                || booking.getBookingStatus() == BookingStatus.REJECTED
                || booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Booking cannot be cancelled in its current state.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        saveBookings();
        LogManager.getInstance().logInfo("User " + currentUser.getUserId()
                + " cancelled booking " + booking.getBookingId());
    }

    /**
     * Approves a pending booking.
     *
     * @param bookingId identifier of the booking to approve
     * @throws UnauthorizedAccessException     if the current user is not staff or admin
     * @throws ResourceUnavailableException    if the resource is no longer available
     */
    public void approveBooking(String bookingId)
            throws UnauthorizedAccessException, ResourceUnavailableException {
        requireStaffOrAdmin();
        Booking booking = requirePendingBooking(bookingId);

        Resource resource = resourceManager.findById(booking.getResourceId());
        TimeSlot slot = new TimeSlot(booking.getStartTime(), booking.getEndTime());
        validateResourceAvailable(resource, slot, booking.getBookingId());

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        saveBookings();
        LogManager.getInstance().logInfo("Booking " + booking.getBookingId() + " approved by "
                + authService.getCurrentUser().getUserId());
    }

    /**
     * Rejects a pending booking.
     *
     * @param bookingId identifier of the booking to reject
     * @throws UnauthorizedAccessException if the current user is not staff or admin
     */
    public void rejectBooking(String bookingId) throws UnauthorizedAccessException {
        requireStaffOrAdmin();
        Booking booking = requirePendingBooking(bookingId);
        booking.setBookingStatus(BookingStatus.REJECTED);
        saveBookings();
        LogManager.getInstance().logInfo("Booking " + booking.getBookingId() + " rejected by "
                + authService.getCurrentUser().getUserId());
    }

    /**
     * Updates the status of an existing booking.
     *
     * @param bookingId identifier of the booking to update
     * @param status    new booking status
     * @throws UnauthorizedAccessException     if the current user is not permitted
     * @throws ResourceUnavailableException    if confirming would cause a conflict
     */
    public void updateBookingStatus(String bookingId, BookingStatus status)
            throws UnauthorizedAccessException, ResourceUnavailableException {
        if (status == BookingStatus.CONFIRMED) {
            approveBooking(bookingId);
            return;
        }
        if (status == BookingStatus.REJECTED) {
            rejectBooking(bookingId);
            return;
        }
        if (status == BookingStatus.CANCELLED) {
            Booking booking = findBookingById(bookingId);
            if (booking != null && booking.getBookingStatus() == BookingStatus.PENDING) {
                rejectBooking(bookingId);
            } else {
                cancelBooking(bookingId);
            }
            return;
        }

        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only administrators can set this booking status.");
        }

        if (status == BookingStatus.COMPLETED) {
            booking.setBookingStatus(BookingStatus.COMPLETED);
            saveBookings();
        }
    }

    /**
     * Returns the resource manager backing this booking service, so that other
     * layers (GUI controllers, {@link com.booking.controller.AppController}) can
     * share the same in-memory resource collection instead of creating a second one.
     *
     * @return the shared {@link ResourceManager}
     */
    public ResourceManager<Resource> getResourceManager() {
        return resourceManager;
    }

    /**
     * Returns all bookings currently managed by this service.
     *
     * @return list of all bookings
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookingsById.values());
    }

    /**
     * Returns bookings associated with a specific user.
     *
     * @param userId identifier of the user
     * @return list of bookings for the given user
     */
    public List<Booking> getBookingsByUser(String userId) {
        List<Booking> results = new ArrayList<>();
        if (userId == null) {
            return results;
        }

        for (Booking booking : bookingsById.values()) {
            if (userId.equals(booking.getUserId())) {
                results.add(booking);
            }
        }
        return results;
    }

    /**
     * Returns bookings associated with a specific resource.
     *
     * @param resourceId identifier of the resource
     * @return list of bookings for the given resource
     */
    public List<Booking> getBookingsByResource(String resourceId) {
        List<Booking> results = new ArrayList<>();
        if (resourceId == null) {
            return results;
        }

        for (Booking booking : bookingsById.values()) {
            if (resourceId.equals(booking.getResourceId())) {
                results.add(booking);
            }
        }
        return results;
    }

    /**
     * Loads booking data from persistent storage.
     */
    public void loadBookings() {
        bookingsById.clear();
        for (Booking booking : bookingFileHandler.load()) {
            bookingsById.put(booking.getBookingId(), booking);
        }
    }

    /**
     * Persists all booking data to storage.
     */
    public void saveBookings() {
        try {
            bookingFileHandler.save(new ArrayList<>(bookingsById.values()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save bookings.", e);
        }
    }

    private User findUserById(String userId) {
        if (userId == null) {
            return null;
        }

        for (User user : authService.getUsers()) {
            if (userId.equals(user.getUserId())) {
                return user;
            }
        }
        return null;
    }

    private Booking findBookingById(String bookingId) {
        if (bookingId == null) {
            return null;
        }
        return bookingsById.get(bookingId);
    }

    private String generateBookingId() {
        return "B-" + UUID.randomUUID();
    }

    private User requireStaffOrAdmin() throws UnauthorizedAccessException {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("You must be logged in to perform this action.");
        }
        if (currentUser.getRole() != Role.STAFF && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only staff or administrators can perform this action.");
        }
        return currentUser;
    }

    private Booking requirePendingBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending bookings can be approved or rejected.");
        }
        return booking;
    }

    private boolean canManageBooking(User currentUser, Booking booking) {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.STAFF) {
            return true;
        }
        return currentUser.getUserId().equals(booking.getUserId());
    }

    private void validateRoleCanBookResource(User user, Resource resource)
            throws UnauthorizedAccessException {
        if (user.getRole() == Role.STUDENT
                && resource.getResourceType() == ResourceType.EVENT_SPACE) {
            throw new UnauthorizedAccessException(
                    "Students are not permitted to book event spaces.");
        }
    }

    private void validateBookingDuration(User user, TimeSlot slot)
            throws InvalidBookingDurationException {
        if (slot.getStartTime() == null || slot.getEndTime() == null) {
            throw new InvalidBookingDurationException("Start time and end time are required.");
        }

        if (!slot.isValid()) {
            throw new InvalidBookingDurationException("End time must be after start time.");
        }

        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingDurationException("Bookings cannot be made in the past.");
        }

        long durationMinutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        long maxMinutes = getMaxDurationHours(user.getRole()) * 60L;
        if (durationMinutes > maxMinutes) {
            throw new InvalidBookingDurationException(
                    "Booking duration exceeds the maximum allowed for " + user.getRole() + " users.");
        }
    }

    private int getMaxDurationHours(Role role) {
        return switch (role) {
            case STUDENT -> MAX_DURATION_HOURS_STUDENT;
            case STAFF -> MAX_DURATION_HOURS_STAFF;
            case ADMIN -> MAX_DURATION_HOURS_ADMIN;
        };
    }

    private void validateResourceAvailable(Resource resource, TimeSlot slot, String excludeBookingId)
            throws ResourceUnavailableException {
        if (resource == null) {
            throw new ResourceUnavailableException("Resource not found.");
        }

        if (!resource.checkAvailability(slot)) {
            if (resource.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
                throw new ResourceUnavailableException(
                        "Resource is not available: " + resource.getName());
            }
            throw new ResourceUnavailableException("Requested time slot is invalid.");
        }

        for (Booking existing : bookingsById.values()) {
            if (excludeBookingId != null && excludeBookingId.equals(existing.getBookingId())) {
                continue;
            }
            if (!resource.getResourceId().equals(existing.getResourceId())) {
                continue;
            }
            if (existing.getBookingStatus() == BookingStatus.CANCELLED
                    || existing.getBookingStatus() == BookingStatus.REJECTED
                    || existing.getBookingStatus() == BookingStatus.COMPLETED) {
                continue;
            }
            TimeSlot existingSlot = new TimeSlot(existing.getStartTime(), existing.getEndTime());
            if (slot.overlaps(existingSlot)) {
                throw new ResourceUnavailableException(
                        "Resource is already booked for the requested time slot.");
            }
        }
    }
}
