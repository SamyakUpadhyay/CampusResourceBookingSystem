package com.booking.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for all bookable campus resources.
 */
public abstract class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceId;
    private String name;
    private String location;
    private int capacity;
    private AvailabilityStatus availabilityStatus;

    /**
     * Constructs a new campus resource.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name of the resource
     * @param location           physical location on campus
     * @param capacity           maximum occupancy or usage capacity
     * @param availabilityStatus current availability state
     */
    public Resource(String resourceId, String name, String location, int capacity,
                    AvailabilityStatus availabilityStatus) {
        this.resourceId = resourceId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
    }

    /**
     * Returns the type classification of this resource.
     *
     * @return the {@link ResourceType} of this resource
     */
    public abstract ResourceType getResourceType();

    /**
     * Returns a short, human-readable description of the type-specific
     * booking rules that apply to this resource (e.g. equipment pickup
     * rules, event-space approval requirements).
     *
     * @return a description of this resource's booking rules
     */
    public abstract String getBookingRules();

    /**
     * Performs a resource-level availability check for the given slot,
     * i.e. whether this resource's own {@link AvailabilityStatus} allows a
     * booking at all and the slot itself is well-formed. This does
     * <em>not</em> check for conflicts against other bookings — that
     * cross-booking conflict detection is the responsibility of
     * {@link com.booking.service.BookingService}, which has visibility
     * into the full set of existing bookings for this resource.
     *
     * @param slot the requested time slot
     * @return {@code true} if this resource is open for bookings and the slot is valid
     */
    public boolean checkAvailability(TimeSlot slot) {
        return slot != null && slot.isValid() && availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resource resource = (Resource) o;
        return Objects.equals(resourceId, resource.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "resourceId='" + resourceId + '\''
                + ", name='" + name + '\''
                + ", location='" + location + '\''
                + ", capacity=" + capacity
                + ", availabilityStatus=" + availabilityStatus
                + ", resourceType=" + getResourceType()
                + '}';
    }
}
