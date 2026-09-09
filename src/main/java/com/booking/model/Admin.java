package com.booking.model;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an administrator with full system management access.
 */
public class Admin extends User {

    private static final long serialVersionUID = 1L;

    private String adminId;

    /**
     * Constructs a new administrator user.
     *
     * @param userId       unique identifier for the user
     * @param username     login username
     * @param passwordHash encoded password value
     * @param fullName     display name of the administrator
     * @param email        contact email address
     * @param adminId      institutional administrator identifier
     */
    public Admin(String userId, String username, String passwordHash, String fullName,
                 String email, String adminId) {
        super(userId, username, passwordHash, fullName, email);
        this.adminId = adminId;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.copyOf(EnumSet.allOf(Permission.class));
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Admin admin = (Admin) o;
        return Objects.equals(adminId, admin.adminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminId);
    }

    @Override
    public String toString() {
        return "Admin{"
                + "userId='" + getUserId() + '\''
                + ", username='" + getUsername() + '\''
                + ", fullName='" + getFullName() + '\''
                + ", email='" + getEmail() + '\''
                + ", role=" + getRole()
                + ", adminId='" + adminId + '\''
                + '}';
    }
}
