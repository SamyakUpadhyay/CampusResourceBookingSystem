package com.booking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a reservation of a campus resource by a user.
 */
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bookingId;
    private String userId;
    private String resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus bookingStatus;

    /**
     * Constructs a new booking record.
     *
     * @param bookingId     unique identifier for the booking
     * @param userId        identifier of the user who made the booking
     * @param resourceId    identifier of the booked resource
     * @param startTime     scheduled start date and time
     * @param endTime       scheduled end date and time
     * @param bookingStatus current booking status
     */
    public Booking(String bookingId, String userId, String resourceId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId)
                && Objects.equals(userId, booking.userId)
                && Objects.equals(resourceId, booking.resourceId)
                && Objects.equals(startTime, booking.startTime)
                && Objects.equals(endTime, booking.endTime)
                && bookingStatus == booking.bookingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, userId, resourceId, startTime, endTime, bookingStatus);
    }

    @Override
    public String toString() {
        return "Booking{"
                + "bookingId='" + bookingId + '\''
                + ", userId='" + userId + '\''
                + ", resourceId='" + resourceId + '\''
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", bookingStatus=" + bookingStatus
                + '}';
    }
}
