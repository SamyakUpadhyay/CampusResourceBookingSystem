package com.booking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a contiguous window of time requested for a booking.
 * Used by {@link Resource#checkAvailability(TimeSlot)} and by
 * {@link com.booking.service.BookingService} to detect scheduling conflicts.
 */
public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Constructs a new time slot.
     *
     * @param startTime start of the window
     * @param endTime   end of the window
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Checks whether this slot is internally well-formed, i.e. both
     * endpoints are present and the end time is strictly after the start time.
     *
     * @return {@code true} if this is a valid, non-empty time window
     */
    public boolean isValid() {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }

    /**
     * Checks whether this slot overlaps another slot. Two slots that only
     * touch at a boundary (one ends exactly when the other starts) are not
     * considered overlapping.
     *
     * @param other the other time slot to compare against
     * @return {@code true} if the two windows share any point in time
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null || !isValid() || !other.isValid()) {
            return false;
        }
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(startTime, timeSlot.startTime) && Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return "TimeSlot{" + startTime + " -> " + endTime + '}';
    }
}
