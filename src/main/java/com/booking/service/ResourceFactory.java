package com.booking.service;

import com.booking.model.AvailabilityStatus;
import com.booking.model.EventSpace;
import com.booking.model.LabEquipment;
import com.booking.model.Resource;
import com.booking.model.ResourceType;
import com.booking.model.StudyRoom;

/**
 * Factory for creating concrete {@link Resource} instances by type.
 * Centralising creation here means adding a new resource type only requires
 * extending this factory rather than touching every place resources are built.
 */
public class ResourceFactory {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ResourceFactory() {
        // Utility class — no instances permitted.
    }

    /**
     * Creates a resource instance based on the specified type, applying sensible
     * type-specific defaults for attributes not captured by the common fields.
     *
     * @param type               the {@link ResourceType} to instantiate
     * @param resourceId         unique identifier for the resource
     * @param name               display name
     * @param location           physical location
     * @param capacity           general capacity
     * @param availabilityStatus current availability state
     * @return a new {@link Resource} instance of the appropriate subtype
     */
    public static Resource createResource(ResourceType type, String resourceId, String name,
                                          String location, int capacity,
                                          AvailabilityStatus availabilityStatus) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type must not be null");
        }

        return switch (type) {
            case STUDY_ROOM -> createStudyRoom(resourceId, name, location, capacity,
                    availabilityStatus, resourceId, false, false);
            case LAB_EQUIPMENT -> createLabEquipment(resourceId, name, location, capacity,
                    availabilityStatus, "General", 1);
            case EVENT_SPACE -> createEventSpace(resourceId, name, location, capacity,
                    availabilityStatus, capacity, false);
        };
    }

    /**
     * Creates a new {@link StudyRoom} instance.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name
     * @param location           physical location
     * @param capacity           maximum occupants
     * @param availabilityStatus current availability state
     * @param roomNumber         room number or label
     * @param hasProjector       whether a projector is available
     * @param hasWhiteboard      whether a whiteboard is available
     * @return a new {@link StudyRoom}
     */
    public static StudyRoom createStudyRoom(String resourceId, String name, String location,
                                            int capacity, AvailabilityStatus availabilityStatus,
                                            String roomNumber, boolean hasProjector,
                                            boolean hasWhiteboard) {
        return new StudyRoom(resourceId, name, location, capacity, availabilityStatus,
                roomNumber, hasProjector, hasWhiteboard);
    }

    /**
     * Creates a new {@link LabEquipment} instance.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name
     * @param location           lab location
     * @param capacity           usage capacity
     * @param availabilityStatus current availability state
     * @param equipmentType      category of equipment
     * @param quantity           number of units available
     * @return a new {@link LabEquipment}
     */
    public static LabEquipment createLabEquipment(String resourceId, String name, String location,
                                                  int capacity, AvailabilityStatus availabilityStatus,
                                                  String equipmentType, int quantity) {
        return new LabEquipment(resourceId, name, location, capacity, availabilityStatus,
                equipmentType, quantity);
    }

    /**
     * Creates a new {@link EventSpace} instance.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name
     * @param location           venue location
     * @param capacity           general capacity
     * @param availabilityStatus current availability state
     * @param maxEventCapacity   maximum event attendees
     * @param hasAVEquipment     whether AV equipment is provided
     * @return a new {@link EventSpace}
     */
    public static EventSpace createEventSpace(String resourceId, String name, String location,
                                              int capacity, AvailabilityStatus availabilityStatus,
                                              int maxEventCapacity, boolean hasAVEquipment) {
        return new EventSpace(resourceId, name, location, capacity, availabilityStatus,
                maxEventCapacity, hasAVEquipment);
    }
}
