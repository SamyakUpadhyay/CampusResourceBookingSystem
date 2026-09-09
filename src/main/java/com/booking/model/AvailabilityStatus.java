package com.booking.model;

/**
 * Indicates whether a resource can currently be booked.
 */
public enum AvailabilityStatus {
    /** Resource is open for new bookings. */
    AVAILABLE,
    /** Resource cannot be booked at this time. */
    UNAVAILABLE,
    /** Resource is temporarily offline for maintenance. */
    MAINTENANCE
}
