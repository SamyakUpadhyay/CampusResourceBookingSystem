package com.booking.model;

/**
 * Represents the lifecycle state of a booking.
 */
public enum BookingStatus {
    /** Booking request submitted and awaiting confirmation. */
    PENDING,
    /** Booking has been confirmed and is active. */
    CONFIRMED,
    /** Booking request was declined by staff or an administrator. */
    REJECTED,
    /** Booking was cancelled by the user or an administrator. */
    CANCELLED,
    /** Booking period has elapsed successfully. */
    COMPLETED
}
