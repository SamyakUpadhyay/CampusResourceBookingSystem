package com.booking.model;

/**
 * Fine-grained actions a {@link User} may be permitted to perform.
 * Exposed via {@link User#getPermissions()} so role-based checks can be
 * expressed in terms of capabilities rather than scattered role comparisons.
 */
public enum Permission {
    /** View the resource catalogue and resource availability. */
    VIEW,
    /** Create a booking for oneself. */
    BOOK_OWN,
    /** Cancel a booking that belongs to oneself. */
    CANCEL_OWN,
    /** Approve or reject a pending booking made by another user. */
    APPROVE_OWN,
    /** Cancel or otherwise modify any user's booking. */
    MODIFY_OWN,
    /** Create, update, or delete campus resources. */
    MANAGE_RESOURCES,
    /** Manage user accounts (enable/disable, reassign roles). */
    MANAGE_USERS,
    /** View the system audit log. */
    VIEW_LOGS
}
