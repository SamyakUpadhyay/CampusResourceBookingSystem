package com.booking.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract base class representing a system user.
 * Subclasses define role-specific attributes and behaviour.
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private boolean active = true;

    /**
     * Constructs a new user with the given credentials and profile details.
     *
     * @param userId       unique identifier for the user
     * @param username     login username
     * @param passwordHash encoded password value
     * @param fullName     display name of the user
     * @param email        contact email address
     */
    public User(String userId, String username, String passwordHash, String fullName, String email) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
    }

    /**
     * Returns the role assigned to this user.
     *
     * @return the user's {@link Role}
     */
    public abstract Role getRole();

    /**
     * Returns the set of fine-grained actions this user is permitted to
     * perform. Subclasses override this to provide role-specific
     * capabilities (see {@link Permission}); this is the polymorphic
     * counterpart to {@link #getRole()} used for capability-based checks.
     *
     * @return an immutable set of permissions granted to this user
     */
    public abstract Set<Permission> getPermissions();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns whether this account is currently enabled. Disabled accounts
     * cannot log in.
     *
     * @return {@code true} if the account is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Enables or disables this account.
     *
     * @param active {@code true} to enable the account, {@code false} to disable it
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "userId='" + userId + '\''
                + ", username='" + username + '\''
                + ", fullName='" + fullName + '\''
                + ", email='" + email + '\''
                + ", role=" + getRole()
                + ", active=" + active
                + '}';
    }
}
