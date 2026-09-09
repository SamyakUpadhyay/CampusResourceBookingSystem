package com.booking.exception;

/**
 * Thrown when a user attempts an action they are not authorised to perform.
 */
public class UnauthorizedAccessException extends BookingException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new unauthorized access exception.
     *
     * @param message descriptive error message
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new unauthorized access exception with a cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying cause of this exception
     */
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
