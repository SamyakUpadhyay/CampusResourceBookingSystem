package com.booking.exception;

/**
 * Base checked exception for all booking-related errors in CRSBS.
 */
public class BookingException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new booking exception with the specified detail message.
     *
     * @param message descriptive error message
     */
    public BookingException(String message) {
        super(message);
    }

    /**
     * Constructs a new booking exception with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying cause of this exception
     */
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
