package com.booking.model;

import java.util.Objects;

/**
 * Represents a bookable study room on campus.
 */
public class StudyRoom extends Resource {

    private static final long serialVersionUID = 1L;

    private String roomNumber;
    private boolean hasProjector;
    private boolean hasWhiteboard;

    /**
     * Constructs a new study room resource.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name of the study room
     * @param location           building or area location
     * @param capacity           maximum number of occupants
     * @param availabilityStatus current availability state
     * @param roomNumber         room number or label
     * @param hasProjector       whether a projector is available
     * @param hasWhiteboard      whether a whiteboard is available
     */
    public StudyRoom(String resourceId, String name, String location, int capacity,
                     AvailabilityStatus availabilityStatus, String roomNumber,
                     boolean hasProjector, boolean hasWhiteboard) {
        super(resourceId, name, location, capacity, availabilityStatus);
        this.roomNumber = roomNumber;
        this.hasProjector = hasProjector;
        this.hasWhiteboard = hasWhiteboard;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDY_ROOM;
    }

    @Override
    public String getBookingRules() {
        return "Study rooms may be booked up to the max session length for the requesting "
                + "user's role; " + (hasProjector ? "projector" : "no projector")
                + " and " + (hasWhiteboard ? "whiteboard" : "no whiteboard") + " included.";
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean hasWhiteboard() {
        return hasWhiteboard;
    }

    public void setHasWhiteboard(boolean hasWhiteboard) {
        this.hasWhiteboard = hasWhiteboard;
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
        StudyRoom studyRoom = (StudyRoom) o;
        return hasProjector == studyRoom.hasProjector
                && hasWhiteboard == studyRoom.hasWhiteboard
                && Objects.equals(roomNumber, studyRoom.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomNumber, hasProjector, hasWhiteboard);
    }

    @Override
    public String toString() {
        return "StudyRoom{"
                + "resourceId='" + getResourceId() + '\''
                + ", name='" + getName() + '\''
                + ", location='" + getLocation() + '\''
                + ", capacity=" + getCapacity()
                + ", availabilityStatus=" + getAvailabilityStatus()
                + ", resourceType=" + getResourceType()
                + ", roomNumber='" + roomNumber + '\''
                + ", hasProjector=" + hasProjector
                + ", hasWhiteboard=" + hasWhiteboard
                + '}';
    }
}
