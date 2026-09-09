package com.booking.exception;

/**
 * Thrown when a requested resource is not available for booking.
 */
public class ResourceUnavailableException extends BookingException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new resource unavailable exception.
     *
     * @param message descriptive error message
     */
    public ResourceUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new resource unavailable exception with a cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying cause of this exception
     */
    public ResourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
