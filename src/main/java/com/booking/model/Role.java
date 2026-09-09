package com.booking.model;

/**
 * Defines the access roles supported by the Campus Resource &amp; Study Space Booking System.
 */
public enum Role {
    /** Standard student user with booking privileges. */
    STUDENT,
    /** Staff member with extended booking and management privileges. */
    STAFF,
    /** Administrator with full system access. */
    ADMIN
}
