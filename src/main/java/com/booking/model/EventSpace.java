package com.booking.model;

import java.util.Objects;

/**
 * Represents an event space suitable for group activities and gatherings.
 */
public class EventSpace extends Resource {

    private static final long serialVersionUID = 1L;

    private int maxEventCapacity;
    private boolean hasAVEquipment;

    /**
     * Constructs a new event space resource.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name of the event space
     * @param location           building or venue location
     * @param capacity           general capacity rating
     * @param availabilityStatus current availability state
     * @param maxEventCapacity   maximum attendees for events
     * @param hasAVEquipment     whether audio-visual equipment is provided
     */
    public EventSpace(String resourceId, String name, String location, int capacity,
                      AvailabilityStatus availabilityStatus, int maxEventCapacity,
                      boolean hasAVEquipment) {
        super(resourceId, name, location, capacity, availabilityStatus);
        this.maxEventCapacity = maxEventCapacity;
        this.hasAVEquipment = hasAVEquipment;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.EVENT_SPACE;
    }

    @Override
    public String getBookingRules() {
        return "Event spaces require staff or admin approval; attendance may not exceed "
                + maxEventCapacity + (hasAVEquipment ? "; AV equipment provided." : "; no AV equipment provided.");
    }

    public int getMaxEventCapacity() {
        return maxEventCapacity;
    }

    public void setMaxEventCapacity(int maxEventCapacity) {
        this.maxEventCapacity = maxEventCapacity;
    }

    public boolean hasAVEquipment() {
        return hasAVEquipment;
    }

    public void setHasAVEquipment(boolean hasAVEquipment) {
        this.hasAVEquipment = hasAVEquipment;
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
        EventSpace eventSpace = (EventSpace) o;
        return maxEventCapacity == eventSpace.maxEventCapacity
                && hasAVEquipment == eventSpace.hasAVEquipment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxEventCapacity, hasAVEquipment);
    }

    @Override
    public String toString() {
        return "EventSpace{"
                + "resourceId='" + getResourceId() + '\''
                + ", name='" + getName() + '\''
                + ", location='" + getLocation() + '\''
                + ", capacity=" + getCapacity()
                + ", availabilityStatus=" + getAvailabilityStatus()
                + ", resourceType=" + getResourceType()
                + ", maxEventCapacity=" + maxEventCapacity
                + ", hasAVEquipment=" + hasAVEquipment
                + '}';
    }
}
