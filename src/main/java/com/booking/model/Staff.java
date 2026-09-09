package com.booking.model;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a staff member with extended booking privileges.
 */
public class Staff extends User {

    private static final long serialVersionUID = 1L;

    private String staffId;
    private String department;

    /**
     * Constructs a new staff user.
     *
     * @param userId       unique identifier for the user
     * @param username     login username
     * @param passwordHash encoded password value
     * @param fullName     display name of the staff member
     * @param email        contact email address
     * @param staffId      institutional staff identifier
     * @param department   department of employment
     */
    public Staff(String userId, String username, String passwordHash, String fullName,
                 String email, String staffId, String department) {
        super(userId, username, passwordHash, fullName, email);
        this.staffId = staffId;
        this.department = department;
    }

    @Override
    public Role getRole() {
        return Role.STAFF;
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.copyOf(EnumSet.of(Permission.VIEW, Permission.BOOK_OWN, Permission.CANCEL_OWN,
                Permission.APPROVE_OWN, Permission.MODIFY_OWN));
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
        Staff staff = (Staff) o;
        return Objects.equals(staffId, staff.staffId)
                && Objects.equals(department, staff.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), staffId, department);
    }

    @Override
    public String toString() {
        return "Staff{"
                + "userId='" + getUserId() + '\''
                + ", username='" + getUsername() + '\''
                + ", fullName='" + getFullName() + '\''
                + ", email='" + getEmail() + '\''
                + ", role=" + getRole()
                + ", staffId='" + staffId + '\''
                + ", department='" + department + '\''
                + '}';
    }
}
