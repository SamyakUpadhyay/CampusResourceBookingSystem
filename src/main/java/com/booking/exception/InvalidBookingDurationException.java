package com.booking.exception;

/**
 * Thrown when a booking duration violates system constraints.
 */
public class InvalidBookingDurationException extends BookingException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new invalid booking duration exception.
     *
     * @param message descriptive error message
     */
    public InvalidBookingDurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new invalid booking duration exception with a cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying cause of this exception
     */
    public InvalidBookingDurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
