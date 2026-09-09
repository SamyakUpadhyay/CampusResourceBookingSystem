package com.booking.model;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a student user who can book campus resources.
 */
public class Student extends User {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String department;

    /**
     * Constructs a new student user.
     *
     * @param userId       unique identifier for the user
     * @param username     login username
     * @param passwordHash encoded password value
     * @param fullName     display name of the student
     * @param email        contact email address
     * @param studentId    institutional student identifier
     * @param department   academic department
     */
    public Student(String userId, String username, String passwordHash, String fullName,
                   String email, String studentId, String department) {
        super(userId, username, passwordHash, fullName, email);
        this.studentId = studentId;
        this.department = department;
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.copyOf(EnumSet.of(Permission.VIEW, Permission.BOOK_OWN, Permission.CANCEL_OWN));
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId)
                && Objects.equals(department, student.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, department);
    }

    @Override
    public String toString() {
        return "Student{"
                + "userId='" + getUserId() + '\''
                + ", username='" + getUsername() + '\''
                + ", fullName='" + getFullName() + '\''
                + ", email='" + getEmail() + '\''
                + ", role=" + getRole()
                + ", studentId='" + studentId + '\''
                + ", department='" + department + '\''
                + '}';
    }
}
